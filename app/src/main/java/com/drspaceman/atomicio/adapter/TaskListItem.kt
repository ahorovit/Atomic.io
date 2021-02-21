package com.drspaceman.atomicio.adapter

import androidx.core.widget.doAfterTextChanged
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.ui.EditTaskListener
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

import kotlinx.android.synthetic.main.scheduled_task.*
import kotlinx.android.synthetic.main.day_selection.*
import org.threeten.bp.DayOfWeek


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
            editDuration.doAfterTextChanged { taskViewData.setDuration(it.toString()) }

            val dayChips = listOf(
                Pair(sunday, DayOfWeek.SUNDAY),
                Pair(monday, DayOfWeek.MONDAY),
                Pair(tuesday, DayOfWeek.TUESDAY),
                Pair(wednesday, DayOfWeek.WEDNESDAY),
                Pair(thursday, DayOfWeek.THURSDAY),
                Pair(friday, DayOfWeek.FRIDAY),
                Pair(saturday, DayOfWeek.SATURDAY),
                Pair(sunday, DayOfWeek.SUNDAY)
            )

            dayChips.forEach {
                val chip = it.first
                val day = it.second

                chip.isChecked = taskViewData.dayFlags.isDaySelected(day)
                chip.setOnClickListener {
                    taskViewData.dayFlags.toggleDay(day)
                }
            }
        }
    }

    override fun getLayout(): Int = R.layout.scheduled_task
}