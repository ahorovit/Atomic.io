package com.drspaceman.atomicio.viewmodel

import androidx.lifecycle.ViewModel
import com.drspaceman.atomicio.repository.AtomicIoRepository

abstract class BaseViewModel(
    protected var atomicIoRepo: AtomicIoRepository
): ViewModel() {

    abstract fun deleteItem(itemViewData: BaseViewData)
    abstract fun clearItem()

    abstract class BaseViewData {
        abstract var id: Long?

        abstract fun toModel(): Any
    }
}