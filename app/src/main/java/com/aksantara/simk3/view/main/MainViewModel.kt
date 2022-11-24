package com.aksantara.simk3.view.main

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.aksantara.simk3.models.*
import com.aksantara.simk3.services.AuthEngine
import com.aksantara.simk3.services.DataEngine
import com.aksantara.simk3.utils.AppConstants
import com.aksantara.simk3.utils.AppConstants.GUEST
import com.aksantara.simk3.utils.AppConstants.P2K3
import com.aksantara.simk3.utils.AppConstants.SEMUA_KONDISI
import com.aksantara.simk3.utils.AppConstants.SEMUA_MEDIA
import com.aksantara.simk3.utils.AppConstants.SEMUA_UNIT
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MainViewModel: ViewModel() {

    private val authEngine: AuthEngine = AuthEngine()
    private val dataEngine: DataEngine = DataEngine()
    private var _usersProfile = MutableLiveData<Users>()
    private var _aparData = MutableLiveData<Apar>()
    private var _departemenData = MutableLiveData<Departemen>()

    //<<<<<<<<<<<<<<<<<<<<<<<<<< DATA USER >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun setUserProfile(userId: String): LiveData<Users> {
        _usersProfile = dataEngine.getUserData(userId) as MutableLiveData<Users>
        return _usersProfile
    }
    fun getUserData(): LiveData<Users> {
        return _usersProfile
    }

    fun editDataUser(authUser: Users): LiveData<Users> =
        dataEngine.editDataUser(authUser)

    fun updatePasswordUser(email: String, recentPassword: String, newPassword: String): LiveData<String> =
        authEngine.updatePasswordUser(email, recentPassword, newPassword)

    //<<<<<<<<<<<<<<<<<<<<<<<<<< DATA APAR >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun editDataApar(aparData: Apar): LiveData<Apar> =
        dataEngine.editDataApar(aparData)

    fun uploadDataApar(apar: Apar): LiveData<String> =
        dataEngine.uploadApar(apar)

    fun getListDataApar(departemenId: String, departemenNama: String): LiveData<List<Apar>?> {
        return if (departemenNama == P2K3){
            dataEngine.getListSemuaApar().asLiveData()
        } else {
            dataEngine.getListApar(departemenId).asLiveData()
        }
    }

    fun getListDataAparKadaluwarsa(departemenId: String, departemenNama: String): LiveData<List<Apar>?> {
        return if (departemenNama == P2K3 || departemenNama == GUEST){
            dataEngine.getListSemuaAparKadaluwarsa().asLiveData()
        } else {
            dataEngine.getListAparKadaluwarsa(departemenId).asLiveData()
        }
    }

    fun getListDataAparKurangBaik(departemenId: String, departemenNama: String): LiveData<List<Apar>?> {
        return if (departemenNama == P2K3 || departemenNama == GUEST){
            dataEngine.getListSemuaAparKurangBaik().asLiveData()
        } else {
            dataEngine.getListAparKurangBaik(departemenId).asLiveData()
        }
    }

    fun setAparData(aparId: String): LiveData<Apar> {
        _aparData = dataEngine.getAparData(aparId) as MutableLiveData<Apar>
        return _aparData
    }
    fun getAparData(): LiveData<Apar> {
        return _aparData
    }

    fun deleteApar(aparId: String, departemenId: String, reason: String): LiveData<String> =
        dataEngine.deleteApar(aparId, departemenId, reason)

    fun updateAparKurangBagus(departemenId: String): LiveData<String> =
        dataEngine.updateTotalMinusAparKurangBagus(departemenId)

    fun updateMinusAparKadaluwarsa(departemenId: String): LiveData<String> =
        dataEngine.updateTotalMinusKadaluwarsa(departemenId)

    fun updatePlusAparKadaluwarsa(departemenId: String, aparId: String): LiveData<String> =
        dataEngine.updateTotalPlusKadaluwarsa(departemenId, aparId)

    fun deleteQrPictureApar(qrPicture: String): LiveData<String> =
        dataEngine.deleteQrPicture(qrPicture)

    //<<<<<<<<<<<<<<<<<<<<<<<<<< UPLOAD IMAGES >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun uploadImages(uri: Uri, uid: String, type: String, name: String): LiveData<String> =
        dataEngine.uploadFiles(uri, uid, type, name)

    //<<<<<<<<<<<<<<<<<<<<<<<<<< DATA DEPARTEMEN >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun setDepartemenData(departemenId: String): LiveData<Departemen> {
        _departemenData = dataEngine.getDepartemenData(departemenId) as MutableLiveData<Departemen>
        return _departemenData
    }
    fun getDepartemenData(): LiveData<Departemen> {
        return _departemenData
    }
    fun getListDepartemen(): LiveData<List<Departemen>?> {
        return dataEngine.getListDepartemen().asLiveData()
    }
    fun updateUnitName(departemenId: String, name: String): LiveData<String> =
        dataEngine.updateUnitName(departemenId, name)
    fun deleteDepartemen(departemenId: String): LiveData<String> =
        dataEngine.updateStatusDeleteDepartemen(departemenId)
    fun uploadDataDepartemen(departemen: Departemen): LiveData<String> =
        dataEngine.uploadDepartemen(departemen)

    //<<<<<<<<<<<<<<<<<<<<<<<<<< DATA REMAINDER >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun uploadDataRemainder(remainder: Reminder): LiveData<String> =
        dataEngine.uploadReminder(remainder)

    fun getListDataReminder(): LiveData<List<Reminder>?> {
        return dataEngine.getListReminder("reminder").asLiveData()
    }

    fun getListInspeksiData(departemen: String, departemenId: String): LiveData<List<Inspeksi>?> {
        return if (departemen == P2K3) {
            dataEngine.getListSemuaInspeksiP2K3Limit("inspeksi").asLiveData()
        } else {
            dataEngine.getListSemuaInspeksiUnitLimit(departemenId).asLiveData()
        }
    }



}