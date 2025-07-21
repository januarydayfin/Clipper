package com.krayapp.buffercompanion.bargen.ui.fragments

import android.Manifest
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.gun0912.tedpermission.normal.TedPermission
import com.krayapp.buffercompanion.bargen.BargenViewModel
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.addTextWatcher
import com.krayapp.buffercompanion.bargen.databinding.FragmentMainBinding
import com.krayapp.buffercompanion.bargen.hideKeyboard
import com.krayapp.buffercompanion.bargen.onImeAction
import com.krayapp.buffercompanion.bargen.ui.MainActivity
import com.krayapp.buffercompanion.bargen.ui.adapter.BarcodeAdapter
import com.krayapp.buffercompanion.bargen.ui.bottomsheets.CreateBarcodeBottomsheet
import com.krayapp.buffercompanion.bargen.ui.bottomsheets.TagsBottomsheet
import com.krayapp.buffercompanion.bargen.ui.dialogs.ScanDialog
import com.krayapp.buffercompanion.bargen.ui.dialogs.SettingsDialog
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.utils.addPermissionListener
import com.krayapp.buffercompanion.bargen.utils.attachHidingWithRecycler
import com.krayapp.buffercompanion.bargen.utils.disableAnimation
import com.krayapp.buffercompanion.bargen.utils.enableAnimation
import com.krayapp.buffercompanion.bargen.utils.filterChip
import com.krayapp.buffercompanion.bargen.utils.navbarHeight
import com.krayapp.buffercompanion.bargen.utils.runOnUi
import com.krayapp.buffercompanion.bargen.utils.showBarcodeMenu
import com.krayapp.buffercompanion.bargen.utils.showDeleteConfirmationDialog
import com.krayapp.buffercompanion.bargen.utils.showSortMenu
import com.krayapp.buffercompanion.bargen.utils.toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

class MainFragment : Fragment() {
    private var vb: FragmentMainBinding? = null

    private var barcodeAdapter: BarcodeAdapter? = null
    private val viewmodel: BargenViewModel by viewModels()

    private val backDispatcher =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                toolbarMode(false)
                barcodeAdapter?.selectionModeOff()
                vb?.recycler?.enableAnimation(lifecycleScope)
                viewmodel.clearTagFilter()

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
        attachHidingButtons()
        observeDataFlow()
        setupSortPopup()
        setupToolbar()
    }

    private fun setupSortPopup() {
        vb?.run {
            sortDirection.setOnClickListener { v ->
                v.showSortMenu { viewmodel.changeSort(it) }
            }
        }
    }

    private fun setupToolbar() {
        vb?.run {
            selectAll.setOnClickListener {
                barcodeAdapter?.selectAll()
            }

            settings.setOnClickListener {
                showSettingsDialog()
            }

            cancel.setOnClickListener {
                backDispatcher.handleOnBackPressed()
            }
            delete.setOnClickListener {
                it.context.showDeleteConfirmationDialog {
                    viewmodel.removeBarcodes(barcodeAdapter?.getListIdsForDelete() ?: emptyList())
                    backDispatcher.handleOnBackPressed()
                }
            }

            with(searchView.searchEditText) {
                addTextWatcher { viewmodel.updateNameFilter(it) }
                onImeAction { v ->
                    hideKeyboard()
                    v.clearFocus()
                }
            }
        }
    }

    private fun toolbarMode(chooseMode: Boolean) {
        vb?.run {
            mainToolbar.isVisible = !chooseMode
            selectionToolbar.isVisible = chooseMode
        }
    }

    private fun showSettingsDialog() {
        SettingsDialog().show(childFragmentManager, "")
    }

    private fun checkAppShortcut() {
        val isCalledFromShortcut = (activity as? MainActivity)?.calledFromShortcut() == true

        if (isCalledFromShortcut)
            pasteFromClip()
    }

    private fun attachHidingButtons() {
        vb?.run {
            navSpace.layoutParams = LinearLayout.LayoutParams(0, activity.navbarHeight)
            buttonGroup.attachHidingWithRecycler(recycler, false)
        }
    }

    private fun initAdapter() {
        barcodeAdapter = BarcodeAdapter(
            openBarcode = ::showBarcodeInfo,
            openContextMenu = { model, anchor ->
                anchor.showBarcodeMenu(
                    uiModel = model,
                    startEdit = { startCreatingCustomBarcode(model) },
                    chooseMode = {
                        vb?.recycler?.disableAnimation()
                        barcodeAdapter?.selectionModeOn()
                        toolbarMode(true)
                        addBackCallback()
                    },
                    callDeleteDialog = {
                        anchor.context.showDeleteConfirmationDialog {
                            viewmodel.removeBarcode(model.id)
                        }
                    }
                )
            }
        )

        vb?.recycler?.let {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = barcodeAdapter
        }
    }

    private fun observeDataFlow() {
        runOnUi {
            viewmodel.barcodeFlow.collectLatest {
                barcodeAdapter?.updateData(it)
            }
        }

        runOnUi {
            viewmodel.tagFilterFlow.collectLatest {
                vb?.run {
                    chipGroup.removeAllViews()

                    if (it.isNotEmpty())
                        addBackCallback()

                    it.forEach { rawModel ->
                        val uiModel = rawModel.copy(checked = true)
                        val chip = chipGroup.context.filterChip(uiModel).apply {
                            setOnClickListener {
                                viewmodel.removeChipFromFilter(uiModel.id)
                            }
                        }
                        chipGroup.addView(chip)
                    }
                }
            }
        }
    }

    private fun addBackCallback() {
        activity?.onBackPressedDispatcher?.addCallback(backDispatcher)
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

    private fun startCreatingCustomBarcode(uiModel: BarcodeUiModel? = null) {
        CreateBarcodeBottomsheet(uiModel, saveBarcodeAndTags = { barcode, tags ->
            viewmodel.recordTags(tags)
            viewmodel.createBarcodeRecord(barcode)
        }, tagFounder = { name ->
            viewmodel.findTagWithName(name)
        }).show(childFragmentManager, "")
    }

    private fun startTagBottomSheet() {
        TagsBottomsheet(viewmodel).show(childFragmentManager, "")
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
                startTagBottomSheet()
            }
        }
    }

    private fun showBarcodeInfo(uiModel: BarcodeUiModel) {
        startCreatingCustomBarcode(uiModel)
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