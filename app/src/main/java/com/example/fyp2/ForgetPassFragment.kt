package com.example.fyp2

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import android.widget.ToggleButton
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.fyp2.data.UserViewModel
import com.example.fyp2.data.errorDialog
import com.example.fyp2.databinding.FragmentForgetPassBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class ForgetPassFragment : Fragment() {
    private var _binding: FragmentForgetPassBinding? = null
    private val binding get() = _binding!!
    private val vm: UserViewModel by activityViewModels()
    private val nav by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgetPassBinding.inflate(inflater, container, false)
        binding.btnTogglePassword.setBackgroundResource(R.drawable.baseline_visibility_off_24)
        binding.edtNewPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        binding.btnTogglePassword.setOnCheckedChangeListener { _, isChecked ->
            // Toggle between eye open and eye closed icons
            val iconResourceId = if (isChecked) {
                R.drawable.baseline_visibility_24
            } else {
                R.drawable.baseline_visibility_off_24
            }
            binding.btnTogglePassword.setBackgroundResource(iconResourceId)

            // Toggle between password and textVisiblePassword modes
            binding.edtNewPass.inputType = if (isChecked) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            // Move the cursor to the end of the text to maintain cursor position during toggle
            binding.edtNewPass.setSelection(binding.edtNewPass.text.length)
        }

        binding.btnChangePass.setOnClickListener { changePassword() }
        binding.backToLogin2.setOnClickListener { nav.navigateUp() }

        return binding.root
    }

    private fun changePassword() {
        val u = User(
            id = "",
            name = "",
            email = binding.edtEmail.text.toString().trim().uppercase(),
            password = binding.edtNewPass.text.toString().trim(),
            freeze = false,
            isAdmin = false,
            code = binding.edtSecurityCode.text.toString().trim()
        )

        val err = vm.forgetPassword(u.email, u.code, u.password)
        if (err.isNotEmpty()) {
            errorDialog(err)
            return
        }

        nav.navigateUp()
        Toast.makeText(requireContext(), "Successfully change password", Toast.LENGTH_SHORT).show()
    }
}
