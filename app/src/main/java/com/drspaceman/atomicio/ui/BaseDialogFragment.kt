package com.drspaceman.atomicio.ui

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.viewmodel.BaseDetailsViewModel
import com.drspaceman.atomicio.viewmodel.BaseViewModel

import kotlinx.android.synthetic.main.details_dialog.*

abstract class BaseDialogFragment : DialogFragment() {

    protected abstract val layoutId: Int

    protected abstract val itemId: Long?

    protected abstract val viewModel: BaseDetailsViewModel

    // @todo: make lateinit instead of nullable
    protected abstract val itemViewData: BaseViewModel.BaseViewData

    protected lateinit var parentActivity: AppCompatActivity

    protected abstract fun setObservers()

    protected abstract fun populateExistingValues()

    protected abstract fun saveItemDetails()

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
        viewModel.clearContext()
        super.onCancel(dialog)
    }

    override fun dismiss() {
        viewModel.clearContext()
        super.dismiss()
    }

    protected open fun loadDataItem() {
        viewModel.loadItem(itemId)
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
