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
import kotlinx.android.synthetic.main.fragment_agenda.*
import java.text.DateFormat
import java.util.*

class AgendaPageFragment : BasePageFragment() {
    override val layoutId: Int = R.layout.fragment_agenda

    override val viewModel by activityViewModels<AgendaPageViewModel>()

    var todaysAgenda: AgendaViewData? = null
    var todaysTasks: List<TaskViewData>? = null

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

    override fun loadPageData() {
        viewModel.todaysTasks?.observe(
            viewLifecycleOwner,
                Observer<List<TaskViewData>> {
                    it?.let {
                        todaysTasks = it
                        onAgendaChange()
                    }
                }
            )
    }

    private fun onAgendaChange() {


    }


    companion object {
        fun newInstance() = AgendaPageFragment()
    }
}