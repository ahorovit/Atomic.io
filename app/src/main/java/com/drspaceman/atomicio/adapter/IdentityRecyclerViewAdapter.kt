package com.drspaceman.atomicio.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.ui.MainActivity
import com.drspaceman.atomicio.viewmodel.IdentityViewModel.IdentityView
import kotlinx.android.synthetic.main.identity_view_holder.view.*

class IdentityRecyclerViewAdapter(
    private var identityData: List<IdentityView>?,
    private val mainActivity: MainActivity
) : RecyclerView.Adapter<IdentityRecyclerViewAdapter.IdentityViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IdentityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.identity_view_holder,
            parent,
            false
        )

        return IdentityViewHolder(view, mainActivity)
    }

    override fun getItemCount(): Int {
        return identityData?.count() ?: 0
    }

    override fun onBindViewHolder(holder: IdentityViewHolder, position: Int) {
        val identityData = identityData ?: return



        holder.identityId = identityData[position].id
        holder.identityLabelTextView.text = identityData[position].name
//
//        holder.parentLayout.setOnClickListener {
//
//        }
    }

    fun setIdentityData(identities: List<IdentityView>) {
        identityData = identities
        notifyDataSetChanged()
    }

    class IdentityViewHolder(
        itemView: View,
        private val mainActivity: MainActivity
    ) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                identityId?.let {
                    mainActivity.editIdentityDetails(it)
                }
            }
        }

        var identityId: Long? = null
        val identityLabelTextView = itemView.identityLabelTextView
        val habitTypeImageView = itemView.identityTypeImageView
        val parentLayout = itemView.parentLayout
    }
}