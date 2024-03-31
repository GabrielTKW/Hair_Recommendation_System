package com.example.fyp2

import android.R
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.fyp2.data.HairstylesViewModel
import com.example.fyp2.data.cropToBlob
import com.example.fyp2.data.errorDialog
import com.example.fyp2.data.toBitmap
import com.example.fyp2.databinding.FragmentEditBinding
import com.example.fyp2.databinding.FragmentEditHairstyleBinding
import com.example.fyp2.databinding.FragmentSpecificHairBinding
import com.example.fyp2.databinding.FragmentUploadBinding


class editHairstyle : Fragment() {
    private var _binding: FragmentEditHairstyleBinding? = null
    private val binding get() = _binding!!
    private val vm: HairstylesViewModel by activityViewModels()
    private val nav by lazy {findNavController()}
    private var hairstyleId: String = ""
    private var userId: String =""

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK){
            binding.editImageView.setImageURI(it.data?.data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle: Bundle? = arguments
        if (bundle != null) {
            hairstyleId = bundle.getString("hairstyleId", "")
            userId = bundle.getString("userId", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditHairstyleBinding.inflate(inflater, container, false)

        val spinner = binding.editGender
        val genders = arrayOf("Male", "Female")
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, genders)
        spinner.adapter = arrayAdapter

        val spinner2 = binding.editCategories
        val category = arrayOf("Heart", "Oblong","Oval","Round","Square")
        val arrayAdapter2 = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, category)
        spinner2.adapter = arrayAdapter2



        var h = vm.get(hairstyleId)

        //set all field into correct field
        binding.editName.setText(h?.name)
        binding.editDesc.setText(h?.desc)
        // Set the selected gender in the spinner
        val genderIndex = genders.indexOf(h?.gender)
        if (genderIndex != -1) {
            binding.editGender.setSelection(genderIndex)
        }

        // Set the selected category in the spinner
        val categoryIndex = category.indexOf(h?.cat)
        if (categoryIndex != -1) {
            binding.editCategories.setSelection(categoryIndex)
        }
        binding.editImageView.setImageBitmap(h?.photo?.toBitmap())

        binding.editImageView.setOnClickListener{ select() }
        binding.deleteButton.setOnClickListener{delete() }
        binding.updateButton.setOnClickListener { submit() }
        binding.backtoPrevious.setOnClickListener { nav.navigateUp() }


        return binding.root
    }


    private fun select(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        launcher.launch(intent)
    }

    private fun submit() {
        Log.d("inside update ",userId)
        val h = Hairstyle(
            id = hairstyleId,
            name = binding.editName.text.toString().trim().uppercase(),
            desc = binding.editDesc.text.toString().trim().uppercase(),
            gender = binding.editGender.selectedItem.toString().trim(),
            cat = binding.editCategories.selectedItem.toString().trim(),
            photo = binding.editImageView.cropToBlob(300,300),
            userId = userId
        )

        val err = vm.validate(h,false)
        if(err != "") {
            errorDialog(err)
            return
        }
        vm.update(h)
        nav.navigateUp()
        Toast.makeText(requireContext(), "Successfully updated record", Toast.LENGTH_SHORT).show()
    }

    private fun delete(){
        vm.delete(hairstyleId)
        nav.navigateUp()
        Toast.makeText(requireContext(), "Successfully deleted hairstyle", Toast.LENGTH_SHORT).show()
    }

}