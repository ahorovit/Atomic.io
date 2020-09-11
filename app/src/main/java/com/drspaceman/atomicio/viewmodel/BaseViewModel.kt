package com.drspaceman.atomicio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel

abstract class BaseViewModel(application: Application): AndroidViewModel(application) {

    abstract fun deleteItem(itemViewData: BaseViewData)

    abstract class BaseViewData {
        abstract var id: Long?
    }
}