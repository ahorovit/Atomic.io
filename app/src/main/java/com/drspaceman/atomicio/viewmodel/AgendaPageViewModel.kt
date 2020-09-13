package com.drspaceman.atomicio.viewmodel

import android.app.Application
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.drspaceman.atomicio.model.Agenda
import com.drspaceman.atomicio.util.DateUtil
import java.time.DayOfWeek
import java.time.LocalDate

class AgendaPageViewModel(application: Application) : BaseViewModel(application) {

    var todaysAgenda: LiveData<AgendaViewData>? = null
        private set
        get() {
//            if (field == null) {
//                field = Transformations.map(atomicIoRepo.getAgendaForDate(LocalDate.now()))
//            }
            return field
        }

    var todaysTasks: LiveData<List<TaskViewData>>? = null
        private set
        get() {
//            if (todaysAgenda == null) {
//
//            }
//
//            if (field == null) {
//                field = Transformations.map(atomicIoRepo.)
//            }

            return field
        }

    override fun deleteItem(itemViewData: BaseViewData) {
        TODO("Not yet implemented")
    }

    fun agendaToAgendaViewData(agenda: Agenda): AgendaViewData {
        val dayOfWeek: DayOfWeek? = if (SDK_INT < O) {
            agenda.date?.let {
                DateUtil.getDayOfWeek(it)
            }
        } else {
            agenda.date?.let {
                it.dayOfWeek
            }
        }

        return AgendaViewData(
            agenda.id,
            agenda.date,
            dayOfWeek
        )
    }

    data class TaskViewData(
        override var id: Long? = null,
        var title: String? = "",
        var location: String? = "",
        var hour: Int? = null,
        var minute: Int? = null,
        var duration: Int? = null
    ) : BaseViewData()

    data class AgendaViewData(
        override var id: Long?,
        var date: LocalDate? = null,
        var dayOfWeek: DayOfWeek? = null
    ) : BaseViewData()
}