package com.drspaceman.atomicio.viewmodel

import androidx.lifecycle.ViewModel
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.ui.BaseDialogFragment
import java.lang.Exception

abstract class BaseViewModel(
    protected var atomicIoRepo: AtomicIoRepository
): ViewModel() {

    abstract fun deleteItem(itemViewData: BaseViewData)
    abstract fun clearItem()

    abstract class BaseViewData {
        abstract var id: Long?

        abstract fun toModel(): Any
    }

    // Enables spinner default item (eg "Select Identity...")
    data class ViewDataStub(
        val label: String
    ): BaseViewData(), BaseDialogFragment.SpinnerItemViewData {
        override var id: Long? = VIEWDATA_STUB_ID
        override var type: String? = VIEWDATA_STUB_TYPE
        override var typeResourceId: Int = VIEWDATA_STUB_IMAGE

        override fun toModel(): Any {
            throw Exception("ViewDataStub has no corresponding model")
        }

        override fun toString(): String {
            return label
        }

        companion object {
            const val VIEWDATA_STUB_ID = -1L
            const val VIEWDATA_STUB_TYPE = "Other"
            const val VIEWDATA_STUB_IMAGE = R.drawable.ic_other
        }
    }
}