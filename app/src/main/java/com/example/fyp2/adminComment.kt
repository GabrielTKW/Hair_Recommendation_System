package com.example.fyp2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fyp2.data.CommentAdapter
import com.example.fyp2.data.CommentViewModel
import com.example.fyp2.data.UserViewModel
import com.example.fyp2.databinding.FragmentAdminCommentBinding
import com.example.fyp2.databinding.FragmentSpecificHairBinding


class adminComment : Fragment() {
    private val vmComment: CommentViewModel by activityViewModels()
    private lateinit var commentAdapter: CommentAdapter
    private val nav by lazy { findNavController() }
    private lateinit var binding: FragmentAdminCommentBinding
    private val vmUser: UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminCommentBinding.inflate(inflater, container, false)

        commentAdapter = CommentAdapter(vmUser,User(isAdmin = true)){holder, Comment->
            holder.delete.setOnClickListener { vmComment.deleteComment(holder.commentId.text.toString()) }
        }

        binding.recycleCommentAdmin.adapter = commentAdapter





        binding.recycleCommentAdmin.layoutManager = LinearLayoutManager(requireContext())

        vmComment.getAll().observe(viewLifecycleOwner) { comments ->
            commentAdapter.submitList(comments)
        }



        return binding.root
    }






}