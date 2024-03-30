package com.example.simplenote

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.simplenote.databinding.ActivitySettingBinding
import java.util.Locale

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        drawerLayout = binding.drawerLayout
        spinner = binding.languageSetting

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

        // Populate the Spinner with language options
        val languages = arrayOf("English", "Vietnamese") // Add other languages as needed
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Listen for item selection events on the Spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLanguage = adapterView?.getItemAtPosition(position).toString()
                val currentLanguage = Locale.getDefault().language
                if (selectedLanguage == "Vietnamese" && currentLanguage != "vi") {
                    setLocale("vi") // Change to Vietnamese and restart to apply
                    recreate()
                } else if (selectedLanguage == "English" && currentLanguage != "en"){
                    setLocale("en") // Change to English and restart to apply
                    recreate()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                // Set English as the default language
                val currentLanguage = Locale.getDefault().language
                if (currentLanguage != "en") {
                    setLocale("en")
                    recreate()
                }
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


    private fun setLocale(languageCode: String) {
        val currentLanguage = Locale.getDefault().language
        if (currentLanguage != languageCode) {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val resources = resources
            val configuration = resources.configuration
            configuration.setLocale(locale)
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
    }


}