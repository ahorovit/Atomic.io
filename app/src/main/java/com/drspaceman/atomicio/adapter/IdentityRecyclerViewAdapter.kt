package com.drspaceman.atomicio.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.viewmodel.BaseViewModel
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel.IdentityViewData

class IdentityRecyclerViewAdapter(
    items: List<IdentityViewData>?,
    hostFragment: EditItemListener
) : BaseRecyclerViewAdapter(items, hostFragment){

    override val layoutId: Int = R.layout.identity_view_holder

    override fun createViewHolder(view: View): BaseViewHolder {
        return IdentityViewHolder(view, hostFragment)
    }

    class IdentityViewHolder(
        itemView: View,
        private val hostFragment: EditItemListener
    ) : BaseViewHolder(itemView) {

        var identityId: Long? = null

        // @todo: KAE isn't working!! Why?
        val identityLabelTextView: TextView = itemView.findViewById(R.id.identityLabelTextView)
        val identityTypeImageView: ImageView = itemView.findViewById(R.id.identityTypeImageView)

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