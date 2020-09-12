package com.drspaceman.atomicio.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.drspaceman.atomicio.viewmodel.BaseViewModel

// @todo remove
import kotlinx.android.synthetic.main.spinner_layout.*
import kotlinx.android.synthetic.main.crud_buttons.*

abstract class BaseDialogFragment: DialogFragment() {

    protected abstract val layoutId: Int

    protected abstract val itemId: Long?

    protected abstract val viewModel: BaseViewModel

    protected abstract val itemViewData: BaseViewModel.BaseViewData?

    protected lateinit var parentActivity: AppCompatActivity

    protected abstract fun observeItem(id: Long)

    protected abstract fun populateExistingValues()

    protected abstract fun saveItemDetails()

    protected abstract fun getNewItem()

    protected abstract fun setSpinnerSelection()

    protected abstract fun populateTypeSpinner()

//    override fun getTheme(): Int {
//        return R.style.AppTheme
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateTypeSpinner()
        loadDataItem()

        saveButton.setOnClickListener {
            saveItemDetails()
        }

        deleteButton.setOnClickListener {
            deleteSelectedItem()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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

    protected fun loadDataItem() {
        itemId?.let {
            observeItem(it)
        } ?: run {
            getNewItem()
            setSpinnerSelection()
        }
    }

    protected fun deleteSelectedItem() {
        val item = itemViewData

        item?.id?.let {
            viewModel.deleteItem(item)
        }

        dismiss()
    }

    interface SpinnerViewModel {
//        fun getSpinnerItems(): Any
        fun getSpinnerItemResourceId(type: String?): Int?
    }

    interface SpinnerItemViewData {
        var type: String?
        var typeResourceId: Int
    }
}