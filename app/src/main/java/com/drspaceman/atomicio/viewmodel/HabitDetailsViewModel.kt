package com.drspaceman.atomicio.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel.TaskViewData
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel.HabitViewData
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel.IdentityViewData
import com.drspaceman.atomicio.viewstate.HabitLoaded
import com.drspaceman.atomicio.viewstate.HabitViewState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HabitDetailsViewModel
@ViewModelInject
constructor(
    atomicIoRepo: AtomicIoRepository,
    private val identitiesDelegate: IdentitiesDelegate,
    private val spinnerDelegate: SpinnerDelegate
) : BaseDetailsViewModel(atomicIoRepo),
    IdentitiesViewModelInterface by identitiesDelegate,
    SpinnerViewModelInterface by spinnerDelegate {

    private val habit = MutableLiveData(HabitViewData())

    // TODO: this probably isn't the right way to do this
    private val newHabitId = MutableLiveData<Long?>()

    private val savedTasks = MutableLiveData<List<TaskViewData>>()

    private fun loadSavedTasks() {
        val habitId = habit.value?.id

        if (habitId == null) {
            savedTasks.value = listOf()
        } else {
            viewModelScope.launch {
                savedTasks.value = atomicIoRepo.loadTasksForHabit(habitId).map { TaskViewData.of(it) }
            }
        }
    }

    // User may schedule tasks for a new habit that hasn't been saved yet
    private val pendingTasks = mutableListOf<TaskViewData>()

    private val _viewState = MediatorLiveData<HabitViewState>().apply {
        addSource(habit) {
            refreshViewState()
        }

        addSource(savedTasks) {
            refreshViewState()
        }
    }

    val viewState: LiveData<HabitViewState>
        get() = _viewState


    init {
        loadSavedTasks()
    }

    fun getSpinnerItems(): LiveData<List<IdentityViewData>> {
        return identitiesDelegate.identities
    }

    // TODO: call this function when creating new Habit from Identity
    // Maybe delete?
    fun getHabit(habitId: Long?, identityId: Long?): LiveData<HabitViewData> {
        if (habitId != null) {
            loadExistingItem(habitId)
        } else if (identityId != null) {
            habit.value = HabitViewData(identityId = identityId)
        }

        return habit
    }

    override fun loadExistingItem(id: Long) = viewModelScope.launch {
        habit.value = HabitViewData.of(atomicIoRepo.getHabit(id))
    }

    override fun deleteItem(itemViewData: BaseViewData) = GlobalScope.launch {
        itemViewData.id?.let {
            atomicIoRepo.deleteHabit((itemViewData as HabitViewData).toModel())
        }
    }


    fun addNewTask() {
        pendingTasks.add(TaskViewData())
        refreshViewState()
    }

    private fun refreshViewState() {
        _viewState.value = HabitLoaded(
            habit.value ?: HabitViewData(),
            (savedTasks.value ?: listOf()) + pendingTasks
        )
    }

    private fun insertHabit(habitViewData: HabitViewData) = GlobalScope.launch {
        atomicIoRepo.addHabit(habitViewData.toModel())
    }

    fun saveHabit(habitViewData: HabitViewData) {
        if (habitViewData.id != null) {
            updateHabit(habitViewData)
        } else {
            insertHabit(habitViewData)
        }
    }

    private fun updateHabit(habitViewData: HabitViewData) = GlobalScope.launch {
        val habit = habitViewData.toModel()
        atomicIoRepo.updateHabit(habit)
    }

    fun saveHabitForReturnId(habitViewData: HabitViewData): LiveData<Long?> {
        viewModelScope.launch {
            newHabitId.value = atomicIoRepo.addHabit(habitViewData.toModel())
        }

        return newHabitId
    }


    override fun clearContext() {
        habit.value = HabitViewData()
        newHabitId.value = null
        pendingTasks.clear()
    }
}