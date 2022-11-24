package com.aksantara.simk3.models

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class Users(
    var userId: String? = null,
    var userPicture: String? = null,
    var nama: String? = null,
    var departemen: String? = null,
    var departemenId: String? = null,
    var email: String? = null,
    var registeredAt: String? = null,
    var userVerification: Boolean? = null,

    @get:Exclude
    var isGuest: Boolean? = null,
    @get:Exclude
    var isAuthenticated: Boolean? = null,
    @get:Exclude
    var isCreated: Boolean? = null,
    @get:Exclude
    var errorMessage: String? = null,
    @get:Exclude
    var password: String? = null,
    @get:Exclude
    var confirmPassword: String? = null,
    @get:Exclude
    var isResetPassword: Boolean? = null

): Parcelable