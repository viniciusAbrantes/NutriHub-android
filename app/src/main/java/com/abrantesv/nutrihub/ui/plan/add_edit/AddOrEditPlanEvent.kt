package com.abrantesv.nutrihub.ui.plan.add_edit

import com.abrantesv.nutrihub.data.entities.Meal

sealed class AddOrEditPlanEvent {
    data class OnChangePlanName(val name: String) : AddOrEditPlanEvent()
    data class OnChangeMealName(val name: String) : AddOrEditPlanEvent()
    data class OnChangeFood(val id: Int, val name: String, val unit: String, val amount: Int) : AddOrEditPlanEvent()
    data class OnClickEditMeal(val meal: Meal) : AddOrEditPlanEvent()
    data object OnClickAddMeal : AddOrEditPlanEvent()
    data object OnClickAddFood : AddOrEditPlanEvent()
    data object OnClickCloseMeal : AddOrEditPlanEvent()
    data object OnClickClosePlan : AddOrEditPlanEvent()
    data object OnClickCancelMeal : AddOrEditPlanEvent()
}