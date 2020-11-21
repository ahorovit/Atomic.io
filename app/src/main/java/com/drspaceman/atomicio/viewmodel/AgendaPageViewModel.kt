package com.drspaceman.atomicio.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drspaceman.atomicio.model.Agenda
import com.drspaceman.atomicio.model.Task
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.viewstate.AgendaViewState
import com.drspaceman.atomicio.viewstate.ChecklistViewLoaded
import com.drspaceman.atomicio.viewstate.DayViewLoaded
import com.drspaceman.atomicio.viewstate.AgendaLoading
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

    private val _viewState = MediatorLiveData<AgendaViewState>().apply {
        value = AgendaLoading

        viewModelScope.launch {
            agenda = atomicIoRepo.getAgendaForDate(LocalDate.now())

//            _viewState.addSource(Transformations.map(atomicIoRepo.getTasksForAgenda(agenda.id!!)) { repoTasks ->
//                repoTasks.map { TaskViewData.of(it) }
//            }) { refreshViewState(it) }
        }
    }


    val viewState: LiveData<AgendaViewState>
        get() = _viewState

    private lateinit var agenda: Agenda


    // @todo: remove
    private val _task = MutableLiveData(TaskViewData())
    val task
        get() = _task

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

    // @todo: remove
    fun loadTask(taskId: Long) = viewModelScope.launch {
        _task.value = TaskViewData.of(atomicIoRepo.getTask(taskId))
    }

    // @todo: remove
    override fun clearContext() {
        _task.value = getNewTaskView()
    }

    // @todo: remove
    fun getNewTaskView(): TaskViewData {
        return TaskViewData()
    }

    // @todo: remove
    fun updateTask(writeTaskView: TaskViewData) {
        GlobalScope.launch {
            atomicIoRepo.updateTask(writeTaskView.toModel())
        }
    }

    // @todo: remove
    fun insertTask(newTaskViewData: TaskViewData) {
        val task = newTaskViewData.toModel()

        GlobalScope.launch {
            atomicIoRepo.addTask(task)
        }
    }

    // @todo: remove
    override fun deleteItem(itemViewData: BaseViewData) {
        GlobalScope.launch {
            val task = (itemViewData as TaskViewData).toModel()
            atomicIoRepo.deleteTask(task)
        }
    }

    // @todo: remove
    fun setParentHabit(habitId: Long) {
        _task.value = _task.value?.copy(habitId = habitId)
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