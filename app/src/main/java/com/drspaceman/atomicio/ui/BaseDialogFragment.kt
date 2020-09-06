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
import kotlinx.android.synthetic.main.fragment_identity_details.*

abstract class BaseDialogFragment: DialogFragment() {

    protected abstract val layoutId: Int

    protected abstract val itemId: Long?

    protected abstract val viewModel: BaseViewModel

    protected abstract val itemViewData: BaseViewModel.BaseViewData?

    protected lateinit var parentActivity: AppCompatActivity

    protected lateinit var spinnerAdapter: ArrayAdapter<String>

    protected abstract fun observeItem(id: Long)

    protected abstract fun populateExistingValues()

    protected abstract fun saveItemDetails()

    protected abstract fun getNewItem()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layoutId, container, false)

        populateTypeSpinner()
        loadDataItem()

        saveIdentityButton.setOnClickListener {
            saveItemDetails()
        }

        return view
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

    protected fun populateTypeSpinner() {
        val spinnerViewModel = viewModel as SpinnerViewModel

        spinnerAdapter = ArrayAdapter(
            parentActivity,
            android.R.layout.simple_spinner_item,
            spinnerViewModel.getTypes()
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTypes.adapter = spinnerAdapter

        spinnerTypes.post {
            spinnerTypes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val type = parent.getItemAtPosition(position) as String
                    val resourceId = spinnerViewModel.getTypeResourceId(type)
                    resourceId?.let {
                        imageViewType.setImageResource(it)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Required by OnItemSelectedListener interface, but not needed
                }
            }
        }
    }

    protected fun setSpinnerSelection() {
        itemViewData?.let {
            it as SpinnerItemViewData
            val type = it.type ?: return
            spinnerTypes.setSelection(spinnerAdapter.getPosition(type))
            imageViewType.setImageResource(it.typeResourceId)
        }
    }

    interface SpinnerViewModel {
        fun getTypes(): List<String>
        fun getTypeResourceId(type: String?): Int?
    }

    interface SpinnerItemViewData {
        var type: String?
        var typeResourceId: Int
    }
}
