package com.drspaceman.atomicio.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel.IdentityViewData

import kotlinx.android.synthetic.main.fragment_identity_details.*
import kotlinx.android.synthetic.main.spinner_layout.*

class IdentityDetailsFragment : BaseDialogFragment() {
    override val layoutId = R.layout.fragment_identity_details

    override val itemId: Long? by lazy {
        arguments?.getLong(ARG_IDENTITY_ID, 0)
    }

    override val viewModel by viewModels<IdentityPageViewModel>()

    override var itemViewData: IdentityViewData? = null

    override fun getNewItem() {
        itemViewData = viewModel.getNewIdentityView()
    }

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