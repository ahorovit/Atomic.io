package com.drspaceman.atomicio.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drspaceman.atomicio.model.Agenda
import com.drspaceman.atomicio.model.Task
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.viewstate.AgendaViewState
import com.drspaceman.atomicio.viewstate.ChecklistViewLoaded
import com.drspaceman.atomicio.viewstate.DayViewLoaded
import com.drspaceman.atomicio.viewstate.Loading
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.temporal.ChronoUnit

class AgendaPageViewModel
@ViewModelInject
constructor(
    atomicIoRepo: AtomicIoRepository,
    private val habitsDelegate: HabitsDelegate,
) : BaseViewModel(atomicIoRepo), HabitsViewModelInterface by habitsDelegate {

    private val _viewState = MediatorLiveData<AgendaViewState>()
    val viewState: LiveData<AgendaViewState>
        get() = _viewState

    private lateinit var agenda: Agenda

    private val _task = MutableLiveData(TaskViewData())
    val task
        get() = _task

    init {
        _viewState.value = Loading

        viewModelScope.launch {
            agenda = atomicIoRepo.getAgendaForDate(LocalDate.now())

            _viewState.addSource(Transformations.map(atomicIoRepo.getTasksForAgenda(agenda.id!!)) { repoTasks ->
                repoTasks.map { TaskViewData.of(it) }
            }) { refreshViewState(it) }
        }
    }

    private fun refreshViewState(tasks: List<TaskViewData>) {
        val state = _viewState.value!!

        _viewState.value = when (state) {
            Loading -> getDefaultViewState(tasks)
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
            Loading -> Loading
            is DayViewLoaded -> ChecklistViewLoaded(state.tasks)
            is ChecklistViewLoaded -> DayViewLoaded(state.tasks)
        }
    }

    fun loadTask(taskId: Long) = viewModelScope.launch {
        _task.value = TaskViewData.of(atomicIoRepo.getTask(taskId))
    }

    override fun clearItem() {
        _task.value = getNewTaskView()
    }

    fun getNewTaskView(): TaskViewData {
        return TaskViewData()
    }

    fun updateTask(writeTaskView: TaskViewData) {
        GlobalScope.launch {
            atomicIoRepo.updateTask(writeTaskView.toModel())
        }
    }

    fun insertTask(newTaskViewData: TaskViewData) {
        val task = newTaskViewData.toModel()
        task.agendaId = agenda.id

        GlobalScope.launch {
            atomicIoRepo.addTask(task)
        }
    }

    override fun deleteItem(itemViewData: BaseViewData) {
        GlobalScope.launch {
            val task = (itemViewData as TaskViewData).toModel()
            atomicIoRepo.deleteTask(task)
        }
    }

    fun setParentHabit(habitId: Long) {
        _task.value = _task.value?.copy(habitId = habitId)
    }

    data class TaskViewData(
        override var id: Long? = null,
        var habitId: Long? = null,
        var agendaId: Long? = null,
        var title: String? = "",
        var location: String? = "",
        var startTime: LocalTime? = null,
        var endTime: LocalTime? = null
    ) : BaseViewData() {
        override fun toString() = title ?: ""

        override fun toModel() = Task(
            id,
            habitId,
            agendaId,
            title,
            location,
            startTime,
            endTime
        )

        fun getDuration(): Int? {
            return startTime?.until(endTime, ChronoUnit.MINUTES)?.toInt()
        }

        companion object {
            fun of(task: Task) = TaskViewData(
                task.id,
                task.habitId,
                task.agendaId,
                task.title,
                task.location,
                task.startTime,
                task.endTime
            )
        }
    }
}