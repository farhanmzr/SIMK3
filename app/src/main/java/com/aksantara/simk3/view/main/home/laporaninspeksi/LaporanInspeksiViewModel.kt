package com.aksantara.simk3.view.main.home.laporaninspeksi

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.aksantara.simk3.models.Inspeksi
import com.aksantara.simk3.services.DataEngine
import com.aksantara.simk3.utils.AppConstants
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class LaporanInspeksiViewModel: ViewModel() {

    private val dataEngine: DataEngine = DataEngine()
    private var _inspeksiData = MutableLiveData<Inspeksi>()

    //<<<<<<<<<<<<<<<<<<<<<<<<<< DATA APAR >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun setInspeksiData(inspeksiId: String): LiveData<Inspeksi> {
        _inspeksiData = dataEngine.getInspeksiData(inspeksiId) as MutableLiveData<Inspeksi>
        return _inspeksiData
    }
    fun getInspeksiData(): LiveData<Inspeksi> {
        return _inspeksiData
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<< DATA INSPEKSI >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun editDataInspeksi(inspeksiData: Inspeksi): LiveData<Inspeksi> =
        dataEngine.editDataInspeksi(inspeksiData)

    fun updateMinusAparStatusKurangBagus(departemenId: String, aparId: String, statusApar: String, statusKondisiApar: String): LiveData<String> =
        dataEngine.updateTotalMinusdanStatusAparKurangBagus(departemenId, aparId, statusApar, statusKondisiApar)

    fun updatePlusAparStatusKurangBagus(departemenId: String, aparId: String, statusApar: String, statusKondisiApar: String): LiveData<String> =
        dataEngine.updateTotalPlusdanStatusAparKurangBagus(departemenId, aparId, statusApar, statusKondisiApar)

    fun updateStatusApar(aparId: String, statusApar: String, statusKondisiApar: String): LiveData<String> =
        dataEngine.updateStatusDataApar(aparId, statusApar, statusKondisiApar)

    fun updateImgInspeksi(inspeksiId: String, documentName: String, img: String): LiveData<String> =
        dataEngine.updateDataInspeksi(inspeksiId, documentName, img)

    fun uploadImages(uri: Uri, uid: String, type: String, name: String): LiveData<String> =
        dataEngine.uploadFiles(uri, uid, type, name)

    fun getListInspeksiData(departemen: String, departemenId: String, jenisApar: String, statusKondisiApar: String)
            : LiveData<List<Inspeksi>?> {
        if (departemen == AppConstants.P2K3){
            return if (jenisApar == AppConstants.SEMUA_MEDIA && statusKondisiApar == AppConstants.SEMUA_KONDISI){
                Log.e("semuaInspeksiP2K3", "true")
                dataEngine.getListSemuaInspeksiP2K3("inspeksi").asLiveData()
            } else if (jenisApar == AppConstants.SEMUA_MEDIA && statusKondisiApar != AppConstants.SEMUA_KONDISI){
                Log.e("filterKondisi", "true")
                dataEngine.getListFilterKondisiInspeksiP2K3(statusKondisiApar).asLiveData()
            } else if (jenisApar != AppConstants.SEMUA_MEDIA && statusKondisiApar == AppConstants.SEMUA_KONDISI){
                Log.e("filterMedia", "true")
                dataEngine.getListFilterMediaInspeksiP2K3(jenisApar).asLiveData()
            } else {
                Log.e("allFilterP2K3", "true")
                dataEngine.getListAllFilterInspeksiP2K3(jenisApar, statusKondisiApar).asLiveData()
            }
        } else {
            return if (jenisApar == AppConstants.SEMUA_MEDIA && statusKondisiApar == AppConstants.SEMUA_KONDISI){
                Log.e("semuaInspeksiUnit", "true")
                dataEngine.getListSemuaInspeksiUnit(departemenId).asLiveData()
            } else if (jenisApar == AppConstants.SEMUA_MEDIA && statusKondisiApar != AppConstants.SEMUA_KONDISI){
                Log.e("filterKondisiUnit", "true")
                dataEngine.getListFilterKondisiInspeksiUnit(statusKondisiApar, departemenId).asLiveData()
            } else if (jenisApar != AppConstants.SEMUA_MEDIA && statusKondisiApar == AppConstants.SEMUA_KONDISI){
                Log.e("filterMediaUnit", "true")
                dataEngine.getListFilterMediaInspeksiUnit(jenisApar, departemenId).asLiveData()
            } else {
                Log.e("allFilterUnit", "true")
                dataEngine.getListAllFilterInspeksiUnit(jenisApar, statusKondisiApar, departemenId).asLiveData()
            }
        }

    }

    fun deleteInspeksi(inspeksiId: String): LiveData<String> =
        dataEngine.deleteInspeksi(inspeksiId)

}