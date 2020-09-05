package com.drspaceman.atomicio.ui

import androidx.fragment.app.Fragment
import com.drspaceman.atomicio.adapter.BaseRecyclerViewAdapter

abstract class PageFragment : Fragment(), BaseRecyclerViewAdapter.EditItemListener {
    abstract var recyclerViewAdapter: BaseRecyclerViewAdapter

}