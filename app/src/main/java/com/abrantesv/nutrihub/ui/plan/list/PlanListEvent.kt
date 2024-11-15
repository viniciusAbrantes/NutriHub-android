package com.abrantesv.nutrihub.ui.plan.list

import com.abrantesv.nutrihub.data.entities.Plan


sealed class PlanListEvent {
    data object OnClickAddPlan : PlanListEvent()
    data class OnClickDeletePlan(val plan: Plan) : PlanListEvent()
    data class OnClickEditPlan(val plan: Plan) : PlanListEvent()
}