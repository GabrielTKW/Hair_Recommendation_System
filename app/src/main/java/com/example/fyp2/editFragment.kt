package com.example.fyp2

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.fyp2.data.UserViewModel
import com.example.fyp2.data.errorDialog
import com.example.fyp2.databinding.FragmentEditBinding
import com.example.fyp2.databinding.FragmentProfileBinding
import java.text.SimpleDateFormat
import java.util.Locale


class editFragment : Fragment() {
    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private val vm: UserViewModel by activityViewModels()
    private val nav by lazy {findNavController()}
    private var id: String = ""
    private var formatter = SimpleDateFormat("dd MMMM yyyy '-' hh:mm:ss a", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the bundle arguments here if needed
        val bundle: Bundle? = arguments
        if (bundle != null) {
            id = bundle.getString("userId", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditBinding.inflate(inflater, container, false)

        binding.btnTogglePassword2.setBackgroundResource(R.drawable.baseline_visibility_off_24)
        binding.edtNewPassProfile.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        binding.btnTogglePassword2.setOnCheckedChangeListener { _, isChecked ->
            // Toggle between eye open and eye closed icons
            val iconResourceId = if (isChecked) {
                R.drawable.baseline_visibility_24
            } else {
                R.drawable.baseline_visibility_off_24
            }
            binding.btnTogglePassword2.setBackgroundResource(iconResourceId)

            // Toggle between password and textVisiblePassword modes
            binding.edtNewPassProfile.inputType = if (isChecked) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            // Move the cursor to the end of the text to maintain cursor position during toggle
            binding.edtNewPassProfile.setSelection(binding.edtNewPassProfile.text.length)
        }

        //get userid from bundle
        var user = vm.get(id)

        if(user!=null) {
            binding.edtNameProfileUser.setText(user.name)
            binding.edtNewPassProfile.setText(user.password)
            binding.edtEmailUserProfile.setText(user.email)
        }


        //userID send to textfield

        //btn (onclick listener)
        binding.btnSave.setOnClickListener { submit() }



        return binding.root
    }

    private fun submit(){
        //get all binding
        var user = vm.get(id)
        val u : User

        if(user!=null) {
            u = User(
                id = user.id,
                name = binding.edtNameProfileUser.text.toString().trim(),
                email = binding.edtEmailUserProfile.text.toString().trim().uppercase(),
                password = binding.edtNewPassProfile.text.toString().trim(),
                freeze = user.freeze,
                isAdmin = user.isAdmin,
                code = user.code
            )

            val err = vm.validateEdit(u)
            if(err.isNotEmpty()) {
                errorDialog(err)
                return
            }

            //update the data
            vm.set(u)
            Toast.makeText(requireContext(), "Successfully updated info", Toast.LENGTH_SHORT).show()
        }






        //put it all into an object user

        //set (set the user with new details )
    }


}