package com.capstone.attirely.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class OutfitData(val imageUri: String?, val text: String) : Parcelable