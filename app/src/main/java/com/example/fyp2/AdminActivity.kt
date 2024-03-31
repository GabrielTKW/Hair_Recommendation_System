package com.example.fyp2

import UserAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fyp2.data.UserViewModel
import com.example.fyp2.databinding.ActivityAdminBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class AdminActivity : AppCompatActivity() {

    private var _binding: ActivityAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private val vm: UserViewModel by viewModels()


    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("in admin activity", "come in dy ")
        _binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment3) as NavHostFragment
        navController = navHostFragment.navController


        //1. Create bottom navigation view
        bottomNavigationView = findViewById(R.id.bottom_navigation2)

        //get Admin userId
        val userId = intent.getStringExtra("userId")

        adapter = UserAdapter { holder, User ->

            holder.deleteButton.setOnClickListener {
                vm.delete(User.id)
                Toast.makeText(this, " ${User.id} Deleted", Toast.LENGTH_SHORT).show()
            }

            holder.freezeButton.setOnClickListener {
                val isFrozen = vm.toggleFreezeStatus(User.id)

                if (isFrozen) {
                    Toast.makeText(this, "Freeze ${User.id}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Unfreeze ${User.id}", Toast.LENGTH_SHORT).show()
                }
            }

            holder.root.setOnClickListener {

                val bundle = bundleOf("userId" to User.id)
                Log.d("admin activity",User.id)

                navController.navigate(R.id.adminEditUser, bundle)
            }
        }

        binding.RecycleView3.adapter = adapter
        binding.RecycleView3.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        vm.getResult().observe(this) { list ->
            adapter.submitList(list)
        }

        binding.RecycleView3.apply {
            layoutManager = LinearLayoutManager(this@AdminActivity)
        }

        // Reference the SearchView
        val searchView: SearchView = binding.searchViewUser

        // Set up a listener for the SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle query submission if needed
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Call the search function in the ViewModel when the text changes
                Log.d("HairStyleFragment", "Search Query Changed: $newText")
                vm.search(newText.orEmpty())
                return true
            }
        })


        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Admin_user -> {
                    navController.popBackStack(R.id.blankFragment, false)
                    true
                }
                R.id.Admin_comment -> {
                    navController.navigate(R.id.adminComment)
                    true
                }
                R.id.Admin_hairstyle -> {
                    navController.navigate(R.id.adminProfileFragment,bundleOf("userId" to userId))
                    true
                }
                else -> false
            }
        }
    }


}

