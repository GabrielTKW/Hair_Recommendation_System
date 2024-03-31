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
import com.example.fyp2.databinding.FragmentAdminEditUserBinding


class adminEditUser : Fragment() {

    private lateinit var binding: FragmentAdminEditUserBinding

    private val vm: UserViewModel by activityViewModels()
    private var id: String = ""
    private val nav by lazy { findNavController() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle: Bundle? = arguments
        if (bundle != null) {
            id = bundle.getString("userId", "")
            Log.d("fragment",id)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminEditUserBinding.inflate(inflater,container,false)

        var user = vm.get(id)
        Log.d("check user",user.toString())


        if(user!=null) {

            binding.editEmail.setText(user.email)
            binding.editPassword.setText(user.password)
            binding.editNameUser.setText(user.name)
            Log.d("admin user ","${user.id}")
            var mutableString = ""
            binding.userIDedit.setText(user.id)
            mutableString ="Security Code : "+ user.code
            binding.securityCodeUser.setText(mutableString)



            binding.dltButton.setOnClickListener { vm.delete(user.id) }
            binding.updateButton2.setOnClickListener { update() }
            binding.fButton.setOnClickListener { freeze() }
            binding.adminBtn.setOnClickListener { promoteToAdmin()  }
        }

        return binding.root
    }

    fun update(){
        var user = vm.get(id)

        if(user!=null) {
            user = User(
                id = user.id,
                name = binding.editNameUser.text.toString().trim(),
                email = binding.editEmail.text.toString().trim().uppercase(),
                password = binding.editPassword.text.toString().trim(),
                freeze = user.freeze,
                isAdmin = user.isAdmin,
                code = user.code
            )

            val err = vm.validateEdit(user)
            if (err.isNotEmpty()) {
                errorDialog(err)
                return
            }

            //update the data
            vm.set(user)
            Toast.makeText(requireContext(), "Successfully updated info", Toast.LENGTH_SHORT).show()
        }
    }

    fun freeze(){
        val isFrozen = vm.toggleFreezeStatus(id)

        if (isFrozen) {
            Toast.makeText(requireContext(), "Freeze ${id}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Unfreeze ${id}", Toast.LENGTH_SHORT).show()
        }

    }

    fun promoteToAdmin(){

        val isAdmin = vm.toggleAdminStatus(id)

        if (isAdmin) {
            Toast.makeText(requireContext(), "User with ID $id is now an admin.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "User with ID $id is no longer an admin.", Toast.LENGTH_SHORT).show()
        }

    }


}