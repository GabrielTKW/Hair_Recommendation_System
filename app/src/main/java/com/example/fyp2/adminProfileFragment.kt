package com.example.fyp2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.fyp2.data.UserViewModel
import com.example.fyp2.databinding.FragmentAdminProfileBinding
import com.example.fyp2.databinding.FragmentSpecificHairBinding


class adminProfileFragment : Fragment() {
    private lateinit var binding: FragmentAdminProfileBinding
    private val nav by lazy { findNavController() }
    private val vmUser: UserViewModel by activityViewModels()
    private var userId: String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the bundle arguments here if needed
        val bundle: Bundle? = arguments
        if (bundle != null) {
            userId = bundle.getString("userId","")

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminProfileBinding.inflate(inflater, container, false)

        binding.logOutbuttonAdmin.setOnClickListener {
            // Use requireContext() to get the context associated with the fragment
            val loginIntent = Intent(requireContext(), LoginActivity::class.java)
            loginIntent.putExtra("userId", userId)
            startActivity(loginIntent)

            requireActivity().finish()
        }

        binding.goToUserPage.setOnClickListener {
            //Go to user page
            val mainIntent = Intent(requireContext(), MainActivity::class.java)
            mainIntent.putExtra("userId", userId)
            startActivity(mainIntent)

            requireActivity().finish()
        }


        return binding.root
    }


}