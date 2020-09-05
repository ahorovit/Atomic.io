package com.drspaceman.atomicio.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.drspaceman.atomicio.R
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    // @todo: KAE isn't working!! Why?
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)

        setupToolbar()
        setupBottomNavigationMenu()
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
    }

    private fun setupBottomNavigationMenu() {
        val bottomNavigation = bottomNavigation ?: return

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.agendaPage -> {
                    openFragment(AgendaFragment.newInstance())
                    true
                }
                R.id.identityPage -> {
                    openFragment(IdentityPageFragment.newInstance())
                    true
                }
//                R.id.agendaPage -> {
//                }
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

    private fun openFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainFragmentContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
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
}
