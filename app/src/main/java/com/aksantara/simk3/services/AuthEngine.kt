package com.aksantara.simk3.services

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.utils.AppConstants.STATUS_SUCCESS
import com.aksantara.simk3.utils.DateHelper.getCurrentDate
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class AuthEngine {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestoreRef: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val usersRef: CollectionReference = firestoreRef.collection("users")

    private var STATUS_ERROR = "error"

    //<<<<<<<<<<<<<<<<<<<<<<<<<< AUTHENTICATION METHOD >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun createUserToFirestore(authUser: Users): LiveData<Users> {
        val createdMitraData = MutableLiveData<Users>()
        CoroutineScope(Dispatchers.IO).launch {
            val docRef: DocumentReference = usersRef.document(authUser.userId.toString())
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document: DocumentSnapshot? = task.result
                    if (document?.exists() == false) {
                        docRef.set(authUser).addOnCompleteListener {
                            if (it.isSuccessful) {
                                authUser.isCreated = true
                                createdMitraData.postValue(authUser)
                            } else {
                                Log.d(
                                    "errorCreateUser: ",
                                    it.exception?.message.toString()
                                )
                            }
                        }
                    }
                }
            }
                .addOnFailureListener {
                    Log.d("ErrorGetUser: ", it.message.toString())
                }
        }
        return createdMitraData
    }

    fun loginUser(email: String?, password: String?): LiveData<Users> {
        val authenticatedUser = MutableLiveData<Users>()
        CoroutineScope(Dispatchers.IO).launch {
            firebaseAuth.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user: FirebaseUser? = firebaseAuth.currentUser
                        if (user != null) {
                            val uid = user.uid
                            val name = user.displayName
                            val email = user.email
                            val userInfo = Users(
                                userId = uid,
                                email = email
                            )
                            authenticatedUser.postValue(userInfo)
                        }
                    } else {
                        Log.d("Error Authentication", "Login Failed: ", task.exception)
                        val errorMessage = Users(
                            errorMessage = task.exception?.message
                        )
                        authenticatedUser.postValue(errorMessage)
                    }
                }
        }
        return authenticatedUser
    }

    fun loginAnonym(): LiveData<String> {
        val authenticatedUser = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            firebaseAuth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInAnonymously:success")
                        val user: FirebaseUser? = firebaseAuth.currentUser
                        if (user != null) {
                            authenticatedUser.postValue(STATUS_SUCCESS)
                        }
                    } else {
                        Log.d("Error Authentication", "Login Failed: ", task.exception)
                        STATUS_ERROR = task.exception?.message.toString()
                        authenticatedUser.postValue(STATUS_ERROR)
                    }
                }
        }
        return authenticatedUser
    }

    fun forgotPasswordUser(email: String?): LiveData<Users> {
        val editedUsersData = MutableLiveData<Users>()
        CoroutineScope(Dispatchers.IO).launch {
            firebaseAuth.sendPasswordResetEmail(email!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        val userInfo = Users(
                            isResetPassword = true
                        )
                        editedUsersData.postValue(userInfo)
                    } else {
                        Log.d("Firebase Services", "Error Reset Password: ", task.exception)
                        val errorMessage = Users(
                            errorMessage = task.exception?.message
                        )
                        editedUsersData.postValue(errorMessage)
                    }
                }
        }
        return editedUsersData
    }

    fun updatePasswordUser(email: String?, recentPassword: String?, newPassword: String?): LiveData<String> {
        val statusUpdate = MutableLiveData<String>()
        val user: FirebaseUser? = firebaseAuth.currentUser
        val credential = EmailAuthProvider
            .getCredential(email!!, recentPassword!!)
        CoroutineScope(Dispatchers.IO).launch {
            user!!.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(ContentValues.TAG, "User re-authenticated.")
                        user.updatePassword(newPassword!!)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    statusUpdate.postValue(STATUS_SUCCESS)
                                    Log.d(ContentValues.TAG, "User password updated.")
                                }
                                else {
                                    Log.d("Error Authentication", "passwordUpdate: ", task.exception)
                                    STATUS_ERROR = task.exception?.message.toString()
                                    statusUpdate.postValue(STATUS_ERROR)
                                }
                            }
                    } else {
                        Log.d(ContentValues.TAG, "Error auth failed")
                        STATUS_ERROR = task.exception?.message.toString()
                        statusUpdate.postValue(STATUS_ERROR)
                    }
                }

        }
        return statusUpdate
    }




}