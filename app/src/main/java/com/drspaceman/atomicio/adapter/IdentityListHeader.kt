package com.drspaceman.atomicio.adapter

import android.animation.Animator
import android.view.ViewPropertyAnimator
import android.widget.ImageView
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

            val animator = animateExpandIcon(expandIcon)
            expandIcon.setOnClickListener {
                expandableGroup.onToggleExpanded()
                animator.rotationBy(if (expandableGroup.isExpanded) 180F else -180F)
                    .setDuration(300)
                    .start();
            }

            parentLayout.setOnClickListener {
                hostFragment.editItemDetails(identity.id)
            }
        }
    }

    private fun animateExpandIcon(expandIcon: ImageView): ViewPropertyAnimator {
        expandIcon.setImageResource(if (expandableGroup.isExpanded) R.drawable.ic_collapse else R.drawable.ic_expand)

        val animator = expandIcon.animate()
        animator.setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                expandIcon.isClickable = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                expandIcon.isClickable = true
            }

            override fun onAnimationCancel(animation: Animator?) {
                // Not needed
            }

            override fun onAnimationRepeat(animation: Animator?) {
                // Not needed
            }
        })

        return animator
    }

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        expandableGroup = onToggleListener
    }

    override fun getLayout() = R.layout.identity_header
}