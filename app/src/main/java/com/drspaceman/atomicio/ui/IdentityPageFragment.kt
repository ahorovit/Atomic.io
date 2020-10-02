package com.drspaceman.atomicio.ui

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.HabitListItem
import com.drspaceman.atomicio.adapter.IdentityListHeader
import com.drspaceman.atomicio.ui.decorator.SwipeTouchCallback
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.TouchCallback
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.android.synthetic.main.fragment_identities.*

@AndroidEntryPoint
class IdentityPageFragment : BasePageFragment() {

    private var groupAdapter = GroupAdapter<GroupieViewHolder>()

    override val viewModel by activityViewModels<IdentityPageViewModel>()

    override val layoutId: Int = R.layout.fragment_identities

    private val touchCallback: TouchCallback by lazy {
        object : SwipeTouchCallback() {

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                val item = groupAdapter.getItem(viewHolder.adapterPosition)
                groupAdapter.remove(item)
            }
        }
    }

    override fun loadPageData() {
        identityRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
            ItemTouchHelper(touchCallback).attachToRecyclerView(this)
        }

        viewModel.identityHabits.observe(
            viewLifecycleOwner
        ) { identitiesWithHabits ->

            groupAdapter.clear() // @todo: can we edit instead of starting fresh?

            identitiesWithHabits.forEach {
                val group = ExpandableGroup(
                    IdentityListHeader(it.identity, this)
                ).apply {
                    for (habit in it.habits) {
                        add(HabitListItem(habit, this@IdentityPageFragment))
                    }
                }

                groupAdapter.add(group)
            }
        }
    }

    override fun getEditDialogFragment(id: Long?): BaseDialogFragment {
        return IdentityDetailsFragment.newInstance(id)
    }

    companion object {
        fun newInstance() = IdentityPageFragment()
    }
}