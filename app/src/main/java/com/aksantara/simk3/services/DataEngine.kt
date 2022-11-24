package com.aksantara.simk3.services

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aksantara.simk3.models.*
import com.aksantara.simk3.utils.AppConstants.ID_DEPARTEMEN_P2K3
import com.aksantara.simk3.utils.AppConstants.KURANG_BAIK
import com.aksantara.simk3.utils.AppConstants.STATUS_DOCUMENT_EXIST
import com.aksantara.simk3.utils.AppConstants.STATUS_SUCCESS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
class DataEngine {

    private val firestoreRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storageRef: FirebaseStorage = FirebaseStorage.getInstance()

    private val departemenRef: CollectionReference = firestoreRef.collection("departemen")
    private val usersRef: CollectionReference = firestoreRef.collection("users")
    private val aparRef: CollectionReference = firestoreRef.collection("apar")
    private val inspeksiRef: CollectionReference = firestoreRef.collection("inspeksi")
    private val listInspeksiRef: CollectionReference = firestoreRef.collection("listInspeksi")
    private val reminderRef: CollectionReference = firestoreRef.collection("reminder")

    private var STATUS_ERROR = "error"

    //<<<<<<<<<<<<<<<<<<<<<<<<<< GET LIST DATA FROM DATABASE METHOD >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun getListDepartemen(): Flow<List<Departemen>?> {

        return callbackFlow {

            val docRef = departemenRef.whereEqualTo("statusDepartemenDelete", false)
            val listenerRegistration =
                docRef.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listDepartemen = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Departemen>()
                    }
                    trySend(listDepartemen).isSuccess
                    Log.d("Departemens", listDepartemen.toString())
                }
            awaitClose {
                listenerRegistration.remove()
            }
        }
    }

    fun getListReminder(collectionRef: String): Flow<List<Reminder>?> {

        return callbackFlow {

            val colRef = firestoreRef.collection(collectionRef).orderBy("timeStamp", Query.Direction.DESCENDING)
            val listenerRegistration =
                colRef.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listRemainder = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Reminder>()
                    }
                    trySend(listRemainder).isSuccess
                    Log.d("Remainders", listRemainder.toString())
                }
            awaitClose {
                listenerRegistration.remove()
            }
        }
    }

    //<<<APAR>>>
    fun getListApar(departemenId: String): Flow<List<Apar>?> {

        return callbackFlow {

            val doc = aparRef.whereEqualTo("departemenId", departemenId).whereEqualTo("statusDeletedApar", false)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
            val listenerRegistration =
                doc.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listApar = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Apar>()
                    }
                    trySend(listApar).isSuccess
                    Log.d("Apar", listApar.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListIuran: ")
                listenerRegistration.remove()
            }
        }
    }

    fun getListAparKadaluwarsa(departemenId: String): Flow<List<Apar>?> {

        return callbackFlow {

            val doc = aparRef.whereEqualTo("departemenId", departemenId).whereEqualTo("statusKadaluwarsa", true)
                .whereEqualTo("statusDeletedApar", false)
            val listenerRegistration =
                doc.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listApar = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Apar>()
                    }
                    trySend(listApar).isSuccess
                    Log.d("Apar", listApar.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListIuran: ")
                listenerRegistration.remove()
            }
        }
    }

    fun getListAparKurangBaik(departemenId: String): Flow<List<Apar>?> {

        return callbackFlow {

            val doc = aparRef.whereEqualTo("departemenId", departemenId).whereEqualTo("statusApar", KURANG_BAIK)
                .whereEqualTo("statusDeletedApar", false)
            val listenerRegistration =
                doc.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listApar = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Apar>()
                    }
                    trySend(listApar).isSuccess
                    Log.d("Apar", listApar.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListApar: ")
                listenerRegistration.remove()
            }
        }
    }

    fun getListSemuaApar(): Flow<List<Apar>?> {

        return callbackFlow {

            val colRef = firestoreRef.collection("apar").whereEqualTo("statusDeletedApar", false)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
            val listenerRegistration =
                colRef.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listApar = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Apar>()
                    }
                    trySend(listApar).isSuccess
                    Log.d("Apar", listApar.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListIuran: ")
                listenerRegistration.remove()
            }
        }
    }

    fun getListSemuaAparKadaluwarsa(): Flow<List<Apar>?> {

        return callbackFlow {

            val colRef = firestoreRef.collection("apar").whereEqualTo("statusDeletedApar", false)
                .whereEqualTo("statusKadaluwarsa", true)
            val listenerRegistration =
                colRef.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listApar = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Apar>()
                    }
                    trySend(listApar).isSuccess
                    Log.d("AparKadaluwarsa", listApar.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListKadaluwarsa: ")
                listenerRegistration.remove()
            }
        }
    }

    fun getListSemuaAparKurangBaik(): Flow<List<Apar>?> {

        return callbackFlow {

            val colRef = firestoreRef.collection("apar").whereEqualTo("statusDeletedApar", false)
                .whereEqualTo("statusApar", KURANG_BAIK)
            val listenerRegistration =
                colRef.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listApar = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Apar>()
                    }
                    trySend(listApar).isSuccess
                    Log.d("AparKadaluwarsa", listApar.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListKadaluwarsa: ")
                listenerRegistration.remove()
            }
        }
    }

    //<<<INSPEKSI>>>
    fun getListSemuaInspeksiP2K3(collectionRef: String): Flow<List<Inspeksi>?> {

        return callbackFlow {

            val colRef = firestoreRef.collection(collectionRef).whereEqualTo("statusDeletedInspeksi", false)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
            val listenerRegistration =
                colRef.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listInspeksi = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Inspeksi>()
                    }
                    trySend(listInspeksi).isSuccess
                    Log.d("Inspeksi", listInspeksi.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListIuran: ")
                listenerRegistration.remove()
            }
        }
    }

    fun getListFilterMediaInspeksiP2K3(jenisApar: String): Flow<List<Inspeksi>?> {

        return callbackFlow {

            val doc = inspeksiRef.whereEqualTo("jenisApar", jenisApar).whereEqualTo("statusDeletedInspeksi", false)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
            val listenerRegistration =
                doc.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listInspeksi = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Inspeksi>()
                    }
                    trySend(listInspeksi).isSuccess
                    Log.d("Inspeksi", listInspeksi.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListIuran: ")
                listenerRegistration.remove()
            }
        }
    }

    fun getListFilterKondisiInspeksiP2K3(statusKondisiApar: String): Flow<List<Inspeksi>?> {

        return callbackFlow {

            val doc = inspeksiRef.whereEqualTo("statusKondisiApar", statusKondisiApar)
                .whereEqualTo("statusDeletedInspeksi", false).orderBy("timeStamp", Query.Direction.DESCENDING)
            val listenerRegistration =
                doc.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listInspeksi = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Inspeksi>()
                    }
                    trySend(listInspeksi).isSuccess
                    Log.d("Inspeksi", listInspeksi.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListIuran: ")
                listenerRegistration.remove()
            }
        }
    }

    fun getListAllFilterInspeksiP2K3(jenisApar: String, statusKondisiApar: String): Flow<List<Inspeksi>?> {

        return callbackFlow {

            val doc = inspeksiRef.whereEqualTo("jenisApar", jenisApar).whereEqualTo("statusKondisiApar", statusKondisiApar)
                .whereEqualTo("statusDeletedInspeksi", false).orderBy("timeStamp", Query.Direction.DESCENDING)
            val listenerRegistration =
                doc.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listInspeksi = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Inspeksi>()
                    }
                    trySend(listInspeksi).isSuccess
                    Log.d("Inspeksi", listInspeksi.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListIuran: ")
                listenerRegistration.remove()
            }
        }
    }

    fun getListSemuaInspeksiUnit(departemenId: String): Flow<List<Inspeksi>?> {

        return callbackFlow {

            val doc = inspeksiRef.whereEqualTo("departemenId", departemenId)
                .whereEqualTo("statusDeletedInspeksi", false).orderBy("timeStamp", Query.Direction.DESCENDING)
            val listenerRegistration =
                doc.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listInspeksi = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Inspeksi>()
                    }
                    trySend(listInspeksi).isSuccess
                    Log.d("Inspeksi", listInspeksi.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListIuran: ")
                listenerRegistration.remove()
            }
        }
    }

    fun getListFilterMediaInspeksiUnit(jenisApar: String, departemenId: String): Flow<List<Inspeksi>?> {

        return callbackFlow {

            val doc = inspeksiRef.whereEqualTo("jenisApar", jenisApar).whereEqualTo("departemenId", departemenId)
                .whereEqualTo("statusDeletedInspeksi", false).orderBy("timeStamp", Query.Direction.DESCENDING)
            val listenerRegistration =
                doc.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listInspeksi = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Inspeksi>()
                    }
                    trySend(listInspeksi).isSuccess
                    Log.d("Inspeksi", listInspeksi.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListIuran: ")
                listenerRegistration.remove()
            }
        }
    }

    fun getListFilterKondisiInspeksiUnit(statusKondisiApar: String, departemenId: String): Flow<List<Inspeksi>?> {

        return callbackFlow {

            val doc = inspeksiRef.whereEqualTo("statusKondisiApar", statusKondisiApar).whereEqualTo("departemenId", departemenId)
                .whereEqualTo("statusDeletedInspeksi", false).orderBy("timeStamp", Query.Direction.DESCENDING)
            val listenerRegistration =
                doc.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listInspeksi = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Inspeksi>()
                    }
                    trySend(listInspeksi).isSuccess
                    Log.d("Inspeksi", listInspeksi.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListIuran: ")
                listenerRegistration.remove()
            }
        }
    }

    fun getListAllFilterInspeksiUnit(jenisApar: String, statusKondisiApar: String, departemenId: String): Flow<List<Inspeksi>?> {

        return callbackFlow {

            val doc = inspeksiRef.whereEqualTo("jenisApar", jenisApar).whereEqualTo("statusKondisiApar", statusKondisiApar)
                .whereEqualTo("departemenId", departemenId).whereEqualTo("statusDeletedInspeksi", false)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
            val listenerRegistration =
                doc.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listInspeksi = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Inspeksi>()
                    }
                    trySend(listInspeksi).isSuccess
                    Log.d("Inspeksi", listInspeksi.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListIuran: ")
                listenerRegistration.remove()
            }
        }
    }

    fun getListSemuaInspeksiP2K3Limit(collectionRef: String): Flow<List<Inspeksi>?> {

        return callbackFlow {

            val colRef = firestoreRef.collection(collectionRef).whereEqualTo("statusDeletedInspeksi", false)
                .orderBy("timeStamp", Query.Direction.DESCENDING).limit(3)
            val listenerRegistration =
                colRef.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listInspeksi = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Inspeksi>()
                    }
                    trySend(listInspeksi).isSuccess
                    Log.d("Inspeksi", listInspeksi.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListIuran: ")
                listenerRegistration.remove()
            }
        }
    }

    fun getListSemuaInspeksiUnitLimit(departemenId: String): Flow<List<Inspeksi>?> {

        return callbackFlow {

            val doc = inspeksiRef.whereEqualTo("departemenId", departemenId).whereEqualTo("statusDeletedInspeksi", false)
                .orderBy("timeStamp", Query.Direction.DESCENDING).limit(3)
            val listenerRegistration =
                doc.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listInspeksi = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Inspeksi>()
                    }
                    trySend(listInspeksi).isSuccess
                    Log.d("Inspeksi", listInspeksi.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListIuran: ")
                listenerRegistration.remove()
            }
        }
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<< GET DATA FROM DATABASE METHOD >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun getUserData(userId: String): LiveData<Users> {
        val docRef: DocumentReference = usersRef.document(userId)
        val userProfileData = MutableLiveData<Users>()
        CoroutineScope(Dispatchers.IO).launch {
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val userProfile = document.toObject<Users>()
                    userProfileData.postValue(userProfile!!)
                    Log.d("getUserProfile: ", userProfile.toString())
                } else {
                    Log.d("Error getting Doc", "Document Doesn't Exist")
                }
            }
        }
        return userProfileData
    }

    fun getAparData(aparId: String): LiveData<Apar> {
        val docRef: DocumentReference = aparRef.document(aparId)
        val aparData = MutableLiveData<Apar>()
        CoroutineScope(Dispatchers.IO).launch {
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val apars = document.toObject<Apar>()
                    aparData.postValue(apars!!)
                    Log.d("getAparData: ", apars.toString())
                } else {
                    Log.d("Error getting Doc", "Document Doesn't Exist")
                }
            }
        }
        return aparData
    }

    fun getDepartemenData(departemenId: String): LiveData<Departemen> {
        val docRef: DocumentReference = departemenRef.document(departemenId)
        val departemenData = MutableLiveData<Departemen>()
        CoroutineScope(Dispatchers.IO).launch {
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val departemens = document.toObject<Departemen>()
                    departemenData.postValue(departemens!!)
                    Log.d("getDepartemenData: ", departemens.toString())
                } else {
                    Log.d("Error getting Doc", "Document Doesn't Exist")
                }
            }
        }
        return departemenData
    }

    fun getInspeksiData(inspeksiId: String): LiveData<Inspeksi> {
        val docRef: DocumentReference = inspeksiRef.document(inspeksiId)
        val inspeksiData = MutableLiveData<Inspeksi>()
        CoroutineScope(Dispatchers.IO).launch {
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val inspeksi = document.toObject<Inspeksi>()
                    inspeksiData.postValue(inspeksi!!)
                    Log.d("getInspeksiData: ", inspeksi.toString())
                } else {
                    Log.d("Error getting Doc", "Document Doesn't Exist")
                }
            }
        }
        return inspeksiData
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<< UPDATE DATABASE METHOD >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun editDataUser(authUser: Users): LiveData<Users> {
        val editedUserData = MutableLiveData<Users>()
        CoroutineScope(Dispatchers.IO).launch {
            val docRef: DocumentReference = usersRef.document(authUser.userId.toString())
            docRef.set(authUser, SetOptions.merge()).addOnCompleteListener {
                if (it.isSuccessful) {
                    editedUserData.postValue(authUser)
                } else {
                    Log.d(
                        "errorUpdateProfile: ",
                        it.exception?.message.toString()
                    )
                }
            }
                .addOnFailureListener {
                    Log.d(
                        "errorCreateUser: ", it.message.toString()
                    )
                }
        }
        return editedUserData
    }

    fun editDataApar(aparData: Apar): LiveData<Apar> {
        val editedAparData = MutableLiveData<Apar>()
        CoroutineScope(Dispatchers.IO).launch {
            val docRef: DocumentReference = aparRef.document(aparData.aparId.toString())
            docRef.set(aparData, SetOptions.merge()).addOnCompleteListener {
                if (it.isSuccessful) {
                    editedAparData.postValue(aparData)
                } else {
                    Log.d(
                        "errorUpdateApar: ",
                        it.exception?.message.toString()
                    )
                }
            }
                .addOnFailureListener {
                    Log.d(
                        "errorCreateApar: ", it.message.toString()
                    )
                }
        }
        return editedAparData
    }

    fun editDataInspeksi(inspeksiData: Inspeksi): LiveData<Inspeksi> {
        val editedInspeksiData = MutableLiveData<Inspeksi>()
        CoroutineScope(Dispatchers.IO).launch {
            val docRef: DocumentReference = inspeksiRef.document(inspeksiData.inspeksiId.toString())
            docRef.set(inspeksiData, SetOptions.merge()).addOnCompleteListener {
                if (it.isSuccessful) {
                    editedInspeksiData.postValue(inspeksiData)
                } else {
                    Log.d(
                        "errorUpdateInspeksi: ",
                        it.exception?.message.toString()
                    )
                }
            }
                .addOnFailureListener {
                    Log.d(
                        "errorCreateInspeksi: ", it.message.toString()
                    )
                }
        }
        return editedInspeksiData
    }

    fun updateDataInspeksi(inspeksiId: String, documentName: String, img: String): LiveData<String> {
        val updateInspeksi = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            inspeksiRef.document(inspeksiId)
                .update(FieldPath.of(img), documentName)
                .addOnCompleteListener {
                    updateInspeksi.postValue(STATUS_SUCCESS)
                }
                .addOnFailureListener { error ->
                    STATUS_ERROR = error.message.toString()
                    updateInspeksi.postValue(STATUS_ERROR)
                    Log.d("ErrorUpdateData: ", error.message.toString())
                }
        }
        return updateInspeksi
    }

    fun updateDataListInspeksi(listInspeksiId: String, documentName: String, img: String): LiveData<String> {
        val updateListInspeksi = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            listInspeksiRef.document(listInspeksiId)
                .update(FieldPath.of(img), documentName)
                .addOnCompleteListener {
                    updateListInspeksi.postValue(STATUS_SUCCESS)
                }
                .addOnFailureListener { error ->
                    STATUS_ERROR = error.message.toString()
                    updateListInspeksi.postValue(STATUS_ERROR)
                    Log.d("ErrorUpdateData: ", error.message.toString())
                }
        }
        return updateListInspeksi
    }

    fun updateUnitName(departemenId: String, name: String): LiveData<String> {
        val updateName = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            departemenRef.document(departemenId)
                .update("nama", name)
                .addOnCompleteListener {
                    updateName.postValue(STATUS_SUCCESS)
                }
                .addOnFailureListener { error ->
                    STATUS_ERROR = error.message.toString()
                    updateName.postValue(STATUS_ERROR)
                    Log.d("ErrorUpdateNama: ", error.message.toString())
                }
        }
        return updateName
    }

    fun updateStatusDeleteDepartemen(departemenId: String): LiveData<String> {
        val updateStatusDelete = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            departemenRef.document(departemenId)
                .update("statusDepartemenDelete", true)
                .addOnCompleteListener {
                    updateStatusDelete.postValue(STATUS_SUCCESS)
                }
                .addOnFailureListener { error ->
                    STATUS_ERROR = error.message.toString()
                    updateStatusDelete.postValue(STATUS_ERROR)
                    Log.d("ErrorUpdateStatus: ", error.message.toString())
                }
        }
        return updateStatusDelete
    }

    fun updateTotalMinusAparKurangBagus(departemenId: String): LiveData<String> {
        val statusDelete = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            departemenRef.document(ID_DEPARTEMEN_P2K3)
                .update("totalAparKurangBagus", FieldValue.increment(-1))
                .addOnCompleteListener {
                    departemenRef.document(departemenId)
                        .update("totalAparKurangBagus", FieldValue.increment(-1))
                        .addOnCompleteListener {
                            statusDelete.postValue(STATUS_SUCCESS)
                        }
                        .addOnFailureListener { error ->
                            STATUS_ERROR = error.message.toString()
                            statusDelete.postValue(STATUS_ERROR)
                            Log.d("ErrorUnitKurangBagus: ", error.message.toString())
                        }
                }
                .addOnFailureListener { error ->
                    STATUS_ERROR = error.message.toString()
                    statusDelete.postValue(STATUS_ERROR)
                    Log.d("ErrorP2K3KurangBagus: ", error.message.toString())
                }
        }
        return statusDelete
    }

    //Edit Laporan Inspeksi
    fun updateTotalMinusdanStatusAparKurangBagus(departemenId: String, aparId: String, statusApar: String, statusKondisiApar: String): LiveData<String> {
        val statusDelete = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            departemenRef.document(ID_DEPARTEMEN_P2K3)
                .update("totalAparKurangBagus", FieldValue.increment(-1))
                .addOnCompleteListener {
                    departemenRef.document(departemenId)
                        .update("totalAparKurangBagus", FieldValue.increment(-1))
                        .addOnCompleteListener {
                            aparRef.document(aparId)
                                .update("statusApar", statusApar, "statusKondisiTerakhir", statusKondisiApar)
                                .addOnCompleteListener {
                                    statusDelete.postValue(STATUS_SUCCESS)
                                }
                                .addOnFailureListener { error ->
                                    STATUS_ERROR = error.message.toString()
                                    statusDelete.postValue(STATUS_ERROR)
                                    Log.d("ErrorUpdateStatusApar: ", error.message.toString())
                                }
                        }
                        .addOnFailureListener { error ->
                            STATUS_ERROR = error.message.toString()
                            statusDelete.postValue(STATUS_ERROR)
                            Log.d("ErrorUnitKurangBagus: ", error.message.toString())
                        }
                }
                .addOnFailureListener { error ->
                    STATUS_ERROR = error.message.toString()
                    statusDelete.postValue(STATUS_ERROR)
                    Log.d("ErrorP2K3KurangBagus: ", error.message.toString())
                }
        }
        return statusDelete
    }

    fun updateTotalPlusdanStatusAparKurangBagus(departemenId: String, aparId: String, statusApar: String, statusKondisiApar: String): LiveData<String> {
        val statusDelete = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            departemenRef.document(ID_DEPARTEMEN_P2K3)
                .update("totalAparKurangBagus", FieldValue.increment(+1))
                .addOnCompleteListener {
                    departemenRef.document(departemenId)
                        .update("totalAparKurangBagus", FieldValue.increment(+1))
                        .addOnCompleteListener {
                            aparRef.document(aparId)
                                .update("statusApar", statusApar, "statusKondisiTerakhir", statusKondisiApar)
                                .addOnCompleteListener {
                                    statusDelete.postValue(STATUS_SUCCESS)
                                }
                                .addOnFailureListener { error ->
                                    STATUS_ERROR = error.message.toString()
                                    statusDelete.postValue(STATUS_ERROR)
                                    Log.d("ErrorUpdateStatusApar: ", error.message.toString())
                                }
                        }
                        .addOnFailureListener { error ->
                            STATUS_ERROR = error.message.toString()
                            statusDelete.postValue(STATUS_ERROR)
                            Log.d("ErrorUnitKurangBagus: ", error.message.toString())
                        }
                }
                .addOnFailureListener { error ->
                    STATUS_ERROR = error.message.toString()
                    statusDelete.postValue(STATUS_ERROR)
                    Log.d("ErrorP2K3KurangBagus: ", error.message.toString())
                }
        }
        return statusDelete
    }

    fun updateStatusDataApar(aparId: String, statusApar: String, statusKondisiApar: String): LiveData<String> {
        val statusUpdate = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            aparRef.document(aparId)
                .update("statusApar", statusApar, "statusKondisiTerakhir", statusKondisiApar)
                .addOnCompleteListener {
                    statusUpdate.postValue(STATUS_SUCCESS)
                }
                .addOnFailureListener { error ->
                    STATUS_ERROR = error.message.toString()
                    statusUpdate.postValue(STATUS_ERROR)
                    Log.d("ErrorUpdateStatusApar: ", error.message.toString())
                }
        }
        return statusUpdate
    }

    //Inspeksi but Document Exist
    fun updateInspeksiTotalMinusdanStatusAparKurangBagus(departemenId: String, aparId: String, statusApar: String,
                                                         statusKondisiApar: String, dateTerakhirInspeksi: String): LiveData<String> {
        val statusDelete = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            departemenRef.document(ID_DEPARTEMEN_P2K3)
                .update("totalAparKurangBagus", FieldValue.increment(-1))
                .addOnCompleteListener {
                    departemenRef.document(departemenId)
                        .update("totalAparKurangBagus", FieldValue.increment(-1))
                        .addOnCompleteListener {
                            aparRef.document(aparId)
                                .update("statusApar", statusApar, "statusKondisiTerakhir", statusKondisiApar,
                                "dateTerakhirInspeksi", dateTerakhirInspeksi)
                                .addOnCompleteListener {
                                    statusDelete.postValue(STATUS_SUCCESS)
                                }
                                .addOnFailureListener { error ->
                                    STATUS_ERROR = error.message.toString()
                                    statusDelete.postValue(STATUS_ERROR)
                                    Log.d("ErrorUpdateStatusApar: ", error.message.toString())
                                }
                        }
                        .addOnFailureListener { error ->
                            STATUS_ERROR = error.message.toString()
                            statusDelete.postValue(STATUS_ERROR)
                            Log.d("ErrorUnitKurangBagus: ", error.message.toString())
                        }
                }
                .addOnFailureListener { error ->
                    STATUS_ERROR = error.message.toString()
                    statusDelete.postValue(STATUS_ERROR)
                    Log.d("ErrorP2K3KurangBagus: ", error.message.toString())
                }
        }
        return statusDelete
    }

    fun updateInspeksiTotalPlusdanStatusAparKurangBagus(departemenId: String, aparId: String, statusApar: String,
                                                        statusKondisiApar: String, dateTerakhirInspeksi: String): LiveData<String> {
        val statusDelete = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            departemenRef.document(ID_DEPARTEMEN_P2K3)
                .update("totalAparKurangBagus", FieldValue.increment(+1))
                .addOnCompleteListener {
                    departemenRef.document(departemenId)
                        .update("totalAparKurangBagus", FieldValue.increment(+1))
                        .addOnCompleteListener {
                            aparRef.document(aparId)
                                .update("statusApar", statusApar, "statusKondisiTerakhir", statusKondisiApar,
                                    "dateTerakhirInspeksi", dateTerakhirInspeksi)
                                .addOnCompleteListener {
                                    statusDelete.postValue(STATUS_SUCCESS)
                                }
                                .addOnFailureListener { error ->
                                    STATUS_ERROR = error.message.toString()
                                    statusDelete.postValue(STATUS_ERROR)
                                    Log.d("ErrorUpdateStatusApar: ", error.message.toString())
                                }
                        }
                        .addOnFailureListener { error ->
                            STATUS_ERROR = error.message.toString()
                            statusDelete.postValue(STATUS_ERROR)
                            Log.d("ErrorUnitKurangBagus: ", error.message.toString())
                        }
                }
                .addOnFailureListener { error ->
                    STATUS_ERROR = error.message.toString()
                    statusDelete.postValue(STATUS_ERROR)
                    Log.d("ErrorP2K3KurangBagus: ", error.message.toString())
                }
        }
        return statusDelete
    }

    fun updateInspeksiStatusDataApar(aparId: String, statusApar: String, statusKondisiApar: String,
                                     dateTerakhirInspeksi: String): LiveData<String> {
        val statusUpdate = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            aparRef.document(aparId)
                .update("statusApar", statusApar, "statusKondisiTerakhir", statusKondisiApar,
                    "dateTerakhirInspeksi", dateTerakhirInspeksi)
                .addOnCompleteListener {
                    statusUpdate.postValue(STATUS_SUCCESS)
                }
                .addOnFailureListener { error ->
                    STATUS_ERROR = error.message.toString()
                    statusUpdate.postValue(STATUS_ERROR)
                    Log.d("ErrorUpdateStatusApar: ", error.message.toString())
                }
        }
        return statusUpdate
    }

    //
    fun updateTotalPlusAparKurangBagus(departemenId: String): LiveData<String> {
        val statusUpdate = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            departemenRef.document(ID_DEPARTEMEN_P2K3)
                .update("totalAparKurangBagus", FieldValue.increment(+1))
                .addOnCompleteListener {
                    departemenRef.document(departemenId)
                        .update("totalAparKurangBagus", FieldValue.increment(+1))
                        .addOnCompleteListener {
                            statusUpdate.postValue(STATUS_SUCCESS)
                        }
                        .addOnFailureListener { error ->
                            STATUS_ERROR = error.message.toString()
                            statusUpdate.postValue(STATUS_ERROR)
                            Log.d("ErrorUnitKurangBagus: ", error.message.toString())
                        }
                }
                .addOnFailureListener { error ->
                    STATUS_ERROR = error.message.toString()
                    statusUpdate.postValue(STATUS_ERROR)
                    Log.d("ErrorP2K3KurangBagus: ", error.message.toString())
                }
        }
        return statusUpdate
    }

    fun updateTotalMinusKadaluwarsa(departemenId: String): LiveData<String> {
        val statusDelete = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            departemenRef.document(ID_DEPARTEMEN_P2K3)
                .update("totalAparKadaluwarsa", FieldValue.increment(-1))
                .addOnCompleteListener {
                    departemenRef.document(departemenId)
                        .update("totalAparKadaluwarsa", FieldValue.increment(-1))
                        .addOnCompleteListener {
                            statusDelete.postValue(STATUS_SUCCESS)
                        }
                        .addOnFailureListener { error ->
                            STATUS_ERROR = error.message.toString()
                            statusDelete.postValue(STATUS_ERROR)
                            Log.d("ErrorUnitKadaluwarsa: ", error.message.toString())
                        }
                }
                .addOnFailureListener { error ->
                    STATUS_ERROR = error.message.toString()
                    statusDelete.postValue(STATUS_ERROR)
                    Log.d("ErrorP2K3Kadaluwarsa: ", error.message.toString())
                }
        }
        return statusDelete
    }

    fun updateTotalPlusKadaluwarsa(departemenId: String, aparId: String): LiveData<String> {
        val statusUpdate = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            departemenRef.document(ID_DEPARTEMEN_P2K3)
                .update("totalAparKadaluwarsa", FieldValue.increment(+1))
                .addOnCompleteListener {
                    departemenRef.document(departemenId)
                        .update("totalAparKadaluwarsa", FieldValue.increment(+1))
                        .addOnCompleteListener {
                            aparRef.document(aparId)
                                .update("statusKadaluwarsa",true)
                                .addOnCompleteListener {
                                    statusUpdate.postValue(STATUS_SUCCESS)
                                }
                                .addOnFailureListener { error ->
                                    STATUS_ERROR = error.message.toString()
                                    statusUpdate.postValue(STATUS_ERROR)
                                    Log.d("ErrorUnitKadaluwarsa: ", error.message.toString())
                                }
                        }
                        .addOnFailureListener { error ->
                            STATUS_ERROR = error.message.toString()
                            statusUpdate.postValue(STATUS_ERROR)
                            Log.d("ErrorUnitKadaluwarsa: ", error.message.toString())
                        }
                }
                .addOnFailureListener { error ->
                    STATUS_ERROR = error.message.toString()
                    statusUpdate.postValue(STATUS_ERROR)
                    Log.d("ErrorP2K3Kadaluwarsa: ", error.message.toString())
                }
        }
        return statusUpdate
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<< POST DATA TO DATABASE METHOD >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun uploadFiles(uri: Uri, uid: String, type: String, name: String): LiveData<String> {
        val mStorage: FirebaseStorage = Firebase.storage
        val storageRef = mStorage.reference
        val fileRef = storageRef.child("$uid/$type/$name")
        val downloadUrl = MutableLiveData<String>()

        fileRef.putFile(uri).continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            fileRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                downloadUrl.postValue(downloadUri.toString())
                Log.d("uploadFiles: ", downloadUri.toString())
            } else {
                task.exception?.let {
                    throw it
                }
            }
        }
        return downloadUrl
    }

    fun uploadApar(apar: Apar): LiveData<String> {
        val statusUpload = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val docRef: DocumentReference = aparRef.document(apar.aparId.toString())
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document: DocumentSnapshot? = task.result
                    if (document?.exists() == false) {
                        docRef.set(apar).addOnCompleteListener {
                            if (it.isSuccessful) {
                                departemenRef.document(apar.departemenId.toString())
                                    .update("totalApar", FieldValue.increment(1))
                                    .addOnCompleteListener {
                                        departemenRef.document(ID_DEPARTEMEN_P2K3)
                                            .update("totalApar", FieldValue.increment(1))
                                            .addOnCompleteListener {
                                                statusUpload.postValue(STATUS_SUCCESS)
                                            }
                                            .addOnFailureListener { error ->
                                                STATUS_ERROR = error.message.toString()
                                                statusUpload.postValue(STATUS_ERROR)
                                                Log.d("ErrorUpdateAparP2K3: ", error.message.toString())
                                            }
                                    }
                                    .addOnFailureListener { error ->
                                        STATUS_ERROR = error.message.toString()
                                        statusUpload.postValue(STATUS_ERROR)
                                        Log.d("ErrorUpdateAparUnit: ", error.message.toString())
                                    }
                            } else {
                                STATUS_ERROR = it.exception?.message.toString()
                                statusUpload.postValue(STATUS_ERROR)
                                Log.d(
                                    "errorCreateApar: ",
                                    it.exception?.message.toString()
                                )
                            }
                        }
                    }
                }
            }
                .addOnFailureListener {
                    STATUS_ERROR = it.message.toString()
                    statusUpload.postValue(STATUS_ERROR)
                    Log.d("ErrorTambahApar: ", it.message.toString())
                }
        }
        return statusUpload
    }

    fun uploadInspeksi(inspeksi: Inspeksi): LiveData<String> {
        val statusUpload = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val docRef: DocumentReference = inspeksiRef.document(inspeksi.inspeksiId.toString())
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document: DocumentSnapshot? = task.result
                    if (document?.exists() == false) {
                        docRef.set(inspeksi).addOnCompleteListener {
                            if (it.isSuccessful) {
                                aparRef.document(inspeksi.aparId.toString())
                                    .update("statusKondisiTerakhir", inspeksi.statusKondisiApar,
                                        "statusApar", inspeksi.statusApar,
                                        "dateTerakhirInspeksi", inspeksi.createdAt)
                                    .addOnCompleteListener {
                                        statusUpload.postValue(STATUS_SUCCESS)
                                    }
                                    .addOnFailureListener { error ->
                                        STATUS_ERROR = error.message.toString()
                                        statusUpload.postValue(STATUS_ERROR)
                                        Log.d("ErrorUpdateApar: ", error.message.toString())
                                    }
                            } else {
                                STATUS_ERROR = it.exception?.message.toString()
                                statusUpload.postValue(STATUS_ERROR)
                                Log.d(
                                    "errorCreateInspeksi: ",
                                    it.exception?.message.toString()
                                )
                            }
                        }
                    } else {
                        statusUpload.postValue(STATUS_DOCUMENT_EXIST)
                    }
                }
            }
                .addOnFailureListener {
                    STATUS_ERROR = it.message.toString()
                    statusUpload.postValue(STATUS_ERROR)
                    Log.d("ErrorInpeksi: ", it.message.toString())
                }
        }
        return statusUpload
    }

    fun uploadListInspeksi(listInspeksi: ListInspeksi): LiveData<String> {
        val statusUpload = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val docRef: DocumentReference = listInspeksiRef.document(listInspeksi.listInspeksiId.toString())
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document: DocumentSnapshot? = task.result
                    if (document?.exists() == false) {
                        docRef.set(listInspeksi).addOnCompleteListener {
                            if (it.isSuccessful) {
                                statusUpload.postValue(STATUS_SUCCESS)
                            } else {
                                STATUS_ERROR = it.exception?.message.toString()
                                statusUpload.postValue(STATUS_ERROR)
                                Log.d(
                                    "errorCreateList: ",
                                    it.exception?.message.toString()
                                )
                            }
                        }
                    } else {
                        statusUpload.postValue(STATUS_DOCUMENT_EXIST)
                    }
                }
            }
                .addOnFailureListener {
                    STATUS_ERROR = it.message.toString()
                    statusUpload.postValue(STATUS_ERROR)
                    Log.d("ErrorListInpeksi: ", it.message.toString())
                }
        }
        return statusUpload
    }

    fun uploadReminder(remainder: Reminder): LiveData<String> {
        val statusUpload = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val docRef: DocumentReference = reminderRef.document(remainder.reminderId.toString())
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document: DocumentSnapshot? = task.result
                    if (document?.exists() == false) {
                        docRef.set(remainder).addOnCompleteListener {
                            if (it.isSuccessful) {
                                statusUpload.postValue(STATUS_SUCCESS)
                            } else {
                                STATUS_ERROR = it.exception?.message.toString()
                                statusUpload.postValue(STATUS_ERROR)
                                Log.d(
                                    "errorCreateRemainder: ",
                                    it.exception?.message.toString()
                                )
                            }
                        }
                    } else {
                        STATUS_ERROR = "Document is already"
                        statusUpload.postValue(STATUS_ERROR)
                        Log.d("Document is already", "true")
                    }
                }
            }
                .addOnFailureListener {
                    STATUS_ERROR = it.message.toString()
                    statusUpload.postValue(STATUS_ERROR)
                    Log.d("ErrorRemainder: ", it.message.toString())
                }
        }
        return statusUpload
    }

    fun uploadDepartemen(departemen: Departemen): LiveData<String> {
        val statusUpload = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val docRef: DocumentReference = departemenRef.document(departemen.departemenId.toString())
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document: DocumentSnapshot? = task.result
                    if (document?.exists() == false) {
                        docRef.set(departemen).addOnCompleteListener {
                            if (it.isSuccessful) {
                                statusUpload.postValue(STATUS_SUCCESS)
                            } else {
                                STATUS_ERROR = it.exception?.message.toString()
                                statusUpload.postValue(STATUS_ERROR)
                                Log.d(
                                    "errorCreateDepartemen: ",
                                    it.exception?.message.toString()
                                )
                            }
                        }
                    } else {
                        STATUS_ERROR = "Document is already"
                        statusUpload.postValue(STATUS_ERROR)
                        Log.d("Document is already", "true")
                    }
                }
            }
                .addOnFailureListener {
                    STATUS_ERROR = it.message.toString()
                    statusUpload.postValue(STATUS_ERROR)
                    Log.d("ErrorDepartemen: ", it.message.toString())
                }
        }
        return statusUpload
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<< METHOD DELETE DATA FROM DATABASE >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun deleteApar(aparId: String, departemenId: String, reason: String): LiveData<String> {
        val statusDelete = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val docRef: DocumentReference = aparRef.document(aparId)
            docRef.update("statusDeletedApar", true, "reasonDeletedApar", reason)
                .addOnCompleteListener {
                departemenRef.document(ID_DEPARTEMEN_P2K3)
                    .update("totalApar", FieldValue.increment(-1))
                    .addOnCompleteListener {
                        departemenRef.document(departemenId)
                            .update("totalApar", FieldValue.increment(-1))
                            .addOnCompleteListener {
                                statusDelete.postValue(STATUS_SUCCESS)
                            }
                            .addOnFailureListener { error ->
                                STATUS_ERROR = error.message.toString()
                                statusDelete.postValue(STATUS_ERROR)
                                Log.d("ErrorUpdateAparUnit: ", error.message.toString())
                            }
                    }
                    .addOnFailureListener { error ->
                        STATUS_ERROR = error.message.toString()
                        statusDelete.postValue(STATUS_ERROR)
                        Log.d("ErrorUpdateAparP2K3: ", error.message.toString())
                    }
            }
                .addOnFailureListener { statusDelete.postValue(it.message) }
        }
        return statusDelete
    }

    fun deleteInspeksi(inspeksiId: String): LiveData<String> {
        val statusDelete = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val docRef: DocumentReference = inspeksiRef.document(inspeksiId)
            docRef.delete().addOnSuccessListener {
                statusDelete.postValue(STATUS_SUCCESS)
            }
                .addOnFailureListener { statusDelete.postValue(it.message) }
        }
        return statusDelete
    }

    fun deleteQrPicture(qrPicture: String): LiveData<String> {
        val statusDelete = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val ref: StorageReference = storageRef.getReferenceFromUrl(qrPicture)
            ref.delete().addOnSuccessListener {
                statusDelete.postValue(STATUS_SUCCESS)
            }
                .addOnFailureListener { statusDelete.postValue(it.message) }
        }
        return statusDelete
    }

}
