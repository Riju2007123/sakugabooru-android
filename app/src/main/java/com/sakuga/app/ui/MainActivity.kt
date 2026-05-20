package com.sakuga.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.sakuga.app.R
import com.sakuga.app.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController

        b.bottomNav.setupWithNavController(navController)

        // hide bottom nav on detail/login screens
        navController.addOnDestinationChangedListener { _, dest, _ ->
            b.bottomNav.visibility = when (dest.id) {
                R.id.detailFragment, R.id.loginFragment ->
                    android.view.View.GONE
                else ->
                    android.view.View.VISIBLE
            }
        }
    }
}
