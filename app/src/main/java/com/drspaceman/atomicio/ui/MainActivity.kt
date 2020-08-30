package com.drspaceman.atomicio.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.IdentityRecyclerViewAdapter
import com.drspaceman.atomicio.viewmodel.IdentityViewModel
import com.drspaceman.atomicio.viewmodel.IdentityViewModel.IdentityView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.drawer_view_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var identityRecyclerViewAdapter: IdentityRecyclerViewAdapter
    private val identityViewModel by viewModels<IdentityViewModel>()

    // @todo: KAE isn't working!! Why?
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var identityRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        identityRecyclerView = findViewById(R.id.identityRecyclerView)

        setupToolbar()
        setupBottomNavigationMenu()

//        fab.setOnClickListener { view ->
//
//        }

        initializeIdentityRecyclerView()


    }

    private fun setupToolbar() {
        setSupportActionBar(appToolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            appToolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        toggle.syncState()

        addIdentityButton.setOnClickListener {
            drawerLayout.closeDrawer(drawerView)
            showIdentityDetailsFragment(null)
        }
    }

    private fun setupBottomNavigationMenu() {
        val bottomNavigation = bottomNavigation ?: return

        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.agendaPage -> {
                    // Respond to navigation item 1 click
                    true
                }
                R.id.identityPage -> {
                    // Respond to navigation item 2 click
                    true
                }
                else -> false
            }
        }

        bottomNavigation.setOnNavigationItemReselectedListener { item ->
            when(item.itemId) {
                R.id.agendaPage -> {
                    // Respond to navigation item 1 reselection
                }
                R.id.identityPage -> {
                    // Respond to navigation item 2 reselection
                }
            }
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

    private fun initializeIdentityRecyclerView() {
        identityRecyclerView.layoutManager = LinearLayoutManager(this)
        identityRecyclerViewAdapter = IdentityRecyclerViewAdapter(null, this)
        identityRecyclerView.adapter = identityRecyclerViewAdapter

        identityViewModel.getIdentityViews()?.observe(
            this,
            Observer<List<IdentityView>> {
                it?.let {
                    identityRecyclerViewAdapter.setIdentityData(it)
                }
            }
        )
    }

    fun editIdentityDetails(identityId: Long?) {
        drawerLayout.closeDrawer(drawerView)
        showIdentityDetailsFragment(identityId)
    }


    private fun showIdentityDetailsFragment(identityId: Long?) {
        val fragmentManager = supportFragmentManager
        val identityDetailsFragment = IdentityDetailsFragment.newInstance(identityId)

        identityDetailsFragment.show(fragmentManager, IdentityDetailsFragment.TAG)
    }
}
