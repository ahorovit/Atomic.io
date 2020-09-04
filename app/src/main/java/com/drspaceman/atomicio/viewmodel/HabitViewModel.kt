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
    private var habitView: LiveData<HabitView>? = null

    fun getHabit(habitId: Long): LiveData<HabitView>? {
        if (habitView == null) {
            mapHabitToHabitView(habitId)
        }

        return habitView
    }

    private fun mapHabitToHabitView(habitId: Long) {
        val habit = atomicIoRepo.getLiveHabit(habitId)
        habitView = Transformations.map(habit) { repoHabit ->
            repoHabit?.let {
                HabitView(
                    repoHabit.id,
                    repoHabit.identityId,
                    repoHabit.name,
                    repoHabit.type
                )
            }
        }
    }

    fun getNewHabitView(): HabitView {
        return HabitView()
    }

    fun updateHabit(habitView: HabitView) {
        GlobalScope.launch {
            val habit = habitViewToHabit(habitView)
            atomicIoRepo.updateHabit(habit)
        }
    }

    private fun habitViewToHabit(habitView: HabitView): Habit {
        val habit = habitView.id?.let {
            atomicIoRepo.getHabit(it)
        } ?: atomicIoRepo.createHabit()

        habit.id = habitView.id
        habit.identityId = habitView.identityId
        habit.name = habitView.name
        habit.type = habitView.type

        return habit
    }

    fun insertHabit(habitView: HabitView) {
        val habit = habitViewToHabit(habitView)

        GlobalScope.launch {
            val habitId = atomicIoRepo.addHabit(habit)

            habitId?.let {
                mapHabitToHabitView(it)
            }
        }
    }

    fun getHabits(): LiveData<List<HabitView>> {
        TODO("Not yet implemented")
    }

    data class HabitView(
        var id: Long? = null,
        var identityId: Long? = null,
        var name: String? = "",
        var type: String? = ""
    )
}