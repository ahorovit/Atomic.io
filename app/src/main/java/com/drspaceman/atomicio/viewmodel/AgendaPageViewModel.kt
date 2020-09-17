package com.drspaceman.atomicio.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.drspaceman.atomicio.model.Agenda
import com.drspaceman.atomicio.model.Task
import kotlinx.coroutines.launch
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

class AgendaPageViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var agenda: Agenda

    var tasks: LiveData<List<TaskViewData>>? = null
        get() {
            if(field == null) {
                loadTasks()
            }

            return field
        }

    fun loadTasks() {
        viewModelScope.launch {
            agenda = atomicIoRepo.getAgendaForDate(LocalDate.now())

            println(agenda.toString())

            agenda.id?.let{ agendaId ->
                tasks = Transformations.map(atomicIoRepo.getTasksForAgenda(agendaId)) { repoTasks ->
                    repoTasks.map {
                        taskToTaskViewData(it)
                    }
                }
            }
        }
    }

    private fun taskToTaskViewData(task: Task) : TaskViewData
    {
        return TaskViewData(
            task.id,
            task.habitId,
            task.agendaId,
            task.title,
            task.location,
            task.startTime,
            task.duration
        )
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
        var habitId: Long? = null,
        var agendaId: Long? = null,
        var title: String? = "",
        var location: String? = "",
        var startTime: LocalTime? = null,
        var duration: Int? = null
    ) : BaseViewData()

    data class AgendaViewData(
        override var id: Long?,
        var date: LocalDate? = null,
        var dayOfWeek: DayOfWeek? = null
    ) : BaseViewData()
}