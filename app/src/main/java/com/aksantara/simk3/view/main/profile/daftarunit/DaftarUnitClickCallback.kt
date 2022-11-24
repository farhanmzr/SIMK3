package com.aksantara.simk3.view.main.profile.daftarunit

import com.aksantara.simk3.models.Departemen

interface DaftarUnitClickCallback {
    fun onEditClicked(data: Departemen)
    fun onDeleteClicked(data: Departemen)
}