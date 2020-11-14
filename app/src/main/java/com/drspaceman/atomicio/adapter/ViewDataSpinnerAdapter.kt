package com.drspaceman.atomicio.adapter

import android.content.Context
import android.widget.ArrayAdapter
import com.drspaceman.atomicio.viewmodel.BaseViewModel.BaseViewData
import com.drspaceman.atomicio.viewmodel.BaseViewModel.ViewDataStub
import com.drspaceman.atomicio.viewmodel.BaseViewModel.ViewDataStub.Companion.VIEWDATA_STUB_ID

class ViewDataSpinnerAdapter(
    context: Context,
    resource: Int,
    private val defaultLabel: String = ""
): ArrayAdapter<BaseViewData>(context, resource) {

    private var positionMap = mutableMapOf<Long, Int>()

    fun setSpinnerItems(viewData: List<BaseViewData>)
    {
        positionMap.clear()
        clear()

        // Default spinner item
        val defaultItem = ViewDataStub("Select $defaultLabel...")
        positionMap[VIEWDATA_STUB_ID] = 0
        add(defaultItem)

        for (position in viewData.indices) {
            val viewDatum: BaseViewData = viewData[position]

            viewDatum.id?.let {
                positionMap[it] = position + 1
            }
        }

        addAll(viewData)
    }

    fun getPosition(viewDataId: Long): Int {
        return positionMap[viewDataId] ?: 0
    }
}