package com.example.fyp2.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fyp2.Favourite
import com.example.fyp2.Like
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase

class LikeViewModel: ViewModel() {

    private val col = Firebase.firestore.collection("likes")
    private val likes = MutableLiveData<List<Like>>()

    init{
        col.addSnapshotListener { snap, _ ->  likes.value = snap?.toObjects()}
    }

    fun get(id:String): Like?{
        return likes.value?.find{l->l.id==id}
    }

    fun size(): Int {
        return likes.value?.size ?: 0
    }

    fun set(f : Like){
        col.document().set(f)
    }

    fun delete(id: String){
        col.document(id).delete()
    }

    fun deleteByUserIdAndHairstyleId(userId: String, hairstyleId: String) {
        val like = likes.value?.find { f -> f.userId == userId && f.hairstyleId == hairstyleId }
        like?.let {
            col.document(it.id).delete()
        }
    }

    //for user profile to check whether the hairstyle have been favourite
    fun isHairstyleLiked(userId: String, hairstyleId: String): Boolean {
        return likes.value?.any { l -> l.userId == userId && l.hairstyleId == hairstyleId } ?: false
    }

    fun checkedLikeCountHairstyle(hairstyleId: String): Int {
        return likes.value?.count { l -> l.hairstyleId == hairstyleId } ?: 0
    }

}