package com.drspaceman.atomicio.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.drspaceman.atomicio.model.Agenda
import com.drspaceman.atomicio.model.Habit
import com.drspaceman.atomicio.model.Task
import com.drspaceman.atomicio.ui.BaseDialogFragment
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel.HabitViewData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

class AgendaPageViewModel(
    application: Application
) : BaseViewModel(application), BaseDialogFragment.SpinnerViewModel {

    private lateinit var agenda: Agenda

    private var allHabits: LiveData<List<HabitViewData>>? = null

    var tasks: LiveData<List<TaskViewData>>? = null
        get() {
            if(field == null) {
                loadTasks()
            }

            return field
        }

    private fun loadTasks() {
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
            task.endTime
        )
    }

    fun agendaToAgendaViewData(agenda: Agenda): AgendaViewData {
        return AgendaViewData(
            agenda.id,
            agenda.date,
            agenda.date?.dayOfWeek
        )
    }

    fun getTask(taskId: Long): LiveData<TaskViewData>? {
        TODO("Not yet implemented")
    }


    override fun getSpinnerItemResourceId(type: String?): Int? {
        TODO("Not yet implemented")
    }

    /**
     * Habit Spinner is a list of parent Identities, so we need to observe with
     * LiveData
     */
    fun getSpinnerItems(): LiveData<List<HabitViewData>>?
    {
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
        TODO("Not yet implemented")
    }

    fun insertTask(newTaskViewData: TaskViewData) {
        val task = taskViewDataToTask(newTaskViewData)

        GlobalScope.launch {
            val taskId = atomicIoRepo.addTask(task)

            // @todo sync with a LiveData object?
        }

    }

    private fun taskViewDataToTask(taskViewData: TaskViewData): Task {
        return Task(
            taskViewData.id,
            taskViewData.habitId,
            taskViewData.agendaId,
            taskViewData.title,
            taskViewData.location,
            taskViewData.startTime,
            taskViewData.endTime
        )
    }

    override fun deleteItem(itemViewData: BaseViewData) {
        GlobalScope.launch {
            val task = taskViewDataToTask(itemViewData as TaskViewData)
            atomicIoRepo.deleteTask(task)
        }
    }

    data class TaskViewData(
        override var id: Long? = null,
        var habitId: Long? = null,
        var agendaId: Long? = null,
        var title: String? = "",
        var location: String? = "",
        var startTime: LocalTime? = null,
        var endTime: LocalTime? = null
    ) : BaseViewData()

    data class AgendaViewData(
        override var id: Long?,
        var date: LocalDate? = null,
        var dayOfWeek: DayOfWeek? = null
    ) : BaseViewData()
}