package com.abrantesv.nutrihub

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abrantesv.nutrihub.data.entities.Food
import com.abrantesv.nutrihub.data.entities.Meal
import com.abrantesv.nutrihub.data.entities.Patient
import com.abrantesv.nutrihub.data.entities.Plan
import com.abrantesv.nutrihub.data.patient.PatientRepository
import com.abrantesv.nutrihub.ui.patient.add_edit.AddOrEditPatientScreen
import com.abrantesv.nutrihub.ui.patient.list.PatientListScreen
import com.abrantesv.nutrihub.ui.plan.add_edit.AddOrEditPlanScreen
import com.abrantesv.nutrihub.ui.plan.list.PlanListScreen
import com.abrantesv.nutrihub.util.Routes
import com.abrantesv.nutrihub.util.Routes.DEFAULT_INVALID_ID
import com.abrantesv.nutrihub.util.Routes.PATIENT_ID_ARGUMENT
import com.abrantesv.nutrihub.util.Routes.PLAN_ID_ARGUMENT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var repository: PatientRepository

    private fun initializeDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            if (repository.getAllPatients().firstOrNull()?.isEmpty() == false) return@launch
            val patients = listOf(
                Patient(
                    null,
                    "Vinicius Abrantes Pereira",
                    null,
                    "vinicius_abrantes@teste.com",
                    24,
                    0,
                    1.73f,
                    78f,
                ), Patient(
                    null,
                    "Julianne Silva",
                    null,
                    "julianne_silva@teste.com",
                    23,
                    1,
                    1.68f,
                    68f
                ), Patient(
                    null,
                    "Maria Souza Lima",
                    null,
                    "maria_souza@hotmail.com",
                    50,
                    1,
                    1.59f,
                    67f,
                ), Patient(
                    null,
                    "João José Alves",
                    null,
                    "joao_jose@live.com",
                    43,
                    0,
                    1.8f,
                    90f,
                )
            )

            val foods = listOf(
                Food(0, "Pão", 1, "un", 0),
                Food(1, "Queijo", 20, "g", 0),
                Food(2, "Leite", 200, "ml", 0),
                Food(3, "Arroz", 150, "g", 1),
                Food(4, "Feijao", 150, "g", 1),
                Food(5, "Frango", 200, "g", 1),
            )

            val meals = listOf(
                Meal(0, "Café da manhã", 0), Meal(1, "Almoço", 0)
            )

            val plans = listOf(
                Plan(0, "Plano Geral", true)
            )

            patients.forEach { repository.insertPatient(it) }
            foods.forEach { repository.insertFood(it) }
            meals.forEach { repository.insertMeal(it) }
            plans.forEach { repository.insertPlan(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            initializeDatabase()
            val navController = rememberNavController()

            Scaffold(modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(), bottomBar = {

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                Log.d(TAG, "route=${currentDestination?.route}")

                var shouldShowBottomBar by remember { (mutableStateOf(true)) }
                currentDestination?.route?.let { route ->
                    shouldShowBottomBar =
                        route.contains(Routes.PATIENT_LIST) || route.contains(Routes.MEAL_PLAN_LIST)
                }
                if (shouldShowBottomBar) {
                    BottomNavigation {
                        val items = listOf(Screen.Patients, Screen.Plans)
                        items.forEach { screen ->
                            BottomNavigationItem(icon = {
                                Icon(screen.icon, contentDescription = null)
                            },
                                label = { Text(stringResource(screen.resourceId)) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                })
                        }
                    }
                }
            }) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Patients.route,
                    Modifier.padding(innerPadding)
                ) {
                    composable(Screen.Patients.route) {
                        PatientListScreen(onNavigate = {
                            navController.navigate(it.route)
                        })
                    }

                    composable(
                        Routes.ADD_OR_EDIT_PATIENT + "?$PATIENT_ID_ARGUMENT={$PATIENT_ID_ARGUMENT}",
                        arguments = listOf(navArgument(name = PATIENT_ID_ARGUMENT) {
                            type = NavType.IntType
                            defaultValue = DEFAULT_INVALID_ID
                        })
                    ) {
                        AddOrEditPatientScreen(onPopBackStack = { navController.popBackStack() })
                    }

                    composable(Screen.Plans.route) {
                        PlanListScreen(onNavigate = {
                            navController.navigate(it.route)
                        })
                    }

                    composable(
                        Routes.ADD_OR_EDIT_PLAN + "?$PLAN_ID_ARGUMENT={$PLAN_ID_ARGUMENT}",
                        arguments = listOf(navArgument(name = PLAN_ID_ARGUMENT) {
                            type = NavType.IntType
                            defaultValue = DEFAULT_INVALID_ID
                        })
                    ) {
                        AddOrEditPlanScreen(onPopBackStack = { navController.popBackStack() })
                    }
                }
            }
        }
    }

    companion object {
        val TAG = MainActivity::class.simpleName
    }
}

sealed class Screen(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    data object Patients : Screen(Routes.PATIENT_LIST, R.string.patients_nav, Icons.Filled.Face)
    data object Plans : Screen(
        Routes.MEAL_PLAN_LIST, R.string.meal_plans_nav, Icons.Filled.ShoppingCart
    )
}