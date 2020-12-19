package com.drspaceman.atomicio.adapter

import android.text.Editable
import android.text.TextWatcher
import androidx.core.widget.addTextChangedListener
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.ui.BaseDialogFragment
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

import kotlinx.android.synthetic.main.scheduled_task.*
import kotlinx.android.synthetic.main.day_selection.*


class TaskListItem(
    private val taskViewData: AgendaPageViewModel.TaskViewData,
    private val hostFragment: BaseDialogFragment
): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            editStartTime.setText(taskViewData.getStartTime())
            editDuration.setText(taskViewData.getDuration())

//            editStartTime.addTextChangedListener(object : TextWatcher {
//                override fun beforeTextChanged(
//                    s: CharSequence?,
//                    start: Int,
//                    count: Int,
//                    after: Int
//                ) {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    taskViewData.setStartTime(s)
//                }
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