package com.abrantesv.nutrihub.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Food(
    @PrimaryKey val id: Int? = null,
    var name: String,
    var amount: Int,
    var unit: String,
    val mealId: Int?
)