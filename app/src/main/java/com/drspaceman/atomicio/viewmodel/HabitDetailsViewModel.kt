package com.drspaceman.atomicio.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.drspaceman.atomicio.isNull
import com.drspaceman.atomicio.model.Task
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel.TaskViewData
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel.HabitViewData
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel.IdentityViewData
import com.drspaceman.atomicio.viewstate.HabitCloseable
import com.drspaceman.atomicio.viewstate.HabitLoaded
import com.drspaceman.atomicio.viewstate.HabitLoading
import com.drspaceman.atomicio.viewstate.HabitViewState
import kotlinx.coroutines.*


class HabitDetailsViewModel
@ViewModelInject
constructor(
    atomicIoRepo: AtomicIoRepository,
    val identitiesDelegate: IdentitiesDelegate,
    val spinnerDelegate: SpinnerDelegate
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
        loadSavedTasks()
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

    private fun refreshViewState(errors: List<String>? = null) {
        _viewState.value = HabitLoaded(
            habit.value ?: HabitViewData(),
            (savedTasks.value ?: listOf()) + pendingTasks,
                errors
        )
    }

    private fun insertHabit(habitViewData: HabitViewData) = GlobalScope.launch {
        atomicIoRepo.addHabit(habitViewData.toModel())
    }

    fun attemptSave(habitToSave: HabitViewData) = viewModelScope.launch {
        _viewState.value = HabitLoading

        val errors = validate(habitToSave)

        if (errors.isEmpty()) {
            _viewState.value = HabitCloseable(save(habitToSave))
        } else {
            refreshViewState(errors)
        }
    }

    private suspend fun save(habitToSave: HabitViewData): Long {

        val deferredId = viewModelScope.async {
            if (habitToSave.id != null) {
                atomicIoRepo.updateHabit(habitToSave.toModel())
                habitToSave.id
            } else {
                atomicIoRepo.addHabit(habitToSave.toModel())
            }
        }

        val savedHabitId  = deferredId.await() as Long

        val tasksToSave = ((savedTasks.value ?: listOf()) + pendingTasks).map {
            it.habitId = savedHabitId
            it.title = habitToSave.name
            it.toModel()
        }

        atomicIoRepo.upsertTasks(tasksToSave)

        return savedHabitId
    }

    private fun validate(habitToSave: HabitViewData): List<String> {
        val errors = mutableListOf<String>()

        if (habitToSave.name.isNullOrEmpty()) {
            errors.add("Habit name cannot be empty")
        }
        if (habitToSave.identityId.isNull()) {
            errors.add("Parent Identity myst be selected")
        }

        val allTasks = (savedTasks.value ?: listOf()) + pendingTasks
        allTasks.forEach { task ->
            if(task.startTime.isNull()) {
                errors.add("All scheduled Tasks must have a Start Time")
            }

            if(task.startTime.isNull()) {
                errors.add("All scheduled Tasks must have an estimated duration")
            }

            if (task.dayFlags.isEmpty()) {
                errors.add("All scheduled Tasks must have at least one day selected")
            }
        }

        return errors.distinct()
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