package com.example.fyp2.data

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import androidx.fragment.app.Fragment
import com.example.fyp2.R
import com.google.firebase.firestore.Blob
import java.io.ByteArrayOutputStream

fun Fragment.errorDialog(text: String){
    AlertDialog.Builder(context)
        .setIcon(R.drawable.baseline_error_24)
        .setTitle("Error")
        .setMessage(text)
        .setPositiveButton("Dismiss",null)
        .show()
}

//Usage Crop and Resize bitmap(upscale)

fun Bitmap.crop(width: Int , height: Int): Bitmap {
    //Source width , height and ratio
    val sw = this.width
    val sh = this.height
    val sratio = 1.0 * sw/sh

    //Target offset(x,y) width , height and ratio
    val x: Int
    val y: Int
    val w: Int
    val h: Int
    val ratio = 1.0 * width /height

    if(ratio >= sratio){
        w= sw
        h = (sw/ratio).toInt()
        x =0
        y = (sh-h)/2
    }else{
        //retain height , calculate width
        w=(sh*ratio).toInt()
        h = sh
        x = (sw-w)/2
        y=0
    }

    return Bitmap
        .createBitmap(this,x,y,w,h)//crop
        .scale(width,height)

}
//Usage convert from Bitmap to Blob
@Suppress("DEPRECATION")
fun Bitmap.toBlob():Blob{
    ByteArrayOutputStream().use{
        this.compress(Bitmap.CompressFormat.WEBP,80,it)
        return Blob.fromBytes(it.toByteArray())
    }
}

//usage:convert from blob to bitmap
fun Blob.toBitmap():Bitmap{
    val bytes = this.toBytes()
    return BitmapFactory.decodeByteArray(bytes,0,bytes.size)
}

//usage crop to blob
fun ImageView.cropToBlob(width:Int, height: Int): Blob{
    if(this.drawable == null)
        return Blob.fromBytes(ByteArray(0))
    else
        return this.drawable.toBitmap().crop(width,height).toBlob()
}

