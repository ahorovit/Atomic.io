package com.drspaceman.atomicio.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.viewmodel.IdentityViewModel.IdentityView

class IdentityRecyclerViewAdapter(
    private var identityData: List<IdentityView>?,
    private val hostFragment: EditIdentityListener
) : RecyclerView.Adapter<IdentityRecyclerViewAdapter.IdentityViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IdentityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.identity_view_holder,
            parent,
            false
        )

        return IdentityViewHolder(view, hostFragment)
    }

    override fun getItemCount(): Int {
        return identityData?.count() ?: 0
    }

    override fun onBindViewHolder(holder: IdentityViewHolder, position: Int) {

        identityData?.let {
            val identity = it[position]
            holder.identityId = identity.id
            holder.identityLabelTextView.text = identity.name
            holder.identityTypeImageView.setImageResource(identity.typeResourceId)
        }
    }

    fun setIdentityData(identities: List<IdentityView>) {
        identityData = identities
        notifyDataSetChanged()
    }

    class IdentityViewHolder(
        itemView: View,
        private val hostFragment: EditIdentityListener
    ) : RecyclerView.ViewHolder(itemView) {

        var identityId: Long? = null

        // @todo: KAE isn't working!! Why?
        val identityLabelTextView: TextView = itemView.findViewById(R.id.identityLabelTextView)
        val identityTypeImageView: ImageView = itemView.findViewById(R.id.identityTypeImageView)

        init {
            itemView.setOnClickListener {
                identityId?.let {
                    hostFragment.editIdentityDetails(it)
                }
            }
        }
    }

    interface EditIdentityListener {
        fun editIdentityDetails(identityId: Long?)
    }
}