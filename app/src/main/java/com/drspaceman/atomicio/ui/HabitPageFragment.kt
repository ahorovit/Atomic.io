package com.drspaceman.atomicio.ui

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.HabitRecyclerViewAdapter
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel
import kotlinx.android.synthetic.main.fragment_habits.*

class HabitPageFragment : BasePageFragment() {
    override val viewModel by activityViewModels<HabitPageViewModel>()
    override val layoutId: Int = R.layout.fragment_habits

    override fun initializeRecyclerView() {
        habitRecyclerView.layoutManager = LinearLayoutManager(context)
        recyclerViewAdapter = HabitRecyclerViewAdapter(null, this)
        habitRecyclerView.adapter = recyclerViewAdapter

        this.viewModel.getHabits()?.observe(
            viewLifecycleOwner,
            Observer<List<HabitPageViewModel.HabitViewData>> {
                it?.let {
                    recyclerViewAdapter.itemList = it
                }
            }
        )
    }

    override fun getEditDialogFragment(id: Long?): BaseDialogFragment {
        return HabitDetailsFragment.newInstance(id)
    }

    companion object {
        fun newInstance() = HabitPageFragment()
    }
}