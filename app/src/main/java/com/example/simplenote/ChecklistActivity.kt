package com.example.simplenote

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplenote.databinding.ActivityChecklistBinding


class ChecklistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChecklistBinding
    private lateinit var db: NoteDBHelper
    private lateinit var checklistMenuAdapter: ChecklistMenuAdapter
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChecklistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.toolbar.setOnMenuItemClickListener{menuItem ->
            when(menuItem.itemId){
                R.id.add_note -> {
                    showCreateChecklistPopup()
                    true
                }
                else -> false
            }
        }

        db = NoteDBHelper(this)
        checklistMenuAdapter = ChecklistMenuAdapter(db.getAllChecklists(), this)

        binding.checklistRecycleView.layoutManager = LinearLayoutManager(this)
        binding.checklistRecycleView.adapter = checklistMenuAdapter


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
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_checklist -> {
                    startActivity(Intent(this, ChecklistActivity::class.java))
                    true
                }
                R.id.nav_reminder -> {
                    startActivity(Intent(this, ReminderActivity::class.java))
                    true
                }
                R.id.nav_progress_tracker -> {
                    startActivity(Intent(this, ProgressTrackerActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingActivity::class.java))
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
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })


    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        checklistMenuAdapter.refreshData(db.getAllChecklists())
    }

    private fun showCreateChecklistPopup(){
        val fragmentManger = supportFragmentManager
        val fragmentTransaction = fragmentManger.beginTransaction()
        val fragment = CreateChecklistPopup()
        fragmentTransaction.add(fragment, "CreateChecklistPopup")
        fragmentTransaction.commit()
    }

}