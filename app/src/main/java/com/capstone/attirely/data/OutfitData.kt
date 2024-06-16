package com.capstone.attirely.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OutfitData(
    val category: String,
    val imageUri: String,
    val text: String
) : Parcelable
