package com.drspaceman.atomicio.ui

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.HabitRecyclerViewAdapter
import com.drspaceman.atomicio.viewmodel.BaseViewModel
import com.drspaceman.atomicio.viewmodel.HabitViewModel

import kotlinx.android.synthetic.main.fragment_agenda.*
import java.util.*

class AgendaFragment : BasePageFragment() {
    override val layoutId: Int = R.layout.fragment_agenda

    override val viewModel: BaseViewModel
        get() = TODO("Not yet implemented")

    private var habitSequence: MutableList<HabitViewModel.HabitViewData>? = null


    override fun getEditDialogFragment(id: Long?): BaseDialogFragment {
        TODO("Not yet implemented")
    }

    override fun initializeRecyclerView() {
        initializeData()
        val sequence = habitSequence ?: return

        habitSequenceRecyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerViewAdapter = HabitRecyclerViewAdapter(sequence, this)
        habitSequenceRecyclerView.adapter = recyclerViewAdapter

//        attachItemTouchHelper()
        }



    // @todo: implement DB and LiveData for habitSequence
    private fun initializeData() {
        habitSequence = mutableListOf(
            HabitViewModel.HabitViewData(name="wake up", type="sleep"),
            HabitViewModel.HabitViewData(name="eat breakfast", type="diet"),
            HabitViewModel.HabitViewData(name="walk dog", type="exercise")
        )
    }

    /**
     * WIP: Make habitSequence items draggable for reordering
     */
    private fun attachItemTouchHelper() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                dragged: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                habitSequence?.let {
                    val positionDragged = dragged.adapterPosition
                    val positionTarget = target.adapterPosition

                    Collections.swap(it, positionDragged, positionTarget)

                    recyclerViewAdapter?.notifyItemMoved(positionDragged, positionTarget)
                }

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                TODO("Not yet implemented")
            }

        })

        itemTouchHelper.attachToRecyclerView(habitSequenceRecyclerView)
    }

    companion object {
        fun newInstance() = AgendaFragment()
    }
}