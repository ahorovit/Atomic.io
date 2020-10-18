package com.drspaceman.atomicio.adapter

import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.ui.BasePageFragment
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel.TaskViewData
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

import kotlinx.android.synthetic.main.checklist_item.*

class CheckListItem(
    private val taskViewData: TaskViewData,
    private val hostFragment: BasePageFragment
): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
//            taskTypeImageView.setImageResource(taskViewData.) // @todo: hold typeResourceId
            taskLabelTextView.text = taskViewData.title
            timesTextView.text = "${taskViewData.startTime} - ${taskViewData.endTime}"
        }
    }

    override fun getLayout() = R.layout.checklist_item
}