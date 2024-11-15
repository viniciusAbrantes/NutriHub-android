package com.abrantesv.nutrihub.data.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Meal(
    @PrimaryKey val id: Int? = null,
    val name: String,
    val planId: Int,
    @Ignore val foods: MutableList<Food> = mutableListOf()
) {
    constructor(id: Int?, name: String, planId: Int) : this(
        id, name, planId, mutableListOf()
    )
}