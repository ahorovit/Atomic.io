package com.drspaceman.atomicio.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.drspaceman.atomicio.model.Agenda
import com.drspaceman.atomicio.model.Habit
import com.drspaceman.atomicio.model.Task
import com.drspaceman.atomicio.ui.BaseDialogFragment
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel.HabitViewData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.temporal.ChronoUnit

class AgendaPageViewModel(
    application: Application
) : BaseViewModel(application), BaseDialogFragment.SpinnerViewModel {

    private lateinit var agenda: Agenda

    private var allHabits: LiveData<List<HabitViewData>>? = null

    var tasks: LiveData<List<TaskViewData>>? = null
        get() {
            if (field == null) {
                loadTasks()
            }

            return field
        }

    private fun loadTasks() {
        viewModelScope.launch {
            agenda = atomicIoRepo.getAgendaForDate(LocalDate.now())

            agenda.id?.let { agendaId ->
                tasks = Transformations.map(atomicIoRepo.getTasksForAgenda(agendaId)) { repoTasks ->
                    repoTasks.map {
                        TaskViewData.of(it)
                    }
                }
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
            repoHabits.map { habitToHabitView(it) }
        }
    }

    // @todo: this is duplicated from IdentityPageViewModel
    private fun habitToHabitView(habit: Habit): HabitViewData {
        return HabitViewData(
            habit.id,
            habit.identityId,
            habit.name,
            habit.type,
            atomicIoRepo.getTypeResourceId(habit.type)
        )
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

        fun toModel() = Task(
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