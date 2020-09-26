package com.drspaceman.atomicio.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.viewmodel.BaseViewModel
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel.IdentityViewData

import kotlinx.android.synthetic.main.identity_view_holder.view.*

class IdentityRecyclerViewAdapter(
    items: List<IdentityViewData>?,
    hostFragment: EditItemListener
) : BaseRecyclerViewAdapter(items, hostFragment){

    override val layoutId: Int = R.layout.identity_header

    override fun createViewHolder(view: View) = IdentityViewHolder(view, hostFragment)

    class IdentityViewHolder(
        itemView: View,
        private val hostFragment: EditItemListener
    ) : BaseViewHolder(itemView) {

        var identityId: Long? = null
        val identityLabelTextView: TextView = itemView.identityLabelTextView
        val identityTypeImageView: ImageView = itemView.identityTypeImageView

        init {
            itemView.setOnClickListener {
                identityId?.let {
                    hostFragment.editItemDetails(it)
                }
            }
        }

        override fun bindViewData(viewData: BaseViewModel.BaseViewData) {
            val identity = viewData as IdentityViewData
            identityId = identity.id
            identityLabelTextView.text = identity.name
            identityTypeImageView.setImageResource(identity.typeResourceId)
        }
    }
}