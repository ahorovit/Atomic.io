package com.drspaceman.atomicio.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drspaceman.atomicio.viewmodel.BaseViewModel

abstract class BaseRecyclerViewAdapter(
    items: List<BaseViewModel.BaseViewData>?,
    protected val hostFragment: EditItemListener
) : RecyclerView.Adapter<BaseRecyclerViewAdapter.BaseViewHolder>() {

    protected abstract val layoutId: Int

    protected abstract fun createViewHolder(view: View): BaseViewHolder

    protected var itemList: List<BaseViewModel.BaseViewData>? = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            layoutId,
            parent,
            false
        )

        return createViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val viewData = itemList?.get(position) ?: return
        holder.bindViewData(viewData)
    }


    override fun getItemCount(): Int {
        return itemList?.count() ?: 0
    }

    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bindViewData(viewData: BaseViewModel.BaseViewData)
    }

    interface EditItemListener {
        fun editItemDetails(identityId: Long?)
    }
}