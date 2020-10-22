package com.drspaceman.atomicio.viewstate

import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel

sealed class AgendaViewState

object Loading: AgendaViewState()

data class DayViewLoaded(
    val tasks: List<AgendaPageViewModel.TaskViewData>
): AgendaViewState()

data class ChecklistViewLoaded(
    val tasks: List<AgendaPageViewModel.TaskViewData>
): AgendaViewState()

