package com.example.fyp2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fyp2.data.FavouriteViewModel
import com.example.fyp2.data.HairStyleAdapter
import com.example.fyp2.data.HairstylesViewModel
import com.example.fyp2.data.LikeViewModel
import com.example.fyp2.databinding.FragmentHairStyleBinding
import com.example.fyp2.databinding.FragmentUserFavouriteBinding
import com.example.fyp2.databinding.FragmentUserHairstylePostBinding

class userHairstylePost : Fragment() {

    private lateinit var binding: FragmentUserHairstylePostBinding
    private val nav by lazy { findNavController() }

    private val vm: HairstylesViewModel by activityViewModels()
    private val vmFavourite: FavouriteViewModel by activityViewModels()
    private val vmLike : LikeViewModel by activityViewModels()
    private lateinit var adapter: HairStyleAdapter

    private var id: String = ""

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
        binding = FragmentUserHairstylePostBinding.inflate(inflater, container, false)

        adapter = HairStyleAdapter(id , vmFavourite,vmLike) {holder , Hairstyle ->
            //Item click
            holder.root.setOnClickListener{
                val bundle1 = bundleOf("hairstyleid" to Hairstyle.id)
                val bundle2 = bundleOf("userId" to id)

                val combinedBundle = Bundle().apply {
                    putAll(bundle1)
                    putAll(bundle2)
                }

                nav.navigate(R.id.specificHairFragment, combinedBundle)
            }

            holder.likeButton.setOnClickListener{
                //this will return true or false , if true means like , if false means unlike
                var bool = likeHairstyle(Hairstyle.id)

                //get the holder like count so can update directly when the user click
                var likeCount = if (holder.likeCount.text.toString().isNotEmpty()) holder.likeCount.text.toString().toInt() else 0

                if(bool){
                    //if true means i want to turn unlike -> liked
                    holder.likeButton.setImageResource(R.drawable.baseline_thumb_uped_24)
                    Log.d("unlike -> liked ","$likeCount")
                    likeCount++
                    Log.d("like button listener ","$likeCount")
                    holder.likeCount.text = "$likeCount"


                }else{
                    //if false means removed favourite liked - > unlike
                    holder.likeButton.setImageResource(R.drawable.baseline_thumb_up_24)
                    Log.d("unlike -> liked ","$likeCount")
                    likeCount--
                    Log.d("like button listener ","$likeCount")
                    holder.likeCount.text = "$likeCount"
                }
            }


            //save to favourite
            holder.btnSave.setOnClickListener{
                var bool = saveHairstyle(Hairstyle.id)

                if(bool){
                    //if true means i want to turn like -> liked
                    holder.btnSave.setImageResource(R.drawable.baseline_saved_24)
                }else{
                    //if false means removed favourite saved - > save
                    holder.btnSave.setImageResource(R.drawable.baseline_save_24)
                }
            }

        }

        vm.getUserCreatedHairstyle(id).observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.userHairstyleTextView.text = "${list.size} hairstyle(s)"
        }

        binding.recyclerView3.apply {
            layoutManager = LinearLayoutManager(requireContext())
            // Set the adapter to the RecyclerView
            adapter = this@userHairstylePost.adapter
        }


        return binding.root
    }


    private fun likeHairstyle(hairstyleId:String ): Boolean{
        return if (vmLike.isHairstyleLiked(id , hairstyleId)) {
            //means already liked , then delete from liked
            vmLike.deleteByUserIdAndHairstyleId(id, hairstyleId)
            Toast.makeText(requireContext(), "removed ${vm.get(hairstyleId)?.name} likes", Toast.LENGTH_SHORT).show()
            false
        }else{
            // means haven't favorite, then save as favorite
            val l = Like(
                userId = id,
                hairstyleId = hairstyleId
            )

            vmLike.set(l)
            Toast.makeText(requireContext(), "added ${vm.get(hairstyleId)?.name} likes", Toast.LENGTH_SHORT).show()
            true
        }

    }

    private fun saveHairstyle(hairstyleId: String): Boolean {
        return if (vmFavourite.isHairstyleFavorited(id, hairstyleId)) {
            // means already favorite, then delete from favorite
            vmFavourite.deleteByUserIdAndHairstyleId(id, hairstyleId)
            Toast.makeText(requireContext(), "removed ${vm.get(hairstyleId)?.name} from favorite", Toast.LENGTH_SHORT).show()
            false
        } else {
            // means haven't favorite, then save as favorite
            val f = Favourite(
                userId = id,
                hairstyleId = hairstyleId
            )

            vmFavourite.set(f)
            Toast.makeText(requireContext(), "added ${vm.get(hairstyleId)?.name} to favorite", Toast.LENGTH_SHORT).show()
            true
        }
    }

}