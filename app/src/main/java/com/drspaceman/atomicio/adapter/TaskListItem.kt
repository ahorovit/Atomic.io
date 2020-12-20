package com.drspaceman.atomicio.adapter

import android.text.Editable
import android.text.TextWatcher
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
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
            editDuration.doAfterTextChanged { taskViewData.setDuration(it.toString()) }

//            editDuration.addTextChangedListener(object : TextWatcher {
//                override fun beforeTextChanged(
//                    s: CharSequence?,
//                    start: Int,
//                    count: Int,
//                    after: Int
//                ) {}
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//                override fun afterTextChanged(s: Editable?) {
//                    TODO("Not yet implemented")
//                }
//
//            })
        }
    }

    override fun getLayout(): Int = R.layout.scheduled_task
}