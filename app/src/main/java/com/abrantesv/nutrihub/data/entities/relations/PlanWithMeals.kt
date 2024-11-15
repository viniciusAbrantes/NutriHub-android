package com.abrantesv.nutrihub.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.abrantesv.nutrihub.data.entities.Meal
import com.abrantesv.nutrihub.data.entities.Plan

data class PlanWithMeals(
    @Embedded val plan: Plan,

    @Relation(
        parentColumn = "id",
        entityColumn = "planId"
    )
    val meals: List<Meal>
)
