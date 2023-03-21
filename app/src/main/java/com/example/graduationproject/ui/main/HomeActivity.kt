package com.example.graduationproject.ui.main

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
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
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setUpBotNav()
        setUpDrawer()
        actionDrawer()


    }

    private fun setUpDrawer() {
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.searchFragment, R.id.profileFragment, R.id.wishlistFragment,
            ), binding.drawerLayout
        )
        setSupportActionBar(binding.appBarHome.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setUpBotNav() {
        navController = findNavController(R.id.nav_host_fragment_content_home)
        findNavController(R.id.nav_host_fragment_content_home).addOnDestinationChangedListener { controller, destination, arguments ->
            title = destination.label
            when (destination.id) {
                R.id.searchResultFragment -> binding.navViewBot.visibility = View.GONE
                R.id.bookFragment-> binding.navViewBot.visibility = View.GONE
                else -> binding.navViewBot.visibility = View.VISIBLE
            }

        }
        binding.navViewBot.setupWithNavController(navController)
    }

    private fun actionDrawer() {
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_logout -> {
                    Constants.customToast(applicationContext, this, "logout")
                    lifecycleScope.launch {
                        clearDataStore()
                        clearDataStoreID()
                    }
                    startActivity(Intent(this, MainActivity::class.java))

                    true
                }
                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private suspend fun clearDataStore() {
        dataStore = applicationContext.dataStore
        val dataStoreKey: Preferences.Key<Int> = intPreferencesKey("userToken")
        dataStore.edit {
            it.remove(dataStoreKey)
        }
    }

    private suspend fun clearDataStoreID() {
        dataStore = applicationContext.dataStore
        val dataStoreKey: Preferences.Key<String> = stringPreferencesKey("userId")
        dataStore.edit {
            it.remove(dataStoreKey)
        }
    }


    override fun onBackPressed() {
//        if (this.binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            this.binding.drawerLayout.closeDrawer(GravityCompat.START)
//        } else {
//            super.onBackPressed()
//        }
        showExitDialog()
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

}