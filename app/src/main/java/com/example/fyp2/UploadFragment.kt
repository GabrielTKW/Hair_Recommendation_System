package com.example.fyp2

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.fyp2.databinding.FragmentUploadBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.DateFormat
import java.util.Calendar
import androidx.navigation.fragment.findNavController
import com.example.fyp2.data.HairstylesViewModel
import com.example.fyp2.data.cropToBlob
import com.example.fyp2.data.errorDialog
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

class UploadFragment : Fragment() {
    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding!!
    private val vm: HairstylesViewModel by activityViewModels()
    private val nav by lazy {findNavController()}
   //private val vm : FriendViewModel by activityViewModels()
   private var id: String = ""

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK){
            binding.uploadImage.setImageURI(it.data?.data)
        }
    }

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
        // Inflate the binding object for this fragment
        _binding = FragmentUploadBinding.inflate(inflater, container, false)

        val spinner = binding.uploadGender
        val genders = arrayOf("Male", "Female")
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, genders)
        spinner.adapter = arrayAdapter

        val spinner2 = binding.uploadCat
        val category = arrayOf("Heart", "Oblong","Oval","Round","Square")
        val arrayAdapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, category)
        spinner2.adapter = arrayAdapter2


        reset()
        binding.uploadImage.setOnClickListener{select()}
        binding.btnReset.setOnClickListener{reset()}
        binding.saveButton.setOnClickListener{submit()}



        return binding.root
    }

    private fun select(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        launcher.launch(intent)
    }

    private fun reset(){
        binding.uploadHairstyleName.text.clear()
        binding.uploadHairDes.text.clear()
        binding.uploadGender.setSelection(0)
        binding.uploadCat.setSelection(0)
        binding.uploadImage.setImageDrawable(null)
    }

    private fun submit() {

        val h = Hairstyle(
            id = "H${String.format("%03d", vm.size())}",
               name = binding.uploadHairstyleName.text.toString().trim().uppercase(),
                desc = binding.uploadHairDes.text.toString().trim().uppercase(),
                gender = binding.uploadGender.selectedItem.toString().trim(),
                cat = binding.uploadCat.selectedItem.toString().trim(),
                photo = binding.uploadImage.cropToBlob(300,300),
                userId = id
            )


            //if the id is duplicated add a date time behind
            h.id = vm.replaceId("H${String.format("%03d", vm.size())}")
            val err = vm.validate(h)
            if(err != "") {
                errorDialog(err)
                return
            }

        vm.set(h)
        reset()
        nav.navigateUp()
        Toast.makeText(requireContext(), "Successfully added record", Toast.LENGTH_SHORT).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }









}
