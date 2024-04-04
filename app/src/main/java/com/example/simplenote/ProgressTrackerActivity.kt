package com.example.simplenote

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.simplenote.databinding.ActivityProgressTrackerBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class ProgressTrackerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressTrackerBinding
    private lateinit var db: DatabaseHelper
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgressTrackerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        db =  DatabaseHelper(this)
        drawerLayout = binding.drawerLayout
        pieChart = binding.pieChart

        val layoutParams = pieChart.layoutParams
        layoutParams.width = 700 // Set the desired width in pixels
        layoutParams.height = 700 // Set the desired height in pixels
        pieChart.layoutParams = layoutParams

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

        // Initialize PieChart
        setupPieChart()
        // Populate and update PieChart
        updatePieChart()

        binding.rateNewTaskButton.setOnClickListener {
            showDialogRateTaskPopup()
            updatePieChart()
        }

        binding.deleteAllProgressButton.setOnClickListener {
            db.deleteProgress()
            updatePieChart()
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

    private fun setupPieChart() {
        pieChart.apply {
            setUsePercentValues(false) // Show number not percentage
            description.isEnabled = false // Disable description text
            setDrawEntryLabels(false) // Disable labels on PieChart entries

            // Center hole and text in the hole
            isDrawHoleEnabled = true
            holeRadius = 40f
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)
            transparentCircleRadius = 45f
            setDrawCenterText(true)
            centerText = "Task Progress"
            setCenterTextSize(16f)
            setCenterTextColor(Color.BLACK)

            legend.isEnabled = false // Disable the legend
            animateY(1000) // Animate PieChart duration 1000 milliseconds
        }
    }

    private fun updatePieChart() {
        // Get data from the database and update PieChart accordingly
        val lateTasks = db.getLateTasks()
        val unfinishedTasks = db.getUnfinishedTasks()
        val onTimeTasks = db.getOnTimeTasks()

        val lateTasksEntry = PieEntry(lateTasks.toFloat(), "Late Tasks")
        val unfinishedTasksEntry = PieEntry(unfinishedTasks.toFloat(), "Unfinished Tasks")
        val onTimeTasksEntry = PieEntry(onTimeTasks.toFloat(), "On Time Tasks")

        val pieDataSet = PieDataSet(listOf(lateTasksEntry, unfinishedTasksEntry, onTimeTasksEntry), "Task Progress")
        // Set colors for each entry in the PieChart
        pieDataSet.colors = listOf(Color.RED, Color.YELLOW, Color.GREEN)

        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.invalidate()
    }

    private fun showDialogRateTaskPopup(){
        val dialogView = layoutInflater.inflate(R.layout.dialog_rate_task, null)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)
        val dialog = dialogBuilder.create()

        dialogView.findViewById<Button>(R.id.rateLateTask).setOnClickListener {
            val taskType1 = "lateTasks"
            db.rateTask(taskType1)
            dialog.dismiss()
            updatePieChart()
        }

        dialogView.findViewById<Button>(R.id.rateUnfinishedTask).setOnClickListener {
            val taskType2 = "unfinishedTasks"
            db.rateTask(taskType2)
            dialog.dismiss()
            updatePieChart()
        }

        dialogView.findViewById<Button>(R.id.rateOnTimeTask).setOnClickListener {
            val taskType3 = "onTimeTasks"
            db.rateTask(taskType3)
            dialog.dismiss()
            updatePieChart()
        }

        dialog.show()
    }

    private fun isCurrentActivity(activityClass: Class<*>): Boolean {
        return activityClass == this::class.java
    }
}