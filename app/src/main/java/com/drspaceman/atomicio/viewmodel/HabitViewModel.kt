package com.drspaceman.atomicio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.drspaceman.atomicio.model.Habit
import com.drspaceman.atomicio.repository.AtomicIoRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    // @TODO: insert as Explicit Dependency
    private var atomicIoRepo = AtomicIoRepository(getApplication())
    private var habitView: LiveData<HabitViewData>? = null

    fun getHabit(habitId: Long): LiveData<HabitViewData>? {
        if (habitView == null) {
            mapHabitToHabitView(habitId)
        }

        return habitView
    }

    private fun mapHabitToHabitView(habitId: Long) {
        val habit = atomicIoRepo.getLiveHabit(habitId)
        habitView = Transformations.map(habit) { repoHabit ->
            repoHabit?.let {
                HabitViewData(
                    repoHabit.id,
                    repoHabit.identityId,
                    repoHabit.name,
                    repoHabit.type
                )
            }
        }
    }

    fun getNewHabitView(): HabitViewData {
        return HabitViewData()
    }

    fun updateHabit(habitView: HabitViewData) {
        GlobalScope.launch {
            val habit = habitViewToHabit(habitView)
            atomicIoRepo.updateHabit(habit)
        }
    }

    private fun habitViewToHabit(habitView: HabitViewData): Habit {
        val habit = habitView.id?.let {
            atomicIoRepo.getHabit(it)
        } ?: atomicIoRepo.createHabit()

        habit.id = habitView.id
        habit.identityId = habitView.identityId
        habit.name = habitView.name
        habit.type = habitView.type

        return habit
    }

    fun insertHabit(habitView: HabitViewData) {
        val habit = habitViewToHabit(habitView)

        GlobalScope.launch {
            val habitId = atomicIoRepo.addHabit(habit)

            habitId?.let {
                mapHabitToHabitView(it)
            }
        }
    }

    fun getHabits(): LiveData<List<HabitViewData>> {
        TODO("Not yet implemented")
    }

    data class HabitViewData(
        var id: Long? = null,
        var identityId: Long? = null,
        var name: String? = "",
        var type: String? = ""
    ) : BaseViewModel.BaseViewData()
}