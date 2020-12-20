package com.drspaceman.atomicio.adapter

import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.ui.EditTaskListener
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

import kotlinx.android.synthetic.main.scheduled_task.*


class TaskListItem(
    private val taskViewData: AgendaPageViewModel.TaskViewData,
    private val hostFragment: EditTaskListener
): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            editStartTime.setText(taskViewData.getStartTime())
            editStartTime.setOnClickListener {
                hostFragment.pickTimeForTask(taskViewData, position)
            }

            editDuration.setText(taskViewData.getDuration())
        }
    }

    override fun getLayout(): Int = R.layout.scheduled_task
}