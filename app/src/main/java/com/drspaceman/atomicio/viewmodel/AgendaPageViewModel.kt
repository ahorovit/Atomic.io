package com.drspaceman.atomicio.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drspaceman.atomicio.model.Task
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.viewstate.AgendaViewState
import com.drspaceman.atomicio.viewstate.ChecklistViewLoaded
import com.drspaceman.atomicio.viewstate.DayViewLoaded
import com.drspaceman.atomicio.viewstate.AgendaLoading
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

class AgendaPageViewModel
@ViewModelInject
constructor(
    atomicIoRepo: AtomicIoRepository,
    private val habitsDelegate: HabitsDelegate,
) : BaseViewModel(atomicIoRepo), HabitsViewModelInterface by habitsDelegate {

    private lateinit var tasks: LiveData<List<Task>>

    private val _viewState = MediatorLiveData<AgendaViewState>().apply {
        value = AgendaLoading

        viewModelScope.launch {
            tasks = atomicIoRepo.loadTasksForDay(LocalDate.now().dayOfWeek)

            addSource(tasks) { repoTasks ->
                refreshViewState(repoTasks.map { TaskViewData.of(it) })
            }
        }
    }

    val viewState: LiveData<AgendaViewState>
        get() = _viewState

    private fun refreshViewState(tasks: List<TaskViewData>) {
        val state = _viewState.value!!

        _viewState.value = when (state) {
            AgendaLoading -> getDefaultViewState(tasks)
            is DayViewLoaded -> state.copy(tasks = tasks)
            is ChecklistViewLoaded -> state.copy(tasks = tasks)
        }
    }

    // TODO: access Settings for preferred view
    private fun getDefaultViewState(tasks: List<TaskViewData>): AgendaViewState {
        return DayViewLoaded(tasks)
    }

    fun toggleAgendaView() {
        val state = _viewState.value!!

        _viewState.value = when (state) {
            AgendaLoading -> AgendaLoading
            is DayViewLoaded -> ChecklistViewLoaded(state.tasks)
            is ChecklistViewLoaded -> DayViewLoaded(state.tasks)
        }
    }

    // @todo: move to TaskDetailsViewModel
    data class TaskViewData(
        override var id: Long? = null,
        var habitId: Long? = null,
        var title: String? = "",
        var startTime: LocalTime? = null,
        var duration: Int? = null
    ) : BaseViewData() {
        override fun toString() = title ?: ""

        override fun toModel() = Task(
            id,
            habitId,
            title,
            startTime,
            duration
        )

        companion object {
            fun of(task: Task) = TaskViewData(
                task.id,
                task.habitId,
                task.name,
                task.startTime,
            )
        }
    }
}