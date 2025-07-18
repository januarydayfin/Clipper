package com.krayapp.buffercompanion.ui.fragments

import android.Manifest
import android.content.ClipboardManager
import android.content.Context
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
import com.krayapp.buffercompanion.BargenViewModel
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.databinding.FragmentMainBinding
import com.krayapp.buffercompanion.ui.adapter.BarcodeAdapter
import com.krayapp.buffercompanion.ui.bottomsheets.CreateBarcodeBottomsheet
import com.krayapp.buffercompanion.ui.dialogs.ScanDialog
import com.krayapp.buffercompanion.utils.addPermissionListener
import com.krayapp.buffercompanion.utils.runOnUi
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
        initClick()
        initAdapter()
        observeDataFlow()
    }


    private fun initAdapter() {
        barcodeAdapter = BarcodeAdapter {
            activity?.onBackPressedDispatcher?.addCallback(backDispatcher)
        }

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
    }

    private fun startScannerDialog() {
        TedPermission
            .create()
            .addPermissionListener(onGranted = {
                ScanDialog {
                    //todo запись в бд и обновление списка
                }.show(childFragmentManager, "")
            }, onDenied = { _ ->
            })
            .setPermissions(Manifest.permission.CAMERA)
            .check()
    }

    private fun startCreatingCustomBarcode() {
        CreateBarcodeBottomsheet().show(childFragmentManager, "")
    }


    private fun initClick() {
        vb?.run {
            startScanner.setOnClickListener {
                startScannerDialog()
            }
            createCustom.setOnClickListener {
                startCreatingCustomBarcode()
            }
        }
    }

    private fun pasteFromClip() {
        val manager =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val textFromClip = manager.primaryClip?.getItemAt(0)?.text.toString()

        if (textFromClip.isNotEmpty() && textFromClip != "null") {
            //todo идем создавать qr код из текста
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        vb = null
    }
}