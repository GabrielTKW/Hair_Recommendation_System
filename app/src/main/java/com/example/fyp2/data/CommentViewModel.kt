package com.example.fyp2.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fyp2.Comment
import com.example.fyp2.Like
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale

class CommentViewModel: ViewModel()  {

    private val col = Firebase.firestore.collection("comments")
    private val comments = MutableLiveData<List<Comment>>()
    private var formatter = SimpleDateFormat("dd MMMM yyyy '-' hh:mm:ss a", Locale.getDefault())

    init{
        col.addSnapshotListener { snap, _ ->  comments.value = snap?.toObjects()}
    }


    fun getAll() = comments

    fun get(id:String): Comment?{
        return comments.value?.find{c->c.id==id}
    }

    fun getCommentsForHairstyle(hairstyleId: String): MutableLiveData<List<Comment>> {
        val filteredComments = MutableLiveData<List<Comment>>()

        col.addSnapshotListener { snap, _ ->
            // Update the filteredComments value with the filtered comments for the given hairstyleId
            filteredComments.value = snap?.toObjects(Comment::class.java)
                ?.filter { c -> c.hairstyleId == hairstyleId } ?: emptyList()

            Log.d("HairstyleId", hairstyleId)
            Log.d("FilteredCommentsSize", "Size: ${filteredComments.value?.size}")
        }

        return filteredComments
    }

    fun deleteComment(commentId: String) {
        col.document(commentId)
            .delete()
            .addOnSuccessListener {
                Log.d("CommentViewModel", "Comment deleted successfully: $commentId")
            }
            .addOnFailureListener { e ->
                Log.w("CommentViewModel", "Error deleting comment: $commentId", e)
            }
    }

    fun size(): Int {
        return comments.value?.size ?: 0
    }

    fun set(c : Comment){
        col.document().set(c)
    }

    fun validateComment(comment: Comment): String {
        val inappropriateWords = listOf("Fuck", "fuck", "pussy","stupid","piss","shit","cocksucker","motherfucker","tits")
        var errorMsg = ""

        errorMsg += if (comment.content.isEmpty()) "- Comment is required.\n"
        else if (comment.content.length < 3) "Comment is too short.\n"
        else if (inappropriateWords.any { comment.content.contains(it, ignoreCase = true) }) "Comment contains inappropriate words.\n"
        else ""

        return errorMsg
    }



}