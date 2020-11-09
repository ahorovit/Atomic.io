package com.drspaceman.atomicio.viewstate

import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel

sealed class TaskViewState

object TaskLoading: TaskViewState()

data class TaskLoaded(
    val task: AgendaPageViewModel.TaskViewData,
    val identities: List<IdentityPageViewModel.IdentityViewData>,
    val habits: List<HabitPageViewModel.HabitViewData>,
): TaskViewState()