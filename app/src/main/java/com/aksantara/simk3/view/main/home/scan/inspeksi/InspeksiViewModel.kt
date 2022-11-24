package com.aksantara.simk3.view.main.home.scan.inspeksi

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aksantara.simk3.models.Apar
import com.aksantara.simk3.models.Inspeksi
import com.aksantara.simk3.models.ListInspeksi
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.services.AuthEngine
import com.aksantara.simk3.services.DataEngine
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class InspeksiViewModel: ViewModel() {

    private val dataEngine: DataEngine = DataEngine()
    private var _aparData = MutableLiveData<Apar>()
    private var _inspeksiData = MutableLiveData<Inspeksi>()

    //<<<<<<<<<<<<<<<<<<<<<<<<<< DATA APAR >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun setAparData(aparId: String): LiveData<Apar> {
        _aparData = dataEngine.getAparData(aparId) as MutableLiveData<Apar>
        return _aparData
    }
    fun getAparData(): LiveData<Apar> {
        return _aparData
    }

    fun updateImgInspeksi(inspeksiId: String, documentName: String, img: String): LiveData<String> =
        dataEngine.updateDataInspeksi(inspeksiId, documentName, img)

    fun updateImgListInspeksi(listInspeksiId: String, documentName: String, img: String): LiveData<String> =
        dataEngine.updateDataListInspeksi(listInspeksiId, documentName, img)

    //<<<<<<<<<<<<<<<<<<<<<<<<<< UPLOAD IMAGES >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun uploadImages(uri: Uri, uid: String, type: String, name: String): LiveData<String> =
        dataEngine.uploadFiles(uri, uid, type, name)

    fun uploadDataInspeksi(inspeksi: Inspeksi): LiveData<String> =
        dataEngine.uploadInspeksi(inspeksi)

    fun editDataInspeksi(inspeksiData: Inspeksi): LiveData<Inspeksi> =
        dataEngine.editDataInspeksi(inspeksiData)

    fun uploadDataListInspeksi(listInspeksi: ListInspeksi): LiveData<String> =
        dataEngine.uploadListInspeksi(listInspeksi)

    //<<<<<<<<<<<<<<<<<<<<<<<<<< UPDATE DEPARTEMEN >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun updateAparKurangBagus(departemenId: String): LiveData<String> =
        dataEngine.updateTotalPlusAparKurangBagus(departemenId)

    //<<<<<INSPEKSI GET>>>>
    fun setInspeksiData(inspeksiId: String): LiveData<Inspeksi> {
        _inspeksiData = dataEngine.getInspeksiData(inspeksiId) as MutableLiveData<Inspeksi>
        return _inspeksiData
    }

    fun updateInspeksiMinusAparStatusKurangBagus(departemenId: String, aparId: String, statusApar: String,
                                         statusKondisiApar: String, dateTerakhirInspeksi: String): LiveData<String> =
        dataEngine.updateInspeksiTotalMinusdanStatusAparKurangBagus(departemenId, aparId, statusApar, statusKondisiApar, dateTerakhirInspeksi)

    fun updateInspeksiPlusAparStatusKurangBagus(departemenId: String, aparId: String, statusApar: String,
                                        statusKondisiApar: String, dateTerakhirInspeksi: String): LiveData<String> =
        dataEngine.updateInspeksiTotalPlusdanStatusAparKurangBagus(departemenId, aparId, statusApar, statusKondisiApar, dateTerakhirInspeksi)

    fun updateInspeksiStatusApar(aparId: String, statusApar: String, statusKondisiApar: String, dateTerakhirInspeksi: String): LiveData<String> =
        dataEngine.updateInspeksiStatusDataApar(aparId, statusApar, statusKondisiApar, dateTerakhirInspeksi)



}