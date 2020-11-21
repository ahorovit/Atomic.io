package com.drspaceman.atomicio.viewstate

import com.drspaceman.atomicio.viewmodel.HabitPageViewModel

sealed class HabitViewState

object HabitLoading: HabitViewState()

data class HabitLoaded(
    val habit: HabitPageViewModel.HabitViewData
): HabitViewState()