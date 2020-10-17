package com.drspaceman.atomicio.ui


import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel.TaskViewData
import com.linkedin.android.tachyon.DayView.EventTimeRange

import java.text.DateFormat
import java.util.*

import kotlinx.android.synthetic.main.fragment_agenda.*

class AgendaPageFragment : BasePageFragment() {

    override val fragmentTitle = "Agenda"

    override val layoutId: Int = R.layout.fragment_agenda

    private var agendaView = CALENDAR_VIEW

    override val viewModel by activityViewModels<AgendaPageViewModel>()

    private var todaysTasks: List<TaskViewData>? = null

    // @todo: implement without Calendar class
    private lateinit var day: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.agenda_fragment_menu, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        renderDayViewBackground()
        onAgendaChange()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.toggleViewButton -> {
                val icon = if (agendaView == CALENDAR_VIEW) {
                    agendaView = CHECKLIST_VIEW
                    R.drawable.ic_checklist_view
                } else {
                    agendaView = CALENDAR_VIEW
                    R.drawable.ic_calendar_view
                }

                item.setIcon(icon)
            }
            R.id.importTemplate -> {  }
            R.id.exportTemplate -> {  }
            else -> return super.onOptionsItemSelected(item)
        }

        return true
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
        return TaskDetailsFragment.newInstance(id)
    }

    override fun loadPageData() {
        viewModel.tasks.observe(
            viewLifecycleOwner,
            {
                todaysTasks = it
                onAgendaChange()
            }
        )
    }

    private fun onAgendaChange() {
        // The day view needs a list of event views and a corresponding list of event time ranges
        var taskViews: MutableList<View?>?
        var taskTimeRanges: MutableList<EventTimeRange?>?
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

            val taskTitle = taskView.findViewById<TextView>(R.id.taskTitle)

            taskTitle.text = task.title
//            taskLocation.text = task.location

            taskView.setBackgroundColor(resources.getColor(android.R.color.holo_red_dark))

            // When an event is clicked, start a new draft event and show the edit event dialog
            // @todo: implement this without !! or Calendar
            taskView.setOnClickListener {
                showEditDetailsDialog(task.id)
            }
            taskViews.add(taskView)

            // The day view needs the event time ranges in the start minute/end minute format,
            // so calculate those here
            val startMinute: Int = 60 * task.startTime!!.hour + task.startTime!!.minute
            val endMinute: Int = 60 * task.endTime!!.hour + task.endTime!!.minute
            taskTimeRanges.add(EventTimeRange(startMinute, endMinute))
        }

        // Update the day view with the new events
        dayView.setEventViews(taskViews, taskTimeRanges)
    }


    companion object {

        private const val CALENDAR_VIEW = "calendar"
        private const val CHECKLIST_VIEW = "checklist"

        fun newInstance() = AgendaPageFragment()
    }
}