package com.example.fyp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.fyp2.data.HairstylesViewModel
import com.example.fyp2.data.UserViewModel
import com.example.fyp2.data.errorDialog
import com.example.fyp2.databinding.ActivityLoginBinding
import com.example.fyp2.data.errorDialog
import com.example.fyp2.databinding.FragmentRegisterAccountBinding

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val vm: UserViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nav = supportFragmentManager.findFragmentById(R.id.nav_host_fragment2) as NavHostFragment
        navController = nav.navController



        binding.btnLogin.setOnClickListener { login() }
        binding.btnGoToReg.setOnClickListener {
            Log.d("Inside the login activity",vm.getSizeLiveData().toString())
            navController.navigate(R.id.registerAccount)

        }
        binding.forgetPassword.setOnClickListener{
            navController.navigate(R.id.forgetPassFragment)
        }


    }

    private fun login(){
        val u = User(
            id = "",
            name = "",
            email = binding.loginEmail.text.toString().trim().uppercase(),
            password = binding.loginPass.text.toString().trim(),
            freeze = false ,
            isAdmin = false,
        )

        Log.d("Before error ","Before going into login verification ")
        val err = vm.login(u.email,u.password)
        if(err.isNotEmpty()) {
            Toast.makeText(this, "Email or Password Incorrect", Toast.LENGTH_SHORT).show()

            return
        }


        // successfully login

        val user = vm.getByEmail(u.email)


        if(user!=null) {
            if (user.isAdmin) {
                Toast.makeText(this, "Successfully Login", Toast.LENGTH_SHORT).show()
                Log.d("inside the admin","${user.isAdmin}")
                val adminIntent = Intent(this, AdminActivity::class.java)
                adminIntent.putExtra("userId", vm.getByEmail(u.email)?.id)
                startActivity(adminIntent)
                finish()
            } else {
                if(!user.freeze) {
                    Toast.makeText(this, "Successfully Login", Toast.LENGTH_SHORT).show()
                    Log.d("inside the admin", "${user.isAdmin}")
                    val mainIntent = Intent(this, MainActivity::class.java)
                    mainIntent.putExtra("userId", vm.getByEmail(u.email)?.id)
                    startActivity(mainIntent)
                    finish()
                }else{
                    Toast.makeText(this, "Account Freeze , Contact admin for more", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

}
