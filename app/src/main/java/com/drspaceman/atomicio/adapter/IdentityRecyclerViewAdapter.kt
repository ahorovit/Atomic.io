package com.drspaceman.atomicio.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.viewmodel.IdentityViewModel
import kotlinx.android.synthetic.main.identity_view_holder.view.*

class IdentityRecyclerViewAdapter(
    val identityList: MutableList<IdentityViewModel.IdentityViewData>
) : RecyclerView.Adapter<IdentityRecyclerViewAdapter.IdentityViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IdentityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.identity_view_holder,
            parent,
            false
        )

        return IdentityViewHolder(view)
    }

    override fun getItemCount(): Int {
        return identityList.count()
    }

    override fun onBindViewHolder(holder: IdentityViewHolder, position: Int) {
        holder.identityLabelTextView.text = identityList[position].name
//
//        holder.parentLayout.setOnClickListener {
//
//        }
    }

    class IdentityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val identityLabelTextView = itemView.identityLabelTextView
        val habitTypeImageView = itemView.identityTypeImageView
        val parentLayout = itemView.parentLayout
    }
}