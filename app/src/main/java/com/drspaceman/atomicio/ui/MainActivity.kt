package com.drspaceman.atomicio.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.HabitRecyclerViewAdapter
import com.drspaceman.atomicio.adapter.IdentityRecyclerViewAdapter
import com.drspaceman.atomicio.viewmodel.HabitViewModel
import com.drspaceman.atomicio.viewmodel.IdentityViewModel

import kotlinx.android.synthetic.main.activity_main.drawerLayout
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.drawer_view_main.*
import kotlinx.android.synthetic.main.habit_sequence.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity() {
    private var identityRecyclerViewAdapter: IdentityRecyclerViewAdapter? = null
    private val identityViewModel by viewModels<IdentityViewModel>()

    // @todo: Move into Fragment
    private var habitSequence: MutableList<HabitViewModel.HabitViewData>? = null
    private var habitRecyclerViewAdapter: HabitRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()

//        fab.setOnClickListener { view ->
//
//        }

        initializeData()
        initializeIdentityRecyclerView()
        initializeHabitSequenceRecyclerView()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        toggle.syncState()

        addIdentityButton.setOnClickListener {
            startIdentityDetails(null)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    // @todo: implement DB and LiveData for habitSequence
    private fun initializeData() {
        habitSequence = mutableListOf(
            HabitViewModel.HabitViewData("wake up", "sleep", 0),
            HabitViewModel.HabitViewData("eat breakfast", "diet", 1),
            HabitViewModel.HabitViewData("walk dog", "exercise", 2)
        )
    }

    private fun initializeHabitSequenceRecyclerView() {
        val sequence = habitSequence ?: return

        habitSequenceRecyclerView.layoutManager = LinearLayoutManager(this)

        habitRecyclerViewAdapter = HabitRecyclerViewAdapter(sequence)
        habitSequenceRecyclerView.adapter = habitRecyclerViewAdapter

//        attachItemTouchHelper()
    }

    private fun initializeIdentityRecyclerView() {
        identityRecyclerView.layoutManager = LinearLayoutManager(this)
        identityRecyclerViewAdapter = IdentityRecyclerViewAdapter(null, this)
        identityRecyclerView.adapter = identityRecyclerViewAdapter
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

    fun editIdentityDetails(identityView: IdentityViewModel.IdentityView) {
        drawerLayout.closeDrawer(drawerView)

        identityView.id?.let {
            startIdentityDetails(it)
        }
    }

    private fun startIdentityDetails(identityId: Long?) {
        val intent = Intent(this, IdentityDetailActivity::class.java)

        if (identityId != null) {
            intent.putExtra(EXTRA_IDENTITY_ID, identityId)
        }

        startActivity(intent)
    }

    companion object {
        const val EXTRA_IDENTITY_ID = "com.drspaceman.atomicio.EXTRA_IDENTITY_ID"
    }
}
