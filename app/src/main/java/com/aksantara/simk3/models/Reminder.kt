package com.aksantara.simk3.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Reminder(

    var reminderId: String? = null,
    var reminderTitle: String? = null,
    var reminderDesc: String? = null,
    var reminderDate: String? = null,

    @ServerTimestamp
    val timeStamp: Timestamp? = null

    ) : Parcelable