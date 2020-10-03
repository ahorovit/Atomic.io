package com.drspaceman.atomicio.ui

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.BaseRecyclerViewAdapter.NestedEditItemListener
import com.drspaceman.atomicio.adapter.HabitListItem
import com.drspaceman.atomicio.adapter.HabitPlaceholderItem
import com.drspaceman.atomicio.adapter.IdentityListHeader
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.android.synthetic.main.fragment_identities.*

@AndroidEntryPoint
class IdentityPageFragment : BasePageFragment(), NestedEditItemListener {

    private var groupAdapter = GroupAdapter<GroupieViewHolder>()

    override val viewModel by activityViewModels<IdentityPageViewModel>()

    override val layoutId: Int = R.layout.fragment_identities

    private var expandedIdentityId: Long? = null
    private var expandedIdentity: IdentityListHeader? = null

    override fun loadPageData() {
        trackExpandedListItem()
        setUpRecyclerView()
    }

    private fun trackExpandedListItem() {
        viewModel.expandedIdentityId.observe(
            viewLifecycleOwner,
            {
                expandedIdentityId = it
            }
        )
    }

    private fun setUpRecyclerView() {
        identityRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }

        viewModel.identityHabits.observe(
            viewLifecycleOwner,
            { identitiesWithHabits ->
                groupAdapter.clear()

                identitiesWithHabits.forEach { identityWithHabits ->
                    // Build expandable Identity Header
                    val isExpanded = identityWithHabits.identity.id == expandedIdentityId
                    val identityHeader = IdentityListHeader(
                        identityWithHabits.identity,
                        this,
                        isExpanded
                    )

                    val group = ExpandableGroup(identityHeader, isExpanded).apply {
                        // Add Child Habits to identity group
                        if (identityWithHabits.habits.isNotEmpty()) {
                            addAll(identityWithHabits.habits.map {
                                HabitListItem(it, this@IdentityPageFragment)
                            })
                        } else {
                            add(HabitPlaceholderItem())
                        }
                    }

                    if (isExpanded) {
                        expandedIdentity = identityHeader
                    }

                    groupAdapter.add(group)
                }
            }
        )
    }

    override fun onToggleExpand(identityListHeader: IdentityListHeader, identityId: Long?) {
        if (identityId != expandedIdentityId) {
            // Collapse current expanded header, and track new header
            expandedIdentity?.collapse()
            expandedIdentity = identityListHeader
            viewModel.setExpandedIdentityId(identityId)
        } else {
            // toggled item is the one currently tracked --> stop tracking
            expandedIdentity = null
            viewModel.clearExpandedIdentityId()
        }
    }

    override fun editSubItemDetails(itemId: Long?) {
        val fragmentManager = activity?.supportFragmentManager ?: return
        val editDetailsFragment = HabitDetailsFragment.newInstance(itemId)
        editDetailsFragment.show(fragmentManager, "${editDetailsFragment::class}_tag")
    }

    override fun getEditDialogFragment(id: Long?): BaseDialogFragment {
        return IdentityDetailsFragment.newInstance(id)
    }

    companion object {
        fun newInstance() = IdentityPageFragment()
    }
}