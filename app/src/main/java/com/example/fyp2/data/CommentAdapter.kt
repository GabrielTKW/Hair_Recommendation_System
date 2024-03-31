package com.example.fyp2.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp2.Comment
import com.example.fyp2.R
import com.example.fyp2.User
import java.text.SimpleDateFormat
import java.util.Locale

class CommentAdapter(
    private val vmUser: UserViewModel,
    private val currentUser: User?,
    private val fn: (ViewHolder, Comment) -> Unit = { _, _ -> }
) : ListAdapter<Comment, CommentAdapter.ViewHolder>(DiffCallback) {

    private var formatter = SimpleDateFormat("dd MMMM yyyy '-' hh:mm:ss a", Locale.getDefault())

    companion object DiffCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(a: Comment, b: Comment) = a.id == b.id
        override fun areContentsTheSame(a: Comment, b: Comment) = a == b
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val userId: TextView = view.findViewById(R.id.textViewUserId)
        val content: TextView = view.findViewById(R.id.textViewCommentContent)
        val date: TextView = view.findViewById(R.id.textViewCommentDate)
        val delete: ImageButton = view.findViewById(R.id.buttonDeleteComment)
        val commentId: TextView = view.findViewById(R.id.textViewCommentId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_layout_comment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = getItem(position)
        val user = vmUser.get(comment.userId)

        holder.userId.text = user?.name
        holder.commentId.text = comment.id
        holder.content.text = comment.content
        val formattedTime = comment?.date?.let { formatter.format(it) }
        holder.date.text = formattedTime

        val isAdmin = currentUser?.isAdmin ?: false
        val isOwner = currentUser?.id == comment.userId

        holder.delete.visibility = if (isAdmin || isOwner) View.VISIBLE else View.GONE




        fn(holder, comment)




    }


}
