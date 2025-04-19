package com.example.makart

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

// Sealed class representing the different screens in the app and their routes
sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object SignUp : Screen("sign_up_screen")
    object Splash : Screen("splash_screen")
    object MainMenu : Screen("main_menu")
    object Explore : Screen("explore_screen")

    // No drawingId indicates that it's a new drawing
    object DrawEditorNew : Screen("draw_editor/new")

    // Route with drawingId for editing existing drawings
    object DrawEditor : Screen("draw_editor/{drawingId}") {
        fun createRoute(drawingId: Int): String = "draw_editor/$drawingId"
    }
    // Route with drawingId and Import tag for editing another users drawings and saving as your own
    object DrawEditorImport : Screen("draw_editor/{drawingId}?isImported={isImported}") {
        fun createRoute(drawingId: Int, isImported: Boolean = false): String = "draw_editor/$drawingId?isImported=$isImported"
    }
}

@Composable
fun AppNavigation(navController: NavHostController,userId: String) {
    //Set the starting screen to splash screen
    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        // At the splash screen route, display SplashScreen
        // Pass nav fn to splash-screen which when called navigates to Main-Menu
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true } // prevents going back to splash screen
                    }
                }
            )
        }
        // Login Screen route
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.MainMenu.route) {
                        popUpTo(Screen.Login.route) { inclusive = true } // prevents going back to login screen
                    }
                }
            )
        }
        // Sign-Up Screen route
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
            )
        }


        // At the main-menu route, display MainMenuScreen and
        // pass the nav-controller to the main-menu-screen component
        composable(Screen.MainMenu.route) {
            MainMenuScreen(navController = navController, userId = userId)
        }
        composable(Screen.Explore.route) {
            ExploreScreen(navController = navController)
        }

        // At the DrawEditorNew route, render DrawEditorScreen with no drawingId, thus navigating to
        // the DrawEditorScreen to create a new drawing
        composable(Screen.DrawEditorNew.route) {
            DrawEditorScreen(
                drawingId = null, // No ID for new drawings
                onBack = { navController.popBackStack() },
                userId = userId
            )
        }

        // At the DrawEditorNew route with drawingId argument in the route, render DrawEditorScreen
        // with drawingId passed in route
        composable(
            route = Screen.DrawEditor.route,
            arguments = listOf(navArgument("drawingId") { type = NavType.IntType })
        ) { backStackEntry ->
            // extracts the drawingId argument passed to the navigation
            val drawingId = backStackEntry.arguments?.getInt("drawingId")
            DrawEditorScreen(
                drawingId = drawingId,
                onBack = { navController.popBackStack() },
                userId = userId
            )
        }

        // At the DrawEditorNewImport route with drawingId argument in the route, render DrawEditorScreen
        // with drawingId and Import tag set to true passed in route
        composable(
            route = Screen.DrawEditorImport.route,
            arguments = listOf(navArgument("drawingId") { type = NavType.IntType })
        ) { backStackEntry ->
            val drawingId = backStackEntry.arguments?.getInt("drawingId")
            DrawEditorScreen(
                drawingId = drawingId,
                isImported = true, // Directly set as imported
                onBack = { navController.popBackStack() },
                userId = userId
            )
        }

    }
}


