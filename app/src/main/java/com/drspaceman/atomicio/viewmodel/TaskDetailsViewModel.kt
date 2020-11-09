package com.drspaceman.atomicio.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel.TaskViewData
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel.HabitViewData
import com.drspaceman.atomicio.viewstate.TaskLoaded
import com.drspaceman.atomicio.viewstate.TaskLoading
import com.drspaceman.atomicio.viewstate.TaskViewState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TaskDetailsViewModel
@ViewModelInject
constructor(
    atomicIoRepo: AtomicIoRepository,
    private val identitiesDelegate: IdentitiesDelegate,
    private val habitsDelegate: HabitsDelegate
) : BaseViewModel(atomicIoRepo),
    HabitsViewModelInterface by habitsDelegate,
    IdentitiesViewModelInterface by identitiesDelegate {

    private val _viewState = MediatorLiveData<TaskViewState>()
    val viewState: LiveData<TaskViewState>
        get() = _viewState

    private val task = MutableLiveData(TaskViewData())

    private var selectedIdentity = MutableLiveData<Long?>(null)
    private var selectedHabit: Long? = null

    private val visibleHabits = MediatorLiveData<List<HabitViewData>>()

    init {
        _viewState.value = TaskLoading
        visibleHabits.value = listOf()

        visibleHabits.addSource(habits) {
            refreshVisibleHabits()
        }

        visibleHabits.addSource(selectedIdentity) {
            refreshVisibleHabits()
        }

        _viewState.addSource(task) {
            refreshViewState()
        }

        _viewState.addSource(visibleHabits) {
            refreshViewState()
        }

        _viewState.addSource(identities) {
            refreshViewState()
        }
    }

    private fun refreshViewState() {

        if (!habitsDelegate.isLoaded || !identitiesDelegate.isLoaded) {
            return
        }

        _viewState.value = TaskLoaded(
            task.value!!,
            identities.value!!,
            visibleHabits.value!!
        )
    }

    private fun refreshVisibleHabits() {
        visibleHabits.value = if (selectedIdentity.value == null) {
            habits.value
        } else {
            habits.value!!.filter { it.identityId == selectedIdentity.value }
        }
    }

    fun loadTask(taskId: Long) = viewModelScope.launch {
        task.value = TaskViewData.of(atomicIoRepo.getTask(taskId))
    }

    override fun clearItem() {
        task.value = TaskViewData()
    }

    fun updateTask(writeTaskView: TaskViewData) {
        GlobalScope.launch {
            atomicIoRepo.updateTask(writeTaskView.toModel())
        }
    }

    fun insertTask(newTaskViewData: TaskViewData) {
        val task = newTaskViewData.toModel()
//        task.agendaId = agenda.id

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
        task.value = task.value?.copy(habitId = habitId)
    }

    fun setIdentity(identityId: Long) {
        // TODO
    }

    fun getNewTaskView(): TaskViewData {
        return TaskViewData()
    }
}