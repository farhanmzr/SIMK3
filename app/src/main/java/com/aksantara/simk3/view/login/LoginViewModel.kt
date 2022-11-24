package com.aksantara.simk3.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.aksantara.simk3.models.Departemen
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.services.AuthEngine
import com.aksantara.simk3.services.DataEngine
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class LoginViewModel: ViewModel() {

    private val dataEngine: DataEngine = DataEngine()
    private val authEngine: AuthEngine = AuthEngine()
    private var _usersProfile = MutableLiveData<Users>()

    fun createdNewUser(authUser: Users): LiveData<Users> =
        authEngine.createUserToFirestore(authUser)

    fun signInWithEmail(email: String, password: String): LiveData<Users> =
        authEngine.loginUser(email, password)

    fun signInWithAnonym(): LiveData<String> =
        authEngine.loginAnonym()

    fun forgotPasswordUser(email: String): LiveData<Users> =
        authEngine.forgotPasswordUser(email)

    //setMitraId
    fun setUsersProfile(userId: String): LiveData<Users> {
        _usersProfile = dataEngine.getUserData(userId) as MutableLiveData<Users>
        return _usersProfile
    }

    //listDepartemen
    fun getListDepartemen(): LiveData<List<Departemen>?> {
        return dataEngine.getListDepartemen().asLiveData()
    }

}