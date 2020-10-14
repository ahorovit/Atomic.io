package com.drspaceman.atomicio.ui

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.HabitRecyclerViewAdapter
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_habits.*

@AndroidEntryPoint
class HabitPageFragment : BasePageFragment() {
    override val viewModel by activityViewModels<HabitPageViewModel>()
    override val layoutId: Int = R.layout.fragment_habits

    override val fragmentTitle = "Habits"

    override fun loadPageData() {
        habitRecyclerView.layoutManager = LinearLayoutManager(context)
        recyclerViewAdapter = HabitRecyclerViewAdapter(null, this)
        habitRecyclerView.adapter = recyclerViewAdapter

        this.viewModel.habits.observe(
            viewLifecycleOwner,
            {
                recyclerViewAdapter.itemList = it
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