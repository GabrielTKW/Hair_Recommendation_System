package com.example.fyp2

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
import com.example.fyp2.data.errorDialog
import com.example.fyp2.databinding.FragmentRegisterAccountBinding

class RegisterAccount : Fragment() {
    private var _binding: FragmentRegisterAccountBinding? = null
    private val binding get() = _binding!!
    private val vm: UserViewModel by activityViewModels()
    private val nav by lazy {findNavController()}


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterAccountBinding.inflate(inflater, container, false)
        binding.btnRegister.setOnClickListener{submit()}
        binding.backtoLogin.setOnClickListener { nav.navigateUp() }

        return binding.root
    }

   private fun submit(){

        Log.d("inRegister",vm.size().toString())
       val u = User(
           id = "U${String.format("%03d", vm.size())}",
           name = binding.nameRegister.text.toString().trim(),
           email = binding.emailRegister.text.toString().trim().uppercase(),
           password = binding.passRegister.text.toString().trim(),
           freeze = false ,
           isAdmin = false,
           code = binding.securityCode.text.toString().trim()
       )
       Log.d("after user ",u.id)

       u.id = vm.replaceId("U${String.format("%03d", vm.size())}")
       val err = vm.validate(u)
       if(err.isNotEmpty()) {
           errorDialog(err)
           return
       }

       vm.set(u)
       reset()
       nav.navigateUp()
       Toast.makeText(requireContext(), "Successfully register account", Toast.LENGTH_SHORT).show()
   }

    private fun reset(){
        binding.emailRegister.text.clear()
        binding.passRegister.text.clear()
    }




}