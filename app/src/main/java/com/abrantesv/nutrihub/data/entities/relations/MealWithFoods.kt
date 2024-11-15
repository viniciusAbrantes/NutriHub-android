package com.abrantesv.nutrihub.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.abrantesv.nutrihub.data.entities.Food
import com.abrantesv.nutrihub.data.entities.Meal

data class MealWithFoods(
    @Embedded val meal: Meal,
    @Relation(
        parentColumn = "id",
        entityColumn = "mealId"
    )
    val foods: List<Food>
)
