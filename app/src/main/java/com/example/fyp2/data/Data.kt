package com.example.fyp2

import android.graphics.Bitmap
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Friend (
    @DocumentId
    var id : String = "",
    var name : String = "",
    var age : Int = 0,
)

data class Hairstyle(
    @DocumentId
    var id : String = "",
    var name : String ="",
    var desc : String ="",
    var gender : String = "",
    var cat : String ="",
    var date: Date = Date(),
    //Blob = Binary large object
    var photo: Blob = Blob.fromBytes(ByteArray(0)),
    //need add user into it
    var userId :String =""

)

data class Recommendation(
    var Female : String= "",
    var Male : String="",
    var id: String = "",
)

data class User(
    @DocumentId
    var id : String ="",
    var name : String ="",
    var email: String ="",
    var password: String ="",
    var freeze: Boolean = false,
    var isAdmin: Boolean = false,
    var code: String ="",
    var registeredTime: Date = Date(),
)

data class Favourite(
    @DocumentId
    var id : String="",
    var userId : String ="",
    var hairstyleId : String =""
)

data class Like(
    @DocumentId
    var id : String ="",
    var userId : String ="",
    var hairstyleId : String = ""
)

data class Comment(
    @DocumentId
    var id: String ="",
    var userId : String ="",
    var hairstyleId: String = "",
    var content: String ="",
    var date: Date = Date(),
)

