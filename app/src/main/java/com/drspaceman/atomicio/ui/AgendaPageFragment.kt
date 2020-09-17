package com.drspaceman.atomicio.ui


import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel.AgendaViewData
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel.TaskViewData
import com.linkedin.android.tachyon.DayView.EventTimeRange
import kotlinx.android.synthetic.main.agenda_item.*
import kotlinx.android.synthetic.main.fragment_agenda.*
import java.text.DateFormat
import java.util.*


class AgendaPageFragment : BasePageFragment() {
    override val layoutId: Int = R.layout.fragment_agenda

    override val viewModel by activityViewModels<AgendaPageViewModel>()

    var todaysAgenda: AgendaViewData? = null
    var todaysTasks: List<TaskViewData>? = null


    // @todo: implement without Calendar class
    private lateinit var day: Calendar


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        renderDayViewBackground()
    }

    private fun renderDayViewBackground() {
        // Create a new calendar object set to the start of today
        day = Calendar.getInstance().apply {
            this.set(Calendar.HOUR_OF_DAY, 0)
            this.set(Calendar.MINUTE, 0)
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)
        }

        val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())

        // Inflate a label view for each hour the day view will display
        val hour = day.clone() as Calendar
        val hourLabelViews: MutableList<View> = ArrayList()
        for (i in dayView.startHour..dayView.endHour) {
            hour[Calendar.HOUR_OF_DAY] = i
            val hourLabelView =
                layoutInflater.inflate(R.layout.hour_label, dayView, false) as TextView
            hourLabelView.text = timeFormat.format(hour.time)
            hourLabelViews.add(hourLabelView)
        }

        dayView.setHourLabelViews(hourLabelViews)
    }


    override fun getEditDialogFragment(id: Long?): BaseDialogFragment {
        TODO("Not yet implemented")
    }

    override fun loadPageData() {
        viewModel.tasks?.observe(
            viewLifecycleOwner,
            Observer<List<TaskViewData>> {
                it?.let {
                    todaysTasks = it
                    onAgendaChange()
                }
            }
        )
    }


    private var editEventDate: Calendar? = null
    private var editEventStartTime: Calendar? = null
    private var editEventEndTime: Calendar? = null
    private var editEventDraft: TaskViewData? = null

    private fun onAgendaChange() {
        // The day view needs a list of event views and a corresponding list of event time ranges
        var taskViews: MutableList<View?>? = null
        var taskTimeRanges: MutableList<EventTimeRange?>? = null
        val tasks: List<TaskViewData> = todaysTasks ?: return


        // Sort the events by start time so the layout happens in correct order
        // @todo: implement this without !!
        Collections.sort(tasks,
            Comparator<TaskViewData> { o1, o2 ->
                if (o1.startTime!! < o2.startTime!!) -1 else if (o1.startTime!! == o2.startTime!!) 0 else 1
            })
        taskViews = ArrayList()
        taskTimeRanges = ArrayList()

        // Reclaim all of the existing event views so we can reuse them if needed, this process
        // can be useful if your day view is hosted in a recycler view for example
        val recycled = dayView.removeEventViews()
        var remaining = recycled?.size ?: 0
        for (task in tasks) {
            // Try to recycle an existing event view if there are enough left, otherwise inflate
            // a new one
            val taskView =
                if (remaining > 0) recycled!![--remaining] else layoutInflater.inflate(
                    R.layout.agenda_item,
                    dayView,
                    false
                )

            taskTitle.text = task.title
            taskLocation.text = task.location

//            taskView.setBackgroundColor(resources.getColor(task.color))

            // When an event is clicked, start a new draft event and show the edit event dialog
            // @todo: implement this without !! or Calendar
            taskView.setOnClickListener {
                editEventDraft = task
                editEventDate = day.clone() as Calendar
                editEventStartTime = Calendar.getInstance().apply {
                    this.set(Calendar.HOUR_OF_DAY, editEventDraft!!.startTime!!.hour)
                    this.set(Calendar.MINUTE, editEventDraft!!.startTime!!.minute)
                    this.set(Calendar.SECOND, 0)
                    this.set(Calendar.MILLISECOND, 0)
                }

                editEventEndTime = editEventStartTime!!.clone() as Calendar
                editEventEndTime!!.add(Calendar.MINUTE, editEventDraft!!.duration!!)
//                showEditEventDialog(
//                    true,
//                    editEventDraft.title,
//                    editEventDraft.location,
//                    editEventDraft.color
//                )
            }
            taskViews.add(taskView)

            // The day view needs the event time ranges in the start minute/end minute format,
            // so calculate those here
            val startMinute: Int = 60 * task.startTime!!.hour + task.startTime!!.minute
            val endMinute: Int = startMinute + task.duration!!
            taskTimeRanges.add(EventTimeRange(startMinute, endMinute))
        }

        // Update the day view with the new events
        dayView.setEventViews(taskViews, taskTimeRanges)
    }


    companion object {
        fun newInstance() = AgendaPageFragment()
    }
}