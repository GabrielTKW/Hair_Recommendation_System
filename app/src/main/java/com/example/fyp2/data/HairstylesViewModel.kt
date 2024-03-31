package com.example.fyp2.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fyp2.Favourite
import com.example.fyp2.Hairstyle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.Date

class HairstylesViewModel: ViewModel() {
    private val col = Firebase.firestore.collection("hairstyles")
    private val hairstyles = MutableLiveData<List<Hairstyle>>()

    private var searchQuery = "" // for searching purpose
    private val result = MutableLiveData<List<Hairstyle>>()


    init{
        col.addSnapshotListener { snap, _ ->
            hairstyles.value = snap?.toObjects()
            updateResult()
        }

    }

    fun search(query: String) {
        searchQuery = query
        updateResult()
    }

    fun getHairstylesByGenderAndCategory(gender: String, category: String): MutableLiveData<List<Hairstyle>> {
        val filteredHairstyles = MutableLiveData<List<Hairstyle>>()

        // Add a snapshot listener to update the LiveData when the hairstyles data changes
        col.addSnapshotListener { snap, _ ->
            val list = snap?.toObjects(Hairstyle::class.java)
            val filteredList = list?.filter { h ->
                h.gender.equals(gender, ignoreCase = true) && h.cat.equals(category, ignoreCase = true)
            } ?: emptyList()

            filteredHairstyles.value = filteredList
        }

        return filteredHairstyles
    }


    private fun updateResult() {
        var list = hairstyles.value ?: emptyList()

        // Search by name and category
        list = list.filter { hairstyle ->
            hairstyle.name.contains(searchQuery, true) ||
                    hairstyle.cat.contains(searchQuery, true) ||
                    hairstyle.gender.contains(searchQuery,true)
        }
        Log.d("HairstylesViewModel", "Search Query: $searchQuery, Result Size: ${list.size}")
        result.value = list
    }

    fun get(id:String): Hairstyle?{
        return hairstyles.value?.find{h->h.id==id}
    }

    fun getResult() = result

    fun getAll() = hairstyles

    fun getAllFavourite(userFavorites: List<Favourite>): MutableLiveData<List<Hairstyle>> {
        // Extract unique hairstyle IDs from the provided userFavorites
        val hairstyleIds = userFavorites.map { f -> f.hairstyleId }.distinct()

        // Filter the hairstyles based on the extracted IDs
        val filteredHairstyles = hairstyles.value?.filter { h -> hairstyleIds.contains(h.id) } ?: emptyList()

        // Create a new instance of MutableLiveData and set its value
        val result = MutableLiveData<List<Hairstyle>>()
        result.value = filteredHairstyles

        return result
    }


    fun update(hairstyle: Hairstyle) {
        if (idExistsUsingList(hairstyle.id)) {
            col.document(hairstyle.id).set(hairstyle)
        } else {
            Log.e("HairstylesViewModel", "Attempted to update non-existent hairstyle with ID: ${hairstyle.id}")
        }
    }

    fun getUserCreatedHairstyle(userId: String): MutableLiveData<List<Hairstyle>> {
        val userHairstyles = MutableLiveData<List<Hairstyle>>()

        // Add a snapshot listener to update the LiveData when the hairstyles data changes
        col.addSnapshotListener { snap, _ ->
            val filteredHairstyles = snap?.toObjects(Hairstyle::class.java)
                ?.filter { h -> h.userId == userId } ?: emptyList()

            userHairstyles.value = filteredHairstyles
        }

        return userHairstyles
    }


    fun delete(id: String){
        col.document(id).delete()
    }


    fun deleteAll(){
        //col.get().addOnSuccessListener { snap-> snap.documents.forEach{ doc -> delete(doc.id)} }
        hairstyles.value?.forEach{h -> delete(h.id) }
    }

    // New function to get the size of the hairstyle list
    fun size(): Int {
        return hairstyles.value?.size ?: 0
    }

    fun set(h : Hairstyle){
        col.document(h.id).set(h)
    }

    private suspend fun idExists(id:String):Boolean{
        return col
            .document(id)
            .get()
            .await()
            .exists()
    }

    //Check the list and find whether the id exists
    private fun idExistsUsingList(id:String):Boolean{
        return hairstyles.value?.any { h -> h.id == id } ?:false
    }

    //Check the list and find whether the id exists
    private fun nameExistsUsingList(name:String):Boolean{
        return hairstyles.value?.any { h -> h.name == name } ?:false
    }

    //find whether the name is exist (This can find whether the email is same in user)
    private suspend fun nameExists(name:String):Boolean{
        return col.whereEqualTo("name",name).get().await().size() > 0
    }

    fun replaceId(id: String ): String{
        if(idExistsUsingList(id)){
            return id + Date().toString()
        }

        return id
    }

    fun validate(h:Hairstyle, insert:Boolean = true): String{
       // val regexId = Regex("""^[0-9A-Z]{4}$""".trimMargin())
        var e = ""

        if(insert){
            if (idExistsUsingList(h.id)) "Id is duplicated\n"
            else ""
        }

        e+= if (h.name =="") "- name is required.\n"
        else if (h.name.length < 3) "name must be more than 3 characters. \n"
        else ""

        e += if (h.desc=="") "description is required\n"
        else if (h.desc.length < 3) "Desc too less. \n"
        else ""

        e += if (h.cat =="")"category is required\n"
        else ""

        e += if (h.photo.toBytes().isEmpty()) "-Photo is required.\n"
        else ""

        return e
    }




}