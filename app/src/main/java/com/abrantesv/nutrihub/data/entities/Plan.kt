package com.abrantesv.nutrihub.data.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Plan(
    @PrimaryKey val id: Int? = null,
    val name: String,
    val isTemplate: Boolean,
    @Ignore val meals: MutableList<Meal>
) {
    constructor(id: Int?, name: String, isTemplate: Boolean) : this(
        id, name, isTemplate, mutableListOf()
    )
}