package com.example.simplenote

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplenote.databinding.ActivityChecklistBinding


class ChecklistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChecklistBinding
    private lateinit var db: DatabaseHelper
    private lateinit var checklistsAdapter: ChecklistsAdapter
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChecklistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // The + Button behaviour
        binding.toolbar.setOnMenuItemClickListener{menuItem ->
            when(menuItem.itemId){
                R.id.add_note -> {
                    showDialogCreateChecklistPopup()
                    true
                }
                else -> false
            }
        }

        db = DatabaseHelper(this)
        checklistsAdapter = ChecklistsAdapter(db.getAllChecklists(), this)

        binding.checklistRecycleView.layoutManager = LinearLayoutManager(this)
        binding.checklistRecycleView.adapter = checklistsAdapter


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
    // Inflate the + Button
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        checklistsAdapter.refreshData(db.getAllChecklists())
    }

    private fun showDialogCreateChecklistPopup(){
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_checklist, null)
        val editTextChecklist = dialogView.findViewById<EditText>(R.id.dialogAddChecklistTitle)

        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)
        val dialog = dialogBuilder.create()

        dialogView.findViewById<Button>(R.id.dialogButtonConfirmChecklistMenu).setOnClickListener {
            val title = editTextChecklist.text.toString()
            val checklist = ChecklistDC(0, title)
            db.insertChecklist(checklist)
            onResume()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.dialogButtonCancelChecklistMenu).setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun isCurrentActivity(activityClass: Class<*>): Boolean {
        return activityClass == this::class.java
    }
}