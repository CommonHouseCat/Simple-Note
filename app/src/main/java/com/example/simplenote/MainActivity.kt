package com.example.simplenote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplenote.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: DatabaseHelper
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.add_note -> {
                    startActivity(Intent(this, AddNoteActivity::class.java))
                    true
                }
                else -> false
            }
        }

        db = DatabaseHelper(this)
        notesAdapter = NotesAdapter(db.getAllNotes(), this)

        binding.notesRecycleView.layoutManager = LinearLayoutManager(this)
        binding.notesRecycleView.adapter = notesAdapter
        drawerLayout = binding.drawerLayout

        val toggle = ActionBarDrawerToggle(this, drawerLayout, binding.toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.toolbar.setNavigationOnClickListener{
            if(drawerLayout.isDrawerOpen(binding.navigationView)){
                drawerLayout.closeDrawer(binding.navigationView)
            }else{
                drawerLayout.openDrawer(binding.navigationView)
            }
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId){
                R.id.nav_note -> {
                    if (!isCurrentActivity(MainActivity::class.java)) {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_checklist -> {
                    if (!isCurrentActivity(ChecklistActivity::class.java)) {
                        startActivity(Intent(this, ChecklistActivity::class.java))
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_reminder -> {
                    if (!isCurrentActivity(ReminderActivity::class.java)) {
                        startActivity(Intent(this, ReminderActivity::class.java))
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_progress_tracker -> {
                    if (!isCurrentActivity(ProgressTrackerActivity::class.java)) {
                        startActivity(Intent(this, ProgressTrackerActivity::class.java))
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed(){
                if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START)
                }else{
                    finish()
                }
            }
        })
    }

    // The + icon on the toolbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        notesAdapter.refreshData(db.getAllNotes())
    }

    // Get current activity
    private fun isCurrentActivity(activityClass: Class<*>): Boolean {
        return activityClass == this::class.java
    }
}