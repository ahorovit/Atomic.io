package com.drspaceman.atomicio.adapter

import android.content.Context
import android.widget.ArrayAdapter
import com.drspaceman.atomicio.viewmodel.BaseViewModel.BaseViewData

class ViewDataSpinnerAdapter(
    context: Context,
    resource: Int
): ArrayAdapter<BaseViewData>(context, resource) {

    private var positionMap = mutableMapOf<Long, Int>()

    fun setSpinnerItems(viewData: List<BaseViewData>)
    {
        positionMap.clear()
        clear()

        for (position in viewData.indices) {
            val viewDatum: BaseViewData = viewData[position]

            viewDatum.id?.let {
                positionMap[it] = position
            }
        }

        addAll(viewData)
    }

    fun getViewDatumPosition(id: Long): Int? {
        return positionMap[id]
    }
}