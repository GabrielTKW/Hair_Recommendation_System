package com.example.fyp2

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.fyp2.data.UserViewModel
import com.example.fyp2.databinding.FragmentProfileBinding
import com.example.fyp2.databinding.FragmentRegisterAccountBinding
import com.example.fyp2.databinding.FragmentSpecificHairBinding
import java.text.SimpleDateFormat
import java.util.Locale


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val vm: UserViewModel by activityViewModels()
    private val nav by lazy {findNavController()}
    private var id: String = ""
    private var formatter = SimpleDateFormat("dd MMMM yyyy '-' hh:mm:ss a", Locale.getDefault())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle: Bundle? = arguments
        if (bundle != null) {
            id = bundle.getString("userId", "")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        var user = vm.get(id)
        if(user!=null) {
            binding.edtEmailRegProfile.text = "Email: ${user.email}"
            binding.edtNameProfile.text = "Name: ${user.name}"
            val formattedDate = formatter.format(user.registeredTime)
            binding.edtRegisteredTime.text = "Registered Time: $formattedDate"
        }



        binding.btnEdtProfile.setOnClickListener { nav.navigate(R.id.editFragment, bundleOf("userId" to id)) }
        binding.btnFavourite.setOnClickListener { nav.navigate(R.id.userFavouriteFragment,bundleOf("userId" to id)) }
        binding.btnMyPost.setOnClickListener {  nav.navigate(R.id.userHairstylePost, bundleOf("userId" to id))}
        binding.btnLogOut.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return binding.root
    }


}