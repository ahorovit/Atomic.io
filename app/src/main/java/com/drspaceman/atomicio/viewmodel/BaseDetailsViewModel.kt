package com.drspaceman.atomicio.viewmodel

import com.drspaceman.atomicio.repository.AtomicIoRepository
import kotlinx.coroutines.Job

abstract class BaseDetailsViewModel(atomicIoRepo: AtomicIoRepository) :
    BaseViewModel(atomicIoRepo) {

    abstract fun clearContext()

    abstract fun deleteItem(itemViewData: BaseViewData): Job

    protected abstract fun loadExistingItem(id: Long): Job

    fun loadItem(id: Long?) {
        if (id == null) {
            clearContext()
        } else {
            loadExistingItem(id)
        }
    }
}