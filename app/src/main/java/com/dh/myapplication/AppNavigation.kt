package com.dh.myapplication

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dh.myapplication.core.utils.RequestType
import com.dh.myapplication.simple.Dashboard
import com.dh.myapplication.simple.barcode_scanner.BarCodeScannerScreenCamera
import com.dh.myapplication.simple.barcode_scanner.PermissionScreen
import com.dh.myapplication.simple.simpleViewModel
import com.dh.myapplication.ui.AppScreens

@Composable
fun AppNavigation(viewmodel: simpleViewModel = hiltViewModel(), callLocation: (RequestType) -> Unit) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreens.DashboardTwo.route) {

        composable(AppScreens.DashboardTwo.route) {

            Dashboard(viewmodel,callLocation = callLocation) {
                navController.navigate(AppScreens.Permission.route)
            }

        }

        composable(AppScreens.Permission.route) {
            PermissionScreen(onGranted = {
                navController.popBackStack()

                navController.navigate(AppScreens.BarCode.route)
            })
        }

        composable(AppScreens.BarCode.route) {

            BarCodeScannerScreenCamera(viewmodel) {
                navController.popBackStack()

            }


        }

    }
}