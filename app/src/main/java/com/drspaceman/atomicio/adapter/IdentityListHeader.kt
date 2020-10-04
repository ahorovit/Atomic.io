package com.drspaceman.atomicio.adapter

import android.animation.Animator
import android.opengl.Visibility
import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.ImageView
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.BaseRecyclerViewAdapter.NestedEditItemListener
import com.drspaceman.atomicio.ui.IdentityPageFragment
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
        if (isExpanded) {
            toggle()
        }
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            identityTypeImageView.setImageResource(identity.typeResourceId)
            identityLabelTextView.text = identity.name

            addIcon.setOnClickListener {
                hostFragment.editSubItemDetails(null, identity.id)
            }

            editIcon.setOnClickListener {
                hostFragment.editItemDetails(identity.id)
            }

            // Hold icon and animator so we can collapse item when another is expanded
            expandImageView = expandIcon
            expandAnimator = getAnimatorForImageView(expandImageView)

            // Recycled view might already have rotated icon
            expandImageView.rotation = if (isExpanded) -180F else 0F

            expandImageView.setOnClickListener {
                hostFragment.onToggleExpand(this@IdentityListHeader, identity.id)
                toggle()
            }

            val editVisibility = if (identity.id == IdentityPageFragment.MISC_HABITS_ID) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }

            addIcon.visibility = editVisibility
            editIcon.visibility = editVisibility
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