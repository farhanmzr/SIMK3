package com.aksantara.simk3.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Apar(
    var aparId: String? = null,
    var qrAparPicture: String? = null,
    var departemenId: String? = null,
    var departemenNama: String? = null,
    var media: String? = null,
    var tipe: String? = null,
    var kapasitas: String? = null,
    var lokasiApar: String? = null,
    var datePembelian: String? = null,
    var dateKadaluwarsa: String? = null,
    var dateTerakhirInspeksi: String? = null,
    var statusKondisiTerakhir: String? = null,
    var statusApar: String? = null,
    var createdAt: String? = null,
    var statusKadaluwarsa: Boolean? = null,
    var locationStorageId: String? = null,
    var statusDeletedApar: Boolean? = null,
    var reasonDeletedApar: String? = null,
    @ServerTimestamp
    var timeStamp: Timestamp? = null
) : Parcelable