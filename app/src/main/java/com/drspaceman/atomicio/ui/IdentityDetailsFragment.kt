package com.drspaceman.atomicio.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.viewmodel.IdentityDetailsViewModel
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel.IdentityViewData
import com.drspaceman.atomicio.viewmodel.SpinnerViewModelInterface
import com.drspaceman.atomicio.viewstate.IdentityLoaded
import com.drspaceman.atomicio.viewstate.IdentityLoading
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.details_dialog.*

import kotlinx.android.synthetic.main.fragment_identity_details.*
import kotlinx.android.synthetic.main.spinner_layout.*

@AndroidEntryPoint
class IdentityDetailsFragment : BaseDialogFragment() {

    private lateinit var spinnerAdapter: ArrayAdapter<String>

    override val layoutId = R.layout.fragment_identity_details

    override val itemId: Long? by lazy {
        arguments?.getLong(ARG_IDENTITY_ID, 0)
    }

    override val viewModel by activityViewModels<IdentityDetailsViewModel>()

    override lateinit var itemViewData: IdentityViewData

    override fun setObservers() {
        viewModel.viewState.observe(
            this,
            {
                viewFlipper.displayedChild = when(it) {
                    IdentityLoading -> LOADING
                    is IdentityLoaded ->  {
                        itemViewData = it.identity
                        populateExistingValues()
                        DETAILS_FORM
                    }
                }
            }
        )
    }

    override fun populateExistingValues() {
        itemViewData.let {
            editTextName.setText(it.name)
            setSpinnerSelection()
        }
    }

    override fun populateTypeSpinner() {
        val spinnerViewModel = viewModel as SpinnerViewModelInterface

        spinnerAdapter = ArrayAdapter(
            requireActivity(),
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
        val type = itemViewData.type ?: return
        spinner.setSelection(spinnerAdapter.getPosition(type))
        spinnerImage.setImageResource(itemViewData.typeResourceId)
    }

    override fun saveItemDetails() {
        val writeIdentityView = itemViewData

        val name = editTextName.text.toString()
        if (name.isEmpty()) {
            return
        }

        writeIdentityView.let {
            it.name = name
            it.type = spinner.selectedItem as String
            it.description = spinner.selectedItem as String // @todo get rid of redundant fields
        }

        writeIdentityView.id?.let {
            viewModel.updateIdentity(writeIdentityView)
        } ?: run {
            viewModel.insertIdentity(writeIdentityView)
        }

        dismiss()
    }

    override fun deleteSelectedItem() {
        itemViewData.id?.let {
            AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.confirm_identity_delete))
                .setMessage("Child Habits will become 'Misc Habits' if not deleted")
                .setPositiveButton("Delete Habits") { _, _ ->
                    viewModel.deleteIdentityAndHabits(itemViewData)
                    dismiss()
                }
                .setNegativeButton("Keep Habits") { _, _ ->
                    viewModel.deleteItem(itemViewData)
                    dismiss()
                }
                .setNeutralButton("Cancel", null)
                .create()
                .show()
        }
    }

    companion object {
        private const val ARG_IDENTITY_ID = "extra_identity_id"

        private const val LOADING = 0
        private const val DETAILS_FORM = 1

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