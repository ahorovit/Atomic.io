package com.drspaceman.atomicio.adapter

import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

import kotlinx.android.synthetic.main.identity_footer.*


class IdentityListFooter(
    private val identity: IdentityPageViewModel.IdentityViewData,
    private val hostFragment: BaseRecyclerViewAdapter.EditItemListener
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {

            footer.setOnClickListener {  }
        }
    }

    override fun getLayout() = R.layout.identity_footer
}