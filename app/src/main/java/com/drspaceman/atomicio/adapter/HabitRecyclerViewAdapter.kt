package com.drspaceman.atomicio.adapter

import android.view.View
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.viewmodel.BaseViewModel
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel.HabitViewData
import kotlinx.android.synthetic.main.habit_view_holder.view.*

class HabitRecyclerViewAdapter(
    items: List<HabitViewData>?,
    hostFragment: EditItemListener
) : BaseRecyclerViewAdapter(items, hostFragment){
    override val layoutId: Int = R.layout.habit_view_holder

    override fun createViewHolder(view: View): BaseViewHolder {
        return HabitViewHolder(view, hostFragment)
    }

    class HabitViewHolder(
        itemView: View,
        private val hostFragment: EditItemListener
    ) : BaseViewHolder(itemView) {

        var habitId: Long? = null
        val habitLabelTextView = itemView.habitLabelTextView
        val habitTypeImageView = itemView.habitTypeImageView

        init {
            itemView.setOnClickListener {
                habitId?.let {
                    hostFragment.editItemDetails(it)
                }
            }
        }

        override fun bindViewData(viewData: BaseViewModel.BaseViewData) {
            val habit = viewData as HabitViewData
            habitId = habit.id
            habitLabelTextView.text = habit.name
            habitTypeImageView.setImageResource(habit.typeResourceId)
        }
    }
}