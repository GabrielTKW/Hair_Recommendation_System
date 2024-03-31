import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp2.Hairstyle
import com.example.fyp2.R
import com.example.fyp2.User
import com.example.fyp2.data.HairStyleAdapter

class UserAdapter(
    val fn: (ViewHolder, User) -> Unit = { _, _ -> })
 : ListAdapter<User, UserAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(a: User, b: User) = a.id == b.id
        override fun areContentsTheSame(a: User, b: User) = a == b
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val userIdTextView: TextView = view.findViewById(R.id.userIdTextView)
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val emailTextView: TextView = view.findViewById(R.id.emailTextView)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
        val freezeButton: Button = view.findViewById(R.id.freezeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)

        holder.userIdTextView.text = "User ID: ${user.id}"
        holder.nameTextView.text = "Name: ${user.name}"
        holder.emailTextView.text = "Email: ${user.email}"
        fn(holder, user)
    }
}
