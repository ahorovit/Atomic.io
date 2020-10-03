package com.drspaceman.atomicio.adapter

import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.BaseRecyclerViewAdapter.NestedEditItemListener
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel.HabitViewData
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

import kotlinx.android.synthetic.main.habit_list_item.*

class HabitListItem(
    private val habit: HabitViewData,
    private val hostFragment: NestedEditItemListener
): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            habitLabelTextView.text = habit.name

            editIcon.setOnClickListener {
                hostFragment.editSubItemDetails(habit.id)
            }
        }
    }

    override fun getLayout() = R.layout.habit_list_item
}