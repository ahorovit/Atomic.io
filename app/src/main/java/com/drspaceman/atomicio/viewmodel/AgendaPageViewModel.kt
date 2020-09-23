package com.drspaceman.atomicio.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drspaceman.atomicio.model.Agenda
import com.drspaceman.atomicio.model.Habit
import com.drspaceman.atomicio.model.Task
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.ui.BaseDialogFragment
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel.HabitViewData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.temporal.ChronoUnit

class AgendaPageViewModel
@ViewModelInject
constructor(
    atomicIoRepo: AtomicIoRepository,
) : BaseViewModel(atomicIoRepo), SpinnerViewModelInterface {

    private lateinit var agenda: Agenda

    private var allHabits: LiveData<List<HabitViewData>>? = null

    private var _tasks = MediatorLiveData<List<TaskViewData>>()
    val tasks: LiveData<List<TaskViewData>>
        get() = _tasks

    init {
        viewModelScope.launch {
            agenda = atomicIoRepo.getAgendaForDate(LocalDate.now())

            agenda.id?.let { agendaId ->
                _tasks.addSource(Transformations.map(atomicIoRepo.getTasksForAgenda(agendaId)) { repoTasks ->
                    repoTasks.map {
                        TaskViewData.of(it)
                    }
                }) { _tasks.value = it }
            }
        }
    }

    fun getTask(taskId: Long): LiveData<TaskViewData>? {
        val task = MutableLiveData(TaskViewData())

        viewModelScope.launch {
            task.value = TaskViewData.of(atomicIoRepo.getTask(taskId))
        }

        return task
    }

    /**
     * Habit Spinner is a list of parent Identities, so we need to observe with
     * LiveData
     */
    fun getSpinnerItems(): LiveData<List<HabitViewData>>? {
        if (allHabits == null) {
            mapHabitsToHabitViews()
        }

        return allHabits
    }

    // @todo: this is duplicated from IdentityPageViewModel
    private fun mapHabitsToHabitViews() {
        allHabits = Transformations.map(atomicIoRepo.allHabits) { repoHabits ->
            repoHabits.map { HabitViewData.of(it) }
        }
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


    override fun getSpinnerItemResourceId(type: String?): Int? {
        TODO("Not yet implemented")
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