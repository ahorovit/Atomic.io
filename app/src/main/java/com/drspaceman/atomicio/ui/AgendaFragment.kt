package com.drspaceman.atomicio.ui


import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.collection.LongSparseArray
import androidx.fragment.app.activityViewModels
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel
import com.drspaceman.atomicio.viewmodel.BaseViewModel
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel
import com.linkedin.android.tachyon.DayView.EventTimeRange
import kotlinx.android.synthetic.main.fragment_agenda.*
import java.text.DateFormat
import java.util.*

class AgendaFragment : BasePageFragment() {
    override val layoutId: Int = R.layout.fragment_agenda

    override val viewModel by activityViewModels<AgendaPageViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        renderDayViewBackground()

    }

    private fun renderDayViewBackground() {
        // Create a new calendar object set to the start of today
        val day = Calendar.getInstance()
        day.set(Calendar.HOUR_OF_DAY, 0)
        day.set(Calendar.MINUTE, 0)
        day.set(Calendar.SECOND, 0)
        day.set(Calendar.MILLISECOND, 0)

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

    override fun initializeRecyclerView() {
//        initializeData()
//        val sequence = habitSequence ?: return
//
//        habitSequenceRecyclerView.layoutManager = LinearLayoutManager(this.context)
//        recyclerViewAdapter = HabitRecyclerViewAdapter(sequence, this)
//        habitSequenceRecyclerView.adapter = recyclerViewAdapter
//
////        attachItemTouchHelper()
        }


    companion object {
        fun newInstance() = AgendaFragment()
    }
}