package com.example.fyp2.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp2.Hairstyle
import com.example.fyp2.R


class HairStyleAdapter (
    private val userId: String,
    private val vmFavourite: FavouriteViewModel,
    private val vmLike: LikeViewModel,
    val fn: (ViewHolder, Hairstyle) -> Unit = { _, _ -> })
    :  ListAdapter<Hairstyle, HairStyleAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Hairstyle>() {
        override fun areItemsTheSame(a: Hairstyle, b: Hairstyle)    = a.id == b.id
        override fun areContentsTheSame(a: Hairstyle, b: Hairstyle) = a == b
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val imgPhoto     : ImageView = view.findViewById(R.id.imageView_Hair)
        val hairstyleName      : TextView = view.findViewById(R.id.ItemLayout_HairStyleName)
        val gender     : TextView = view.findViewById(R.id.itemLayout_gender)
        val likeButton: ImageButton = view.findViewById(R.id.likeButton)
        val btnSave: ImageButton = view.findViewById(R.id.saveButton)
        val likeCount : TextView = view.findViewById(R.id.LikeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hairstyle = getItem(position)

        holder.hairstyleName.text = hairstyle.name
        holder.gender.text  = hairstyle.gender
        holder.imgPhoto.setImageBitmap(hairstyle.photo.toBitmap())

        //This is when click the page it will detect whether the favourite button and like button is liked if is then it will turn blue
        val isFavorited = vmFavourite.isHairstyleFavorited(userId, hairstyle.id)
        if (isFavorited) {
            holder.btnSave.setImageResource(R.drawable.baseline_saved_24)
        } else {
            holder.btnSave.setImageResource(R.drawable.baseline_save_24)
        }

        val isLiked = vmLike.isHairstyleLiked(userId, hairstyle.id)
        if (isLiked) {
            holder.likeButton.setImageResource(R.drawable.baseline_thumb_uped_24)
        } else {
            holder.likeButton.setImageResource(R.drawable.baseline_thumb_up_24)
        }

        //display the like count
        val likeCount = vmLike.checkedLikeCountHairstyle(hairstyle.id)
        holder.likeCount.text = "$likeCount"

        fn(holder, hairstyle)


    }

}