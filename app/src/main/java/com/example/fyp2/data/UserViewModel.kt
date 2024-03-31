package com.example.fyp2.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fyp2.Hairstyle
import com.example.fyp2.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import java.util.Date

class UserViewModel: ViewModel()  {

    private val col = Firebase.firestore.collection("users")
    private val users = MutableLiveData<List<User>>()
    private val sizeLiveData = MutableLiveData<Int>()

    private var searchQuery = ""
    private val result = MutableLiveData<List<User>>()

    fun getResult() = result

    init {
        col.addSnapshotListener { snap, _ ->
            users.value = snap?.toObjects()
            updateResult()
            sizeLiveData.value = users.value?.size ?: 0
        }

    }

    fun getSizeLiveData() = sizeLiveData

    fun get(id:String): User?{
        return users.value?.find{u->u.id==id}
    }

    fun search(query: String) {
        searchQuery = query
        updateResult()
    }

    private fun updateResult() {
        var userList = users.value ?: emptyList()

        // Search by name, user ID, and email
        userList = userList.filter { user ->
            user.name.contains(searchQuery, true) ||
                    user.id.contains(searchQuery, true) ||
                    user.email.contains(searchQuery, true)
        }
        result.value = userList
    }

    fun getByEmail(email: String): User? {
        return users.value?.find { u -> u.email == email }
    }

    fun set(u : User){
        col.document(u.id).set(u)
    }

    fun getAll() = users

    fun delete(id: String){
        col.document(id).delete()
    }

    fun size(): Int {
        return users.value?.size ?: 0
    }

    private fun idExistsUsingList(id:String):Boolean{
        return users.value?.any { h -> h.id == id } ?:false
    }

    fun replaceId(id: String ): String{
        if(idExistsUsingList(id)){
            return id + Date().toString()
        }

        return id
    }

    fun toggleAdminStatus(userId: String): Boolean {
        val user = users.value?.find { u -> u.id == userId }

        if (user != null) {
            // Toggle the admin status
            user.isAdmin = !user.isAdmin
            set(user)
            // Return the updated admin status
            return user.isAdmin
        }

        // Return false if the user is not found
        return false
    }

    fun validate(user: User, insert: Boolean = true): String {
        var errorMessage = ""

        if (insert) {
            errorMessage += if (user.email.isBlank()) "- Email is required.\n"
            else if (!isValidEmail(user.email)) "- Invalid email format.\n"
            else if (isEmailRegistered(user.email)) "- Email is already registered.\n"
            else ""

            errorMessage += if (user.password.isBlank()) "- Password is required.\n"
            else if (!validatePassword(user.password, user.email)) "- Password must meet the criteria( 1 alphanumeric , 1 lowercase alphabet , 1 capital alpha ,1 special character).\n"
            else ""

            errorMessage += if (user.name.isBlank()) "- Name is required.\n"
            else if (!isValidName(user.name)) "- Name must be at least 3 characters long and not alphanumeric only.\n"
            else ""

            errorMessage += if (user.code.isBlank()) "- Security code is required.\n"
            else ""
        }

        return errorMessage
    }

    fun toggleFreezeStatus(userId: String): Boolean {
        val user = users.value?.find { u -> u.id == userId }

        if (user != null) {
            // Toggle the freeze status
            user.freeze = !user.freeze
            set(user)
            // Return the updated freeze status
            return user.freeze
        }

        // Return false if the user is not found
        return false
    }

    fun validateEdit(user: User, insert: Boolean = true): String {
        var errorMessage = ""

        if (insert) {
            errorMessage += if (user.email.isBlank()) "- Email is required.\n"
            else if (!isValidEmail(user.email)) "- Invalid email format.\n"
            else if (user.email != get(user.id)?.email){
                //if not his own email then must check the email has register or not
                if(isEmailRegistered(user.email)) {
                    "- Email is already registered.\n"
                }
                else{
                    ""
                }
            }

            else ""

            errorMessage += if (user.password.isBlank()) "- Password is required.\n"
            else if (!validatePassword(user.password, user.email)) "- Password must meet the criteria( 1 alphanumeric , 1 lowercase alphabet , 1 capital alpha ,1 special character).\n"
            else ""

            errorMessage += if (user.name.isBlank()) "- Name is required.\n"
            else if (!isValidName(user.name)) "- Name must be at least 3 characters long and not alphanumeric only.\n"
            else ""
        }

        return errorMessage
    }

    fun login(email: String, password: String): String {
        val user = users.value?.find { u -> u.email == email }

        if (user == null) {
            return "- Email not registered.\n"
        } else {
            if (user.password == password) {
                return ""
            } else {
                return "- Incorrect password.\n"
            }
        }
    }

    fun forgetPassword(email: String, code: String, newPassword: String): String {
        if (email.isBlank() || code.isBlank() || newPassword.isBlank()) {
            return "- Email, security code, and new password cannot be empty.\n"
        }

        val user = users.value?.find { u -> u.email == email }

        if (user == null) {
            return "- Email not registered.\n"
        } else {
            if (user.code == code) {
                // Security code is correct, update the password
                user.password = newPassword
                set(user)

                return ""
            } else {
                return "- Incorrect security code.\n"
            }
        }
    }



    fun isValidName(name: String): Boolean {
        // Name validation: At least 3 characters and not alphanumeric only
        val nameRegex = Regex("^(?!\\d+\$)[a-zA-Z\\d]{3,}\$")
        return nameRegex.matches(name)
    }

    fun validatePassword(password: String, email: String): Boolean {
        // Password validation: At least one digit, one lowercase, one uppercase, one special character, not the same as email, and at least 6 characters
        val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+\$).{6,}\$")

        val meetsCriteria = passwordRegex.matches(password) && password != email

        return meetsCriteria
    }



    fun isValidEmail(email: String): Boolean {
        // Simple email validation using regex
        val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
        return emailRegex.matches(email)
    }

    fun isEmailRegistered(email: String): Boolean {
        // Check if the email is already registered
        return users.value?.any { u -> u.email == email } ?: false
    }




}