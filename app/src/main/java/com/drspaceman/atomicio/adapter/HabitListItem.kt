package com.drspaceman.atomicio.adapter

import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.BaseRecyclerViewAdapter.EditItemListener
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel.HabitViewData
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

import kotlinx.android.synthetic.main.habit_list_item.*

class HabitListItem(
    val habit: HabitViewData,
    val hostFragment: EditItemListener
): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.habitLabelTextView.text = habit.name

        viewHolder.parentLayout.setOnClickListener {
            hostFragment.editItemDetails(habit.id)
        }
    }

    override fun getLayout() = R.layout.habit_list_item
}