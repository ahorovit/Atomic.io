package com.drspaceman.atomicio.adapter

import android.animation.Animator
import android.view.ViewPropertyAnimator
import android.widget.ImageView
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.BaseRecyclerViewAdapter.NestedEditItemListener
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel.IdentityViewData
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

import kotlinx.android.synthetic.main.identity_header.*

class IdentityListHeader(
    private val identity: IdentityViewData,
    private val hostFragment: NestedEditItemListener,
    private var isExpanded: Boolean
) : Item(), ExpandableItem {
    private lateinit var expandableGroup: ExpandableGroup
    private lateinit var expandImageView: ImageView
    private lateinit var expandAnimator: ViewPropertyAnimator

    fun collapse() {
        if (expandableGroup.isExpanded) {
            toggle()
        }
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            identityTypeImageView.setImageResource(identity.typeResourceId)
            identityLabelTextView.text = identity.name

            addIcon.setOnClickListener {
                hostFragment.editSubItemDetails(null)
            }

            editIcon.setOnClickListener {
                hostFragment.editItemDetails(identity.id)
            }

            // Hold icon and animator so we can collapse item when another is expanded
            expandImageView = expandIcon
            expandAnimator = getAnimatorForImageView(expandImageView)

            // For some reason, expandedGroup.isExpanded doesn't reliably yield the correct icon
            expandImageView.setImageResource(if (isExpanded) R.drawable.ic_collapse else R.drawable.ic_expand)
            expandImageView.setOnClickListener {
                hostFragment.onToggleExpand(this@IdentityListHeader, identity.id)
                toggle()
            }
        }
    }

    private fun toggle() {
        expandableGroup.onToggleExpanded()
        expandAnimator.rotationBy(if (isExpanded) 180F else -180F)
            .setDuration(300)
            .start()

        isExpanded = !isExpanded
    }

    private fun getAnimatorForImageView(expandIcon: ImageView): ViewPropertyAnimator {
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