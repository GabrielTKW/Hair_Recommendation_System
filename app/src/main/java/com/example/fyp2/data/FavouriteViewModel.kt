package com.example.fyp2.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fyp2.Favourite
import com.example.fyp2.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase

class FavouriteViewModel: ViewModel() {

    private val col = Firebase.firestore.collection("favourites")
    private val fav = MutableLiveData<List<Favourite>>()

    init{
        col.addSnapshotListener { snap, _ ->  fav.value = snap?.toObjects()}
    }

    fun get(id:String): Favourite?{
        return fav.value?.find{f->f.id==id}
    }



    fun size(): Int {
        return fav.value?.size ?: 0
    }

    fun set(f : Favourite){
        col.document().set(f)
    }

    fun delete(id: String){
        col.document(id).delete()
    }


    fun deleteByUserIdAndHairstyleId(userId: String, hairstyleId: String) {
        val favoriteToDelete = fav.value?.find { f -> f.userId == userId && f.hairstyleId == hairstyleId }
        favoriteToDelete?.let {
            col.document(it.id).delete()
        }
    }


    //for user profile to get all favourite by userID
    fun getAllByUserId(userId: String): List<Favourite> {
        val filteredFav = fav.value?.filter { f ->
            Log.d("Print the userID", f.userId)
            f.userId == userId
        } ?: emptyList()

        Log.d("the Filter favourite size ", filteredFav.size.toString())
        Log.d("insideGetAllByUserId", this.fav.toString())

        return filteredFav
    }




    //for user profile to check whether the hairstyle have been favourite
    fun isHairstyleFavorited(userId: String, hairstyleId: String): Boolean {
        return fav.value?.any { f -> f.userId == userId && f.hairstyleId == hairstyleId } ?: false
    }



}