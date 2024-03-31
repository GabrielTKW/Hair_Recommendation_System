package com.example.fyp2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fyp2.data.CommentAdapter
import com.example.fyp2.data.CommentViewModel
import com.example.fyp2.data.FavouriteViewModel
import com.example.fyp2.data.HairStyleAdapter
import com.example.fyp2.data.HairstylesViewModel
import com.example.fyp2.data.LikeViewModel
import com.example.fyp2.data.UserViewModel
import com.example.fyp2.data.errorDialog
import com.example.fyp2.data.toBitmap
import com.example.fyp2.databinding.FragmentHairStyleBinding
import com.example.fyp2.databinding.FragmentHomeBinding
import com.example.fyp2.databinding.FragmentSpecificHairBinding
import java.text.SimpleDateFormat
import java.util.Locale

class SpecificHairFragment : Fragment() {
    private lateinit var binding: FragmentSpecificHairBinding
    private val vm: HairstylesViewModel by activityViewModels()
    private lateinit var adapter: HairStyleAdapter
    private var hairstyleid: String = ""
    private var userId: String =""
    private var formatter = SimpleDateFormat("dd MMMM yyyy '-' hh:mm:ss a", Locale.getDefault())
    private val vmFavourite: FavouriteViewModel by activityViewModels()
    private val vmLike : LikeViewModel by activityViewModels()
    private val vmComment: CommentViewModel by activityViewModels()
    private val vmUser: UserViewModel by activityViewModels()
    private lateinit var commentAdapter: CommentAdapter
    private val nav by lazy { findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the bundle arguments here if needed
        val bundle: Bundle? = arguments
        if (bundle != null) {
            hairstyleid = bundle.getString("hairstyleid", "")
            userId = bundle.getString("userId","")

        }




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpecificHairBinding.inflate(inflater, container, false)

        //access the viewmodel to get the data
        var hairstyle = vm.get(hairstyleid)

        binding.imageViewSpecific.setImageBitmap(hairstyle?.photo?.toBitmap())
        binding.specificHairStyleName.text = hairstyle?.name
        binding.specficDesc.text = hairstyle?.desc
        binding.specificCat.text = hairstyle?.cat
        binding.specficGender.text = hairstyle?.gender

        val formattedTime = hairstyle?.date?.let { formatter.format(it) }
        binding.specificTime.text = formattedTime

        // Check if the current user is the owner of the post
        var isAdmin = vmUser.get(userId)?.isAdmin
        if(isAdmin!= null ) {
            if (userId == hairstyle?.userId ||  isAdmin) {
                binding.edtButton.visibility = View.VISIBLE

            } else {
                binding.edtButton.visibility = View.GONE
            }
        }

        //Check whether user got like and saved
        val isFavorited = vmFavourite.isHairstyleFavorited(userId, hairstyleid)
        if (isFavorited) {
            binding.btnSavedSpecific.setImageResource(R.drawable.baseline_saved_24)
        } else {
            binding.btnSavedSpecific.setImageResource(R.drawable.baseline_save_24)
        }

        val isLiked = vmLike.isHairstyleLiked(userId, hairstyleid)
        if (isLiked) {
            binding.btnLikeSpecific.setImageResource(R.drawable.baseline_thumb_uped_24)
        } else {
            binding.btnLikeSpecific.setImageResource(R.drawable.baseline_thumb_up_24)
        }

        //display the like count
        val likeCount = vmLike.checkedLikeCountHairstyle(hairstyleid)
        binding.likeCountSpecific.text = "$likeCount"


        binding.btnLikeSpecific.setOnClickListener{
            //this will return true or false , if true means like , if false means unlike
            var bool = likeHairstyle(hairstyleid)

            //get the holder like count so can update directly when the user click
            var likeCount = if (binding.likeCountSpecific.text.toString().isNotEmpty()) binding.likeCountSpecific.text.toString().toInt() else 0

            if(bool){
                //if true means i want to turn unlike -> liked
                binding.btnLikeSpecific.setImageResource(R.drawable.baseline_thumb_uped_24)
                Log.d("unlike -> liked ","$likeCount")
                likeCount++
                Log.d("like button listener ","$likeCount")
                binding.likeCountSpecific.text = "$likeCount"


            }else{
                //if false means removed favourite liked - > unlike
                binding.btnLikeSpecific.setImageResource(R.drawable.baseline_thumb_up_24)
                likeCount--
                binding.likeCountSpecific.text = "$likeCount"
            }
        }

        binding.btnSavedSpecific.setOnClickListener{
            var bool = saveHairstyle(hairstyleid)

            if(bool){
                //if true means i want to turn like -> liked
                binding.btnSavedSpecific.setImageResource(R.drawable.baseline_saved_24)
            }else{
                //if false means removed favourite saved - > save
                binding.btnSavedSpecific.setImageResource(R.drawable.baseline_save_24)
            }
        }

        binding.addCommentButton.setOnClickListener{ addComment() }
        binding.edtButton.setOnClickListener {
            val bundle1 = bundleOf("hairstyleId" to hairstyleid)
            val bundle2 = bundleOf("userId" to userId)

            val combinedBundle = Bundle().apply {
                putAll(bundle1)
                putAll(bundle2)
            }

            nav.navigate(R.id.editHairstyle, combinedBundle)
        }

        commentAdapter = CommentAdapter(vmUser,vmUser.get(userId)){holder, Comment->
            holder.delete.setOnClickListener { vmComment.deleteComment(holder.commentId.text.toString()) }
        }
        binding.commentRecycleView.adapter = commentAdapter


        binding.commentRecycleView.layoutManager = LinearLayoutManager(requireContext())

        vmComment.getCommentsForHairstyle(hairstyleid).observe(viewLifecycleOwner) { comments ->
            commentAdapter.submitList(comments)
        }


        return binding.root



    }

    private fun addComment(){
        val c = Comment(
            id="",
            userId=userId,
            content = binding.addCommenttext.text.toString().trim(),
            hairstyleId = hairstyleid
        )



        val err = vmComment.validateComment(c)
        if(err != "") {
            errorDialog(err)
            return
        }

        vmComment.set(c)
        Toast.makeText(requireContext(), "Added Comment", Toast.LENGTH_SHORT).show()
        binding.addCommenttext.text.clear()

    }

    private fun likeHairstyle(hairstyleId:String ): Boolean{
        return if (vmLike.isHairstyleLiked(userId , hairstyleId)) {
            //means already liked , then delete from liked
            vmLike.deleteByUserIdAndHairstyleId(userId, hairstyleId)
            Toast.makeText(requireContext(), "removed ${vm.get(hairstyleId)?.name} likes", Toast.LENGTH_SHORT).show()
            false
        }else{
            // means haven't favorite, then save as favorite
            val l = Like(
                userId = userId,
                hairstyleId = hairstyleId
            )

            vmLike.set(l)
            Toast.makeText(requireContext(), "added ${vm.get(hairstyleId)?.name} likes", Toast.LENGTH_SHORT).show()
            true
        }

    }

    private fun saveHairstyle(hairstyleId: String): Boolean {
        return if (vmFavourite.isHairstyleFavorited(userId, hairstyleId)) {
            // means already favorite, then delete from favorite
            vmFavourite.deleteByUserIdAndHairstyleId(userId, hairstyleId)
            Toast.makeText(requireContext(), "removed ${vm.get(hairstyleId)?.name} from favorite", Toast.LENGTH_SHORT).show()
            false
        } else {
            // means haven't favorite, then save as favorite
            val f = Favourite(
                userId = userId,
                hairstyleId = hairstyleId
            )

            vmFavourite.set(f)
            Toast.makeText(requireContext(), "added ${vm.get(hairstyleId)?.name} to favorite", Toast.LENGTH_SHORT).show()
            true
        }
    }


}