package com.drspaceman.atomicio.ui

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.HabitListItem
import com.drspaceman.atomicio.adapter.IdentityListFooter
import com.drspaceman.atomicio.adapter.IdentityListHeader
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.android.synthetic.main.fragment_identities.*

@AndroidEntryPoint
class IdentityPageFragment : BasePageFragment() {

    private var groupAdapter = GroupAdapter<GroupieViewHolder>()

    override val viewModel by activityViewModels<IdentityPageViewModel>()

    override val layoutId: Int = R.layout.fragment_identities

    override fun loadPageData() {
        identityRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }

        viewModel.identityHabits.observe(
            viewLifecycleOwner,
            { identitiesWithHabits ->

                groupAdapter.clear() // @todo: can we edit instead of starting fresh?

                identitiesWithHabits.forEach { identityWithHabit ->



                    val group = ExpandableGroup(
                        IdentityListHeader(identityWithHabit.identity, this)
                    ).apply {

                        val habitList = identityWithHabit.habits.map {
                            HabitListItem(it, this@IdentityPageFragment)
                        }


//                        val items = mutableListOf<HabitListItem>()
//                        for (habit in identityWithHabit.habits) {
//                            items.add(HabitListItem(habit, this@IdentityPageFragment))
//                        }
                        addAll(habitList)

                        if (habitList.isEmpty()) {
                            add(IdentityListFooter(identityWithHabit.identity, this@IdentityPageFragment))
                        }
                    }

                    groupAdapter.add(group)
                }
            }
        )
    }

    override fun getEditDialogFragment(id: Long?): BaseDialogFragment {
        return IdentityDetailsFragment.newInstance(id)
    }

    companion object {
        fun newInstance() = IdentityPageFragment()
    }
}