package com.drspaceman.atomicio.ui

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.IdentityRecyclerViewAdapter
import com.drspaceman.atomicio.viewmodel.IdentityViewModel

import kotlinx.android.synthetic.main.fragment_identities.*

class IdentityPageFragment : BasePageFragment() {
    override val viewModel by activityViewModels<IdentityViewModel>()

    override val layoutId: Int = R.layout.fragment_identities

    override fun initializeRecyclerView() {
        identityRecyclerView.layoutManager = LinearLayoutManager(context)
        recyclerViewAdapter = IdentityRecyclerViewAdapter(null, this)
        identityRecyclerView.adapter = recyclerViewAdapter

        viewModel.getIdentities()?.observe(
            viewLifecycleOwner,
            Observer<List<IdentityViewModel.IdentityViewData>> {
                it?.let {
                    recyclerViewAdapter.itemList = it
                }
            }
        )
    }

    override fun getEditDialogFragment(id: Long?): BaseDialogFragment {
        return IdentityDetailsFragment.newInstance(id)
    }

    companion object {
        fun newInstance() = IdentityPageFragment()
    }
}