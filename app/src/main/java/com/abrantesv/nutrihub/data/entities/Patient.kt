package com.abrantesv.nutrihub.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Patient(
    @PrimaryKey val id: Int? = null,
    val name: String,
    val planId: Int?,
    val email: String?,
    val age: Int,
    val sex: Short,
    val height: Float,
    val weight: Float,
    val lastUpdated: Long = System.currentTimeMillis()
)
