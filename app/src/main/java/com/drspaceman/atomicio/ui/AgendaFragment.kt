package com.drspaceman.atomicio.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.HabitRecyclerViewAdapter
import com.drspaceman.atomicio.viewmodel.HabitViewModel

import kotlinx.android.synthetic.main.fragment_agenda.*
import java.util.*

class AgendaFragment : Fragment() {
    private var habitSequence: MutableList<HabitViewModel.HabitView>? = null
    private var habitRecyclerViewAdapter: HabitRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // @todo: required if adding to the options menu
//        setHasOptionsMenu(true)
    }

    // @todo: add options to menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    // @todo handle menu option selection
    // NOTE: Activity will run onOptionsItemSelected first, and must not handle items specific to this fragment
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_agenda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeData()
        initializeHabitSequenceRecyclerView(view)

    }

    override fun onPause() {
        super.onPause()
    }

    // @todo: implement DB and LiveData for habitSequence
    private fun initializeData() {
        habitSequence = mutableListOf(
            HabitViewModel.HabitView(name="wake up", type="sleep"),
            HabitViewModel.HabitView(name="eat breakfast", type="diet"),
            HabitViewModel.HabitView(name="walk dog", type="exercise")
        )
    }

    private fun initializeHabitSequenceRecyclerView(view: View) {
        val sequence = habitSequence ?: return

        habitSequenceRecyclerView.layoutManager = LinearLayoutManager(this.context)
        habitRecyclerViewAdapter = HabitRecyclerViewAdapter(sequence)
        habitSequenceRecyclerView.adapter = habitRecyclerViewAdapter

//        attachItemTouchHelper()
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

                    habitRecyclerViewAdapter?.notifyItemMoved(positionDragged, positionTarget)
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