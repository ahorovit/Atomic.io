package com.drspaceman.atomicio.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.CheckListItem
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel.TaskViewData
import com.drspaceman.atomicio.viewstate.AgendaViewState
import com.drspaceman.atomicio.viewstate.ChecklistViewLoaded
import com.drspaceman.atomicio.viewstate.DayViewLoaded
import com.drspaceman.atomicio.viewstate.Loading
import com.linkedin.android.tachyon.DayView.EventTimeRange
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_agenda.*
import kotlinx.coroutines.*
import java.text.DateFormat
import java.util.*

class AgendaPageFragment : BasePageFragment() {

    override val fragmentTitle = "Agenda"

    override val layoutId: Int = R.layout.fragment_agenda

    private var agendaViewMenuButton: MenuItem? = null

    private var groupAdapter = GroupAdapter<GroupieViewHolder>()

    override val viewModel by activityViewModels<AgendaPageViewModel>()

    // @todo: implement without Calendar class
    private lateinit var day: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.agenda_fragment_menu, menu)
        agendaViewMenuButton = menu.findItem(R.id.toggleViewButton)
        agendaViewMenuButton?.setIcon(R.drawable.ic_calendar_view) // TODO: get from preferences
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderDayViewBackground()

        checkListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toggleViewButton -> {
                viewModel.toggleAgendaView()
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    // TODO: reimplement without Calendar
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
        viewModel.viewState.observe(
            viewLifecycleOwner,
            {
                render(it)
            }
        )
    }

    private fun render(state: AgendaViewState) {
        viewFlipper.displayedChild = when (state) {
            Loading -> LOADING
            is DayViewLoaded -> {
                onDayViewChange(state.tasks)
                agendaViewMenuButton?.setIcon(R.drawable.ic_calendar_view)
                CALENDAR_VIEW
            }
            is ChecklistViewLoaded -> {
                onCheckListViewChange(state.tasks)
                agendaViewMenuButton?.setIcon(R.drawable.ic_checklist_view)
                CHECKLIST_VIEW
            }
        }
    }

    private fun onCheckListViewChange(tasks: List<TaskViewData>) {
        launch {
            val items = async(Dispatchers.Default) {
                tasks.map { CheckListItem(it, this@AgendaPageFragment) }
            }

            groupAdapter.apply {
                clear()
                addAll(items.await())
            }
        }
    }

    private fun onDayViewChange(tasks: List<TaskViewData>) {
        launch {
            val calendarData = async {
                getCalendarViewItems(tasks)
            }

            val (taskViews, taskTimeRanges) = calendarData.await()

            // Update the day view with the new events
            dayView.setEventViews(taskViews, taskTimeRanges)
        }
    }

    // @todo: example code here is crap -- reimplement
    private suspend fun getCalendarViewItems(tasks: List<TaskViewData>): CalendarData =
        withContext(Dispatchers.Default) {
            // The day view needs a list of event views and a corresponding list of event time ranges
            val taskViews: MutableList<View?>?
            val taskTimeRanges: MutableList<EventTimeRange?>?


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

            CalendarData(taskViews, taskTimeRanges)
        }

    companion object {
        private const val LOADING = 0
        private const val CALENDAR_VIEW = 1
        private const val CHECKLIST_VIEW = 2

        fun newInstance() = AgendaPageFragment()
    }

    data class CalendarData(
        val taskViews: MutableList<View?>,
        val taskTimeRanges: MutableList<EventTimeRange?>
    )
}