package com.aksantara.simk3.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Inspeksi(

    var inspeksiId: String? = null,
    var departemenId: String? = null,
    var aparId: String? = null,
    var userId: String? = null,
    var userDepartemen: String? = null,
    var userPicture: String? = null,

    var departemenNama: String? = null,
    var lokasiApar: String? = null,
    var namaPetugas: String? = null,
    var jenisApar: String? = null,
    var statusKondisiApar: String? = null,
    var statusApar: String? = null,

    var kondisiTabung: String? = null,
    var isiApar: String? = null,
    var tekananTabung: String? = null,
    var handle: String? = null,
    var label: String? = null,
    var mulutPancar: String? = null,
    var pipaPancar: String? = null,
    var tandaPemasangan: String? = null,
    var jarakTanda: String? = null,
    var jarakApar: String? = null,
    var warnaTabung: String? = null,
    var pemasanganApar: String? = null,
    var petunjukPenggunaan: String? = null,
    var catatanPemeriksaan: String? = null,

    var createdAt: String? = null,
    var locationStorageId: String? = null,
    var statusDeletedInspeksi: Boolean? = null,
    var updatedAt: String? = null,

    var imgKondisiTabung: String? = null,
    var imgTekananTabung: String? = null,
    var imgHandle: String? = null,
    var imgLabel: String? = null,
    var imgMulutPancar: String? = null,
    var imgPipaPancar: String? = null,
    var imgWarnaTabung: String? = null,
    var imgPemasangan: String? = null,

    @ServerTimestamp
    val timeStamp: Timestamp? = null

) : Parcelable