package com.drspaceman.atomicio.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.CheckListItem
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel.Companion.CALENDAR_VIEW
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel.Companion.CHECKLIST_VIEW
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel.TaskViewData
import com.linkedin.android.tachyon.DayView.EventTimeRange
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

import java.text.DateFormat
import java.util.*

import kotlinx.android.synthetic.main.fragment_agenda.*

class AgendaPageFragment : BasePageFragment() {

    override val fragmentTitle = "Agenda"

    override val layoutId: Int = R.layout.fragment_agenda

    private var agendaView: String? = null

    private var agendaViewMenuButton: MenuItem? = null

    private var groupAdapter = GroupAdapter<GroupieViewHolder>()

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
        agendaViewMenuButton = menu.findItem(R.id.toggleViewButton)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderDayViewBackground()

        checkListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }

        viewSwitcher.inAnimation =
            AnimationUtils.loadAnimation(activity, android.R.anim.slide_in_left)
        viewSwitcher.outAnimation =
            AnimationUtils.loadAnimation(activity, android.R.anim.slide_out_right)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toggleViewButton -> {
                viewModel.toggleAgendaView()
            }
            R.id.importTemplate -> {

            }
            R.id.exportTemplate -> {

            }
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
        viewModel.agendaView.observe(
            viewLifecycleOwner,
            {
                agendaView = it
                swapAgendaView()
                renderAgenda()
            }
        )

        viewModel.tasks.observe(
            viewLifecycleOwner,
            {
                todaysTasks = it
                renderAgenda()
            }
        )
    }

    private fun swapAgendaView() {
        agendaViewMenuButton?.setIcon(
            when (agendaView) {
                CALENDAR_VIEW -> R.drawable.ic_calendar_view
                else -> R.drawable.ic_checklist_view
            }
        )
    }

    private fun renderAgenda() {
        when (agendaView) {
            CALENDAR_VIEW -> {
                viewSwitcher.showPrevious()
                onDayViewChange()
            }
            CHECKLIST_VIEW -> {
                viewSwitcher.showNext()
                onCheckListViewChange()
            }
            else -> return
        }
    }

    private fun onCheckListViewChange() {
        todaysTasks?.let { tasks ->
            groupAdapter.apply {
                clear()
                addAll(tasks.map { CheckListItem(it, this@AgendaPageFragment) })
            }
        }
    }

    // @todo: example code here is crap -- reimplement
    private fun onDayViewChange() {
        // The day view needs a list of event views and a corresponding list of event time ranges
        val taskViews: MutableList<View?>?
        val taskTimeRanges: MutableList<EventTimeRange?>?
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
        fun newInstance() = AgendaPageFragment()
    }
}