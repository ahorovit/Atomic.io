package com.drspaceman.atomicio.viewmodel

import android.app.Application

class AgendaPageViewModel(application: Application) : BaseViewModel(application) {


    override fun deleteItem(itemViewData: BaseViewData) {
        TODO("Not yet implemented")
    }


    data class TaskViewData(
        override var id: Long? = null,
        var title: String? = "",
        var location: String? = "",
        var hour: Int? = null,
        var minute: Int? = null,
        var duration: Int? = null
    ) : BaseViewData()
}