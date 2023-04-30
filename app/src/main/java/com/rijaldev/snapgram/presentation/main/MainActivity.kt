package com.rijaldev.snapgram.presentation.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.rijaldev.snapgram.R
import com.rijaldev.snapgram.databinding.ActivityMainBinding
import com.rijaldev.snapgram.presentation.addstory.AddStoryActivity
import com.rijaldev.snapgram.presentation.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpDrawer()
        setUpView()
    }

    private fun setUpDrawer() {
        with(binding) {
            setSupportActionBar(appBarMain.toolbar)

            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.main_nav_host) as NavHostFragment
            navController = navHostFragment.navController

            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.homeFragment, R.id.mapsFragment
                ),
                drawerMain
            )

            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)

            navFooterMain.btnLogout.setOnClickListener {
                viewModel.signOut()
                moveToAuth()
            }
        }
    }

    private fun setUpView() {
        with(binding) {
            appBarMain.btnAddStory.setOnClickListener {
                val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
                launcherAddStory.launch(intent)
            }
            navView.setNavigationItemSelectedListener {
                if (it.itemId == R.id.btnChangeLanguage) {
                    val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                    startActivity(intent)
                    return@setNavigationItemSelectedListener false
                }
                NavigationUI.onNavDestinationSelected(it, navController)
                drawerMain.closeDrawer(GravityCompat.START)
                true
            }
        }
    }

    private val launcherAddStory = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val homeFragmentId = navController.graph.startDestinationId
            val currentIsHome = navController.currentDestination?.id == homeFragmentId
            when {
                !currentIsHome -> navController.navigateUp()
            }
            viewModel.refreshStories()
        }
    }

    private fun moveToAuth() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp() =
        navController.navigateUp(appBarConfiguration) or super.onSupportNavigateUp()
}