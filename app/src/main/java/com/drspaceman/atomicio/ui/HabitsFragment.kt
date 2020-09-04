package com.drspaceman.atomicio.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.HabitRecyclerViewAdapter
import com.drspaceman.atomicio.viewmodel.HabitViewModel
import kotlinx.android.synthetic.main.fragment_habits.*

class HabitsFragment : Fragment() {
    private lateinit var habitRecyclerViewAdapter: HabitRecyclerViewAdapter
    private val habitViewModel by viewModels<HabitViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_habits, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeHabitRecyclerView()

        fab.setOnClickListener {
            showHabitDetailsFragment(null)
        }
    }

    private fun initializeHabitRecyclerView() {
        habitRecyclerView.layoutManager = LinearLayoutManager(context)
        habitRecyclerViewAdapter = HabitRecyclerViewAdapter(null, this)
        habitRecyclerView.adapter = habitRecyclerViewAdapter

        habitViewModel.getHabits()?.observe(
            viewLifecycleOwner,
            Observer<List<HabitViewModel.HabitView>> {
                it?.let {
                    habitRecyclerViewAdapter.setHabitData(it)
                }
            }
        )
    }

    override fun editHabitDetails(identityId: Long?) {
        showHabitDetailsFragment(identityId)
    }


    private fun showHabitDetailsFragment(identityId: Long?) {
        val fragmentManager = activity?.supportFragmentManager ?: return
        val identityDetailsFragment = IdentityDetailsFragment.newInstance(identityId)
        identityDetailsFragment.show(fragmentManager, IdentityDetailsFragment.TAG)
    }


    companion object {
        fun newInstance() = HabitsFragment()
    }
}