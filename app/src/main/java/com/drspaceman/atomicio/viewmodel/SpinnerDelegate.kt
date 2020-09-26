package com.drspaceman.atomicio.viewmodel

import androidx.lifecycle.ViewModel
import com.drspaceman.atomicio.repository.AtomicIoRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class SpinnerDelegate
@Inject
constructor() : ViewModel(), SpinnerViewModelInterface {
    override fun getSpinnerItemResourceId(type: String?): Int? {
        return AtomicIoRepository.getTypeResourceId(type)
    }
}

interface SpinnerViewModelInterface {
    fun getSpinnerItemResourceId(type: String?): Int?
}