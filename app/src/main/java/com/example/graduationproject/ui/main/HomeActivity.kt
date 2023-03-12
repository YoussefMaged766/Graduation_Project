package com.example.graduationproject.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.graduationproject.R
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var binding: ActivityHomeBinding
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var dataStore: DataStore<Preferences>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setUpBotNav()
        setUpDrawer()
        actionDrawer()

    }

    private fun setUpDrawer() {

        setSupportActionBar(binding.appBarHome.toolbar)
        actionBarToggle = ActionBarDrawerToggle(this, binding.drawerLayout, 0, 0)
        binding.drawerLayout.addDrawerListener(actionBarToggle)

        // Display the hamburger icon to launch the drawer
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        actionBarToggle.syncState()
    }

    private fun setUpBotNav(){
        navController = findNavController(R.id.nav_host_fragment_content_home)
        findNavController(R.id.nav_host_fragment_content_home).addOnDestinationChangedListener{controller, destination, arguments -> title = destination.label }
        binding.navViewBot.setupWithNavController(navController)
    }

    fun actionDrawer(){
        binding.navView.setNavigationItemSelectedListener { item->
            when(item.itemId){
                R.id.nav_logout-> {
                    Constants.customToast(applicationContext,this,"logout")
                    lifecycleScope.launch {
                        clearDataStore()
                    }
                    startActivity(Intent(this,MainActivity::class.java))

                    true
                }
                else-> false
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        binding.drawerLayout.openDrawer(binding.navView)
        return true
    }
    private suspend fun clearDataStore(){
        dataStore = applicationContext.dataStore
        val dataStoreKey: Preferences.Key<Int> = intPreferencesKey("userToken")
        dataStore.edit {
            it.remove(dataStoreKey)
        }
    }



    override fun onBackPressed() {
        if (this. binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this. binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}