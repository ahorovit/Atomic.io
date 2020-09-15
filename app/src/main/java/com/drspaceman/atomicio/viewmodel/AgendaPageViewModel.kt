package com.drspaceman.atomicio.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.drspaceman.atomicio.model.Agenda
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate

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

    private fun loadOrCreateAgenda(date: LocalDate): Agenda
    {
        TODO("Not yet implemented")

//        viewModelScope.launch {
//            val agenda: Agenda = atomicIoRepo.getAgendaForDate(date)
//
//
//        }
    }


    override fun deleteItem(itemViewData: BaseViewData) {
        TODO("Not yet implemented")
    }

    fun agendaToAgendaViewData(agenda: Agenda): AgendaViewData {
        return AgendaViewData(
            agenda.id,
            agenda.date,
            agenda.date?.dayOfWeek
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