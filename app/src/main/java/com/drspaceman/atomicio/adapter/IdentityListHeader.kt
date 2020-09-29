package com.drspaceman.atomicio.adapter

import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.BaseRecyclerViewAdapter.EditItemListener
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel.IdentityViewData
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

import kotlinx.android.synthetic.main.identity_header.*

class IdentityListHeader(
    private val identity: IdentityViewData,
    private val hostFragment: EditItemListener
) : Item(), ExpandableItem {

    private lateinit var expandableGroup: ExpandableGroup

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            identityLabelTextView.text = identity.name
            identityTypeImageView.setImageResource(identity.typeResourceId)

            expandIcon.setImageResource(getIcon())
            expandIcon.setOnClickListener {
                expandableGroup.onToggleExpanded()
            }

            parentLayout.setOnClickListener {
                hostFragment.editItemDetails(identity.id)
            }
        }
    }

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        expandableGroup = onToggleListener
    }

    override fun getLayout() = R.layout.identity_header

    private fun getIcon(): Int {
        return if (expandableGroup.isExpanded)
            android.R.drawable.arrow_down_float
        else
            android.R.drawable.arrow_up_float
    }
}