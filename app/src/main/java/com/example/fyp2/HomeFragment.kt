package com.example.fyp2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.fyp2.databinding.FragmentHomeBinding
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment() {
   private lateinit var binding:FragmentHomeBinding
   //private val nav by lazy { findNavController() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentHomeBinding.inflate(inflater,container,false)
        //binding.btnFriend.setOnClickListener{nav.navigate(androidx.appcompat.R.id.list_item)}
        binding.btnRead.setOnClickListener{read()}
        binding.btnSet.setOnClickListener{set()}
        binding.btnUpdate.setOnClickListener{update()}
        binding.btnDelete.setOnClickListener{delete()}


        return binding.root
    }

    private fun read(){
        Firebase.firestore
            .collection("friends")
            .get()
            .addOnSuccessListener { snap ->
                //list of friends (toObjects is using for QUERYSNAPSHOT IN COM.GOOGLE.FIREBASE.KTX
                val list = snap.toObjects<Friend>()
                var result =""
                list.forEach{f -> result += "${f.id} ${f.name} ${f.age}\n"}
                binding.txtResult.text = result
            }
    }

    private fun set(){
        //set can also not only add , can also update
        val f = Friend("A004","Diana",99)

        Firebase.firestore
            .collection("friends")
            .document(f.id) //if didnt put here it will automatically generated id from firebase
            .set(f)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Record inserted", Toast.LENGTH_SHORT).show()
            }
    }

    private fun update(){
    //only want to update one single field then use update
        Firebase.firestore
            .collection("friends")
            .document("A004")
            .update("name","Durian","age",8)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Record Updated", Toast.LENGTH_SHORT).show()
            }
    }

    private fun delete(){
        Firebase.firestore
            .collection("friends")
            .document("A004")
            .delete()
            .addOnSuccessListener {
                //On Success will only run when the whole function is executed
                Toast.makeText(requireContext(), "Record Deleted", Toast.LENGTH_SHORT).show()
            }
    }

}