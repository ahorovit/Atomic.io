package com.drspaceman.atomicio.viewstate

import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel

sealed class IdentityViewState

object IdentityLoading: IdentityViewState()

data class IdentityLoaded(
    val identity: IdentityPageViewModel.IdentityViewData
): IdentityViewState()

