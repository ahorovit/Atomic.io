package com.drspaceman.atomicio.adapter

import android.view.View
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.ui.HabitsFragment
import com.drspaceman.atomicio.viewmodel.BaseViewModel
import com.drspaceman.atomicio.viewmodel.HabitViewModel.HabitViewData
import kotlinx.android.synthetic.main.habit_view_holder.view.*

class HabitRecyclerViewAdapter(
    items: List<HabitViewData>?,
    hostFragment: HabitsFragment
) : BaseRecyclerViewAdapter(items, hostFragment){
    override val layoutId: Int = R.layout.habit_view_holder

    override fun createViewHolder(view: View): BaseViewHolder {
        return HabitViewHolder(view)
    }

    class HabitViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val habitLabelTextView = itemView.habitLabelTextView
        val habitTypeImageView = itemView.habitTypeImageView

        override fun bindViewData(viewData: BaseViewModel.BaseViewData) {
            val dataItem = viewData as HabitViewData

            habitLabelTextView.text = dataItem.name
        }
    }
}