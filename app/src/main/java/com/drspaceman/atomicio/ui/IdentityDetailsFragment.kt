package com.drspaceman.atomicio.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel.IdentityViewData

import kotlinx.android.synthetic.main.fragment_identity_details.*
import kotlinx.android.synthetic.main.spinner_layout.*

class IdentityDetailsFragment : BaseDialogFragment() {

    private lateinit var spinnerAdapter: ArrayAdapter<String>

    override val layoutId = R.layout.fragment_identity_details

    override val itemId: Long? by lazy {
        arguments?.getLong(ARG_IDENTITY_ID, 0)
    }

    override val viewModel by viewModels<IdentityPageViewModel>()

    override var itemViewData: IdentityViewData? = null

    override fun observeItem(id: Long) {
        viewModel.getIdentity(id)?.observe(
            this,
            Observer<IdentityViewData> {
                it?.let {
                    itemViewData = it
                    populateExistingValues()
                }
            }
        )
    }

    override fun populateExistingValues() {
        itemViewData?.let {
            editTextName.setText(it.name)
            setSpinnerSelection()
        }
    }

    override fun getNewItem() {
        itemViewData = viewModel.getNewIdentityView()
    }

    override fun populateTypeSpinner() {
        val spinnerViewModel = viewModel as SpinnerViewModel

        spinnerAdapter = ArrayAdapter(
            parentActivity,
            android.R.layout.simple_spinner_item,
            viewModel.getSpinnerItems()
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.post {
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val type = parent.getItemAtPosition(position) as String
                    val resourceId = spinnerViewModel.getSpinnerItemResourceId(type)
                    resourceId?.let {
                        spinnerImage.setImageResource(it)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Required by OnItemSelectedListener interface, but not needed
                }
            }
        }

        loadDataItem()
    }

    override fun setSpinnerSelection() {
        itemViewData?.let {
            it as SpinnerItemViewData
            val type = it.type ?: return
            spinner.setSelection(spinnerAdapter.getPosition(type))
            spinnerImage.setImageResource(it.typeResourceId)
        }
    }

    override fun saveItemDetails() {
        val writeIdentityView = itemViewData?: return

        val name = editTextName.text.toString()
        if (name.isEmpty()) {
            return
        }

        writeIdentityView.let {
            it.name = name
            it.type = spinner.selectedItem as String
        }

        writeIdentityView.id?.let {
            viewModel.updateIdentity(writeIdentityView)
        } ?: run {
            viewModel.insertIdentity(writeIdentityView)
        }

        dismiss()
    }

    companion object {
        private const val ARG_IDENTITY_ID = "extra_identity_id"

        fun newInstance(identityId: Long?): IdentityDetailsFragment {
            val instance = IdentityDetailsFragment()

            identityId?.let {
                val args = Bundle()
                args.putLong(ARG_IDENTITY_ID, identityId)
                instance.arguments = args
            }

            return instance
        }
    }
}