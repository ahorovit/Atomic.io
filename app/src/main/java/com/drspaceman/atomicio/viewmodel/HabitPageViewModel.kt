package com.drspaceman.atomicio.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.model.Habit
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.ui.BaseDialogFragment
import com.drspaceman.atomicio.ui.BaseDialogFragment.SpinnerItemViewData
import com.drspaceman.atomicio.ui.BaseDialogFragment.SpinnerViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HabitPageViewModel(application: Application) : BaseViewModel(application), SpinnerViewModel {

    // @TODO: insert as Explicit Dependency
    private var atomicIoRepo = AtomicIoRepository(getApplication())
    private var oneHabit: LiveData<HabitViewData>? = null
    private var allHabits: LiveData<List<HabitViewData>>? = null

    override fun getSpinnerItems(): List<String> {
        TODO("Not yet implemented")
    }

    override fun getSpinnerItemResourceId(type: String?): Int? {
        TODO("Not yet implemented")
    }

    fun getHabit(habitId: Long): LiveData<HabitViewData>? {
        if (oneHabit == null) {
            mapHabitToHabitViewData(habitId)
        }

        return oneHabit
    }

    private fun mapHabitToHabitViewData(habitId: Long) {
        val habit = atomicIoRepo.getLiveHabit(habitId)
        oneHabit = Transformations.map(habit) { repoHabit ->
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

    fun getHabits(): LiveData<List<HabitViewData>>? {
        if (allHabits == null) {
            mapAllHabitsToHabitViewData()
        }

        return allHabits
    }

    private fun mapAllHabitsToHabitViewData() {
        allHabits = Transformations.map(atomicIoRepo.allHabits) { repoHabits ->
            repoHabits.map { habitToHabitViewData(it) }
        }
    }

    fun getNewHabitView(): HabitViewData {
        return HabitViewData()
    }

    fun updateHabit(habitView: HabitViewData) {
        GlobalScope.launch {
            val habit = habitViewDataToHabit(habitView)
            atomicIoRepo.updateHabit(habit)
        }
    }

    fun insertHabit(habitView: HabitViewData) {
        val habit = habitViewDataToHabit(habitView)

        GlobalScope.launch {
            val habitId = atomicIoRepo.addHabit(habit)

            habitId?.let {
                mapHabitToHabitViewData(it)
            }
        }
    }


    override fun deleteItem(itemViewData: BaseViewData) {
        TODO("Not yet implemented")
    }

    private fun habitViewDataToHabit(habitView: HabitViewData): Habit {
        val habit = habitView.id?.let {
            atomicIoRepo.getHabit(it)
        } ?: atomicIoRepo.createHabit()

        habit.id = habitView.id
        habit.identityId = habitView.identityId
        habit.name = habitView.name
        habit.type = habitView.type

        return habit
    }

    private fun habitToHabitViewData(habit: Habit): HabitViewData {
        return HabitViewData(
            habit.id,
            habit.identityId,
            habit.name,
            habit.type,
            atomicIoRepo.getTypeResourceId(habit.type)
        )
    }

    data class HabitViewData(
        override var id: Long? = null,
        var identityId: Long? = null,
        var name: String? = "",
        override var type: String? = "",
        override var typeResourceId: Int = R.drawable.ic_other
    ) : BaseViewData(), SpinnerItemViewData

}