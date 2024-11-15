package com.abrantesv.nutrihub.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.abrantesv.nutrihub.data.entities.Patient
import com.abrantesv.nutrihub.data.entities.Plan

data class PlanAndPatient(
    @Embedded val plan: Plan,

    @Relation(
        parentColumn = "id",
        entityColumn = "planId"
    )
    val patient: Patient
)
