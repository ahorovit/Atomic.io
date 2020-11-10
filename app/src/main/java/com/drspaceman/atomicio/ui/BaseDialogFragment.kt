package com.drspaceman.atomicio.ui

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.viewmodel.BaseViewModel

import kotlinx.android.synthetic.main.details_dialog.*

abstract class BaseDialogFragment : DialogFragment() {

    protected abstract val layoutId: Int

    protected abstract val itemId: Long?

    protected abstract val viewModel: BaseViewModel

    // @todo: make lateinit instead of nullable
    protected abstract val itemViewData: BaseViewModel.BaseViewData

    protected lateinit var parentActivity: AppCompatActivity

    protected abstract fun setObservers()

    // @todo: pull generic implementation down into base class
    protected abstract fun loadExistingItem(id: Long)

    protected abstract fun populateExistingValues()

    protected abstract fun saveItemDetails()

    // @todo: pull generic implementation into base class
    protected abstract fun getNewItem()

    protected abstract fun setSpinnerSelection()

    protected abstract fun populateTypeSpinner()

//    override fun getTheme(): Int {
//        return R.style.AppTheme
//    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setObservers()
        return inflater.inflate(R.layout.details_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDialogToolBar()

        viewStub.layoutResource = layoutId
        viewStub.inflate()

        populateTypeSpinner()
        loadDataItem()
    }

    fun setupDialogToolBar() {
        dialogToolBar.inflateMenu(R.menu.dialog_menu)
        dialogToolBar.setNavigationIcon(R.drawable.ic_back)
        dialogToolBar.setNavigationOnClickListener {
            dismiss()
        }

        dialogToolBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.saveButton -> {
                    saveItemDetails()
                    true
                }
                R.id.deleteButton -> {
                    deleteSelectedItem()
                    true
                }
                else -> false
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentActivity = context as AppCompatActivity
    }

    override fun onStart() {
        super.onStart()

        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            it.window?.setLayout(width, height)
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        viewModel.clearItem()
        super.onCancel(dialog)
    }

    override fun dismiss() {
        viewModel.clearItem()
        super.dismiss()
    }

    // @todo: move conditional logic into viewModel
    protected open fun loadDataItem() {
        itemId?.let {
            loadExistingItem(it)
        } ?: run {
            getNewItem()
            setSpinnerSelection()
        }
    }

    protected open fun deleteSelectedItem() {
        viewModel.deleteItem(itemViewData)
        dismiss()
    }

    interface SpinnerItemViewData {
        var id: Long?
        var type: String?
        var typeResourceId: Int
    }
}
