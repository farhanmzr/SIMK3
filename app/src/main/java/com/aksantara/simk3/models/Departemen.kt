package com.aksantara.simk3.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Departemen(

    var departemenId: String? = null,
    var nama: String? = null,

    var totalApar: Int? = null,
    var totalAparKurangBagus: Int? = null,
    var totalAparKadaluwarsa: Int? = null,

    var statusDepartemenDelete: Boolean? = null

) : Parcelable