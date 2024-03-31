package com.example.fyp2

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private val OUR_REQUEST_CODE = 123
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //1. Create bottom navigation view
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        //for navigation
        val nav = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = nav.navController

        // Retrieve user ID from Login using intent
        val userId = intent.getStringExtra("userId")

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottom_hairstyle -> {
                    navController.navigate(R.id.HairStyleFragment,bundleOf("userId" to userId))
                    //replaceFragment(HairStyleFragment())
                    true
                }
                R.id.bottom_detect -> {
                    navController.navigate(R.id.detectHairstyleFragment,bundleOf("userId" to userId))
                    //replaceFragment(DetectHairstyleFragment())
                    true
                }
                R.id.bottom_userProfile -> {
                    navController.navigate(R.id.profileFragment, bundleOf("userId" to userId))
                    //replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }


        navController.navigate(R.id.HairStyleFragment,bundleOf("userId" to userId))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if the request code matches OUR_REQUEST_CODE
        if (requestCode == OUR_REQUEST_CODE && resultCode == RESULT_OK) {
            // Forward the result to the fragment

            // Find the NavHostFragment
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

            // Find the fragment by its ID within the NavHostFragment
            val fragment = navHostFragment.childFragmentManager.fragments[0]

            // Forward the result to the fragment
            fragment.onActivityResult(OUR_REQUEST_CODE, resultCode, data)
        }
    }


    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame_container,fragment ).commit()
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}