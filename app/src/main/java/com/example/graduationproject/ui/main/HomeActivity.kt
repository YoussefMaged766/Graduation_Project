package com.example.graduationproject.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.graduationproject.R
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var binding: ActivityHomeBinding
    private lateinit var actionBarToggle: ActionBarDrawerToggle
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



    override fun onBackPressed() {
        if (this. binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this. binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}