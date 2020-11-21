package com.drspaceman.atomicio.viewmodel

import androidx.lifecycle.*
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel.HabitViewData
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityRetainedScoped
class HabitsDelegate
@Inject
constructor(
    private val atomicIoRepo: AtomicIoRepository
) : ViewModel(), HabitsViewModelInterface {
    var isLoaded = false

    // @todo: use liveData builder
    private val _habits = MediatorLiveData<List<HabitViewData>>()
    override val habits: LiveData<List<HabitViewData>>
        get() = _habits

    init {
        _habits.value = listOf()

        viewModelScope.launch {
            _habits.addSource(Transformations.map(atomicIoRepo.allHabits) { repoHabits ->
                repoHabits.map { habit ->
                    HabitViewData.of(habit)
                }
            }) { habitViewData ->
                isLoaded = true
                _habits.value = habitViewData
            }
        }
    }
}

interface HabitsViewModelInterface {
    val habits: LiveData<List<HabitViewData>>
}