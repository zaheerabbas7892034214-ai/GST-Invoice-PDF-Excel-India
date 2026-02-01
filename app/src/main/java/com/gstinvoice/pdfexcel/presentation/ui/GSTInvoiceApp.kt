package com.gstinvoice.pdfexcel.presentation.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gstinvoice.pdfexcel.presentation.ui.export.ExportScreen
import com.gstinvoice.pdfexcel.presentation.ui.home.HomeScreen
import com.gstinvoice.pdfexcel.presentation.ui.import.ImportScreen
import com.gstinvoice.pdfexcel.presentation.ui.preview.PreviewScreen
import com.gstinvoice.pdfexcel.presentation.ui.settings.SettingsScreen
import com.gstinvoice.pdfexcel.presentation.ui.splash.SplashScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Import : Screen("import")
    object Preview : Screen("preview/{invoiceIds}") {
        fun createRoute(invoiceIds: List<Long>): String {
            return "preview/${invoiceIds.joinToString(",")}"
        }
    }
    object Export : Screen("export/{invoiceIds}") {
        fun createRoute(invoiceIds: List<Long>): String {
            return "export/${invoiceIds.joinToString(",")}"
        }
    }
    object Settings : Screen("settings")
}

@Composable
fun GSTInvoiceApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToImport = {
                    navController.navigate(Screen.Import.route)
                },
                onNavigateToPreview = { invoiceIds ->
                    navController.navigate(Screen.Preview.createRoute(invoiceIds))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Import.route) {
            ImportScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToPreview = { invoiceIds ->
                    navController.navigate(Screen.Preview.createRoute(invoiceIds)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(
            route = Screen.Preview.route,
            arguments = listOf(
                navArgument("invoiceIds") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val invoiceIdsString = backStackEntry.arguments?.getString("invoiceIds") ?: ""
            val invoiceIds = invoiceIdsString.split(",").mapNotNull { it.toLongOrNull() }
            
            PreviewScreen(
                invoiceIds = invoiceIds,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToExport = { ids ->
                    navController.navigate(Screen.Export.createRoute(ids))
                }
            )
        }

        composable(
            route = Screen.Export.route,
            arguments = listOf(
                navArgument("invoiceIds") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val invoiceIdsString = backStackEntry.arguments?.getString("invoiceIds") ?: ""
            val invoiceIds = invoiceIdsString.split(",").mapNotNull { it.toLongOrNull() }
            
            ExportScreen(
                invoiceIds = invoiceIds,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
