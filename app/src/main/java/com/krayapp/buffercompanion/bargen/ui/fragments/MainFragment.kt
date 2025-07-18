package com.krayapp.buffercompanion.bargen.ui.fragments

import android.Manifest
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.gun0912.tedpermission.normal.TedPermission
import com.krayapp.buffercompanion.bargen.BargenViewModel
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.databinding.FragmentMainBinding
import com.krayapp.buffercompanion.bargen.ui.MainActivity
import com.krayapp.buffercompanion.bargen.ui.adapter.BarcodeAdapter
import com.krayapp.buffercompanion.bargen.ui.bottomsheets.CreateBarcodeBottomsheet
import com.krayapp.buffercompanion.bargen.ui.dialogs.BarcodeDialog
import com.krayapp.buffercompanion.bargen.ui.dialogs.ScanDialog
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.ui.models.TagUiModel
import com.krayapp.buffercompanion.bargen.utils.addPermissionListener
import com.krayapp.buffercompanion.bargen.utils.filterChip
import com.krayapp.buffercompanion.bargen.utils.runOnUi
import com.krayapp.buffercompanion.bargen.utils.toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    private var vb: FragmentMainBinding? = null

    private var barcodeAdapter: BarcodeAdapter? = null
    private val viewmodel: BargenViewModel by viewModels()

    private val backDispatcher =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                barcodeAdapter?.selectionModeOff()
                this.remove()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vb = FragmentMainBinding.inflate(inflater)
        return vb?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(requireContext().getColor(R.color.md_theme_surface))
        checkAppShortcut()
        initClick()
        initAdapter()
        observeDataFlow()
    }

    private fun checkAppShortcut() {
        val isCalledFromShortcut = (activity as? MainActivity)?.calledFromShortcut() == true

        if (isCalledFromShortcut)
            pasteFromClip()
    }

    private fun initAdapter() {
        barcodeAdapter = BarcodeAdapter(
            onSelectionStarted = {
                activity?.onBackPressedDispatcher?.addCallback(backDispatcher)
            },
            openBarcode = ::showBarcodeInfo,
            openContextMenu = { x, y, v ->

            }
        )

        vb?.recycler?.let {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = barcodeAdapter
        }
    }

    private fun observeDataFlow() {
        runOnUi {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewmodel.barcodeFlow.collectLatest {
                        barcodeAdapter?.updateData(it)
                    }
                }
                launch {
                    viewmodel.tagsFlow.collectLatest {
                        //todo вставялем в чипгруп и меняем на чекнутые
                    }
                }
            }
        }
        viewmodel.updateBarcodeFlow()
    }

    private fun startScannerDialog() {
        TedPermission
            .create()
            .addPermissionListener(onGranted = {
                ScanDialog { result ->
                    if (result != null)
                        viewmodel.createBarcodeRecord(
                            text = result.text,
                            format = result.barcodeFormat
                        )
                }.show(childFragmentManager, "")
            }, onDenied = { _ ->
                toast(R.string.camera_required)
            })
            .setPermissions(Manifest.permission.CAMERA)
            .check()
    }

    private fun startCreatingCustomBarcode() {
        CreateBarcodeBottomsheet { barcode, tags ->
            viewmodel.createBarcodeRecord(barcode)
            viewmodel.recordTags(tags)
        }.show(childFragmentManager, "")
    }


    private fun initClick() {
        vb?.run {
            scanBarcode.setOnClickListener {
                startScannerDialog()
            }
            createNew.setOnClickListener {
                startCreatingCustomBarcode()
            }

            tags.setOnClickListener {
                //todo меню тегов
            }
        }
    }

    private fun showBarcodeInfo(uiModel: BarcodeUiModel) {
        BarcodeDialog(uiModel).show(childFragmentManager, "")
        viewmodel.incrementUsageCount(uiModel.id)
    }

    private fun pasteFromClip() {
        runOnUi {
            delay(500) //нужно чтобы менеджер раздуплился, ибо null
            val manager =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val text = manager.primaryClip?.getItemAt(0)?.text.toString()

            if (text.isNotEmpty())
                viewmodel.createBarcodeRecord(text) {
                    showBarcodeInfo(it)
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vb = null
    }
}