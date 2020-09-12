package com.drspaceman.atomicio.adapter

import android.content.Context
import android.widget.ArrayAdapter
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel.IdentityViewData

class IdentitySpinnerAdapter(
    context: Context,
    resource: Int
): ArrayAdapter<IdentityViewData>(context, resource) {

    private var positionMap = mutableMapOf<Long, Int>()

    fun setSpinnerItems(identities: List<IdentityViewData>)
    {
        positionMap.clear()
        clear()

        for (position in identities.indices) {
            val identity: IdentityViewData = identities[position]

            identity.id?.let {
                positionMap[it] = position
            }
        }

        addAll(identities)
    }

    fun getIdentityPosition(id: Long): Int? {
        return positionMap[id]
    }
}