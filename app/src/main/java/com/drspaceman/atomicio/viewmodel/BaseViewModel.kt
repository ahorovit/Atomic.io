package com.drspaceman.atomicio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.drspaceman.atomicio.repository.AtomicIoRepository

abstract class BaseViewModel(application: Application): AndroidViewModel(application) {

    protected var atomicIoRepo = AtomicIoRepository(this.getApplication())

    abstract fun deleteItem(itemViewData: BaseViewData)

    abstract class BaseViewData {
        abstract var id: Long?

        abstract fun toModel(): Any
    }
}