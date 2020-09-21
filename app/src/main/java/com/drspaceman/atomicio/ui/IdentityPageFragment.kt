package com.drspaceman.atomicio.ui

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.IdentityRecyclerViewAdapter
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel

import kotlinx.android.synthetic.main.fragment_identities.*

class IdentityPageFragment : BasePageFragment() {
    override val viewModel by activityViewModels<IdentityPageViewModel>()

    override val layoutId: Int = R.layout.fragment_identities

    override fun loadPageData() {
        identityRecyclerView.layoutManager = LinearLayoutManager(context)
        recyclerViewAdapter = IdentityRecyclerViewAdapter(null, this)
        identityRecyclerView.adapter = recyclerViewAdapter

        viewModel.identities.observe(
            viewLifecycleOwner,
            {
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