package com.drspaceman.atomicio.viewstate

import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel

sealed class HabitViewState

object HabitLoading: HabitViewState()

data class HabitLoaded(
    val habit: HabitPageViewModel.HabitViewData,
    val tasks: List<AgendaPageViewModel.TaskViewData>,
    val errors: List<String>?
): HabitViewState()

data class HabitCloseable(val savedHabitId: Long): HabitViewState()