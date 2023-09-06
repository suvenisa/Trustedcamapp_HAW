package com.dh.myapplication.simple.barcode_scanner

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreen(onGranted: () -> Unit) {
    val permissionsList: List<String> =
        listOf(
            Manifest.permission.CAMERA)


    val permissionsState = rememberMultiplePermissionsState(permissions = permissionsList)

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        while (true) {
            permissionsState.launchMultiplePermissionRequest()
            if (permissionsState.allPermissionsGranted) {
                onGranted.invoke()
                break
            }
            delay(2000L) // 2-second delay
        }
    }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Bluetooth Permission Needed")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (permissionsState.allPermissionsGranted) {
                        onGranted.invoke()
                    } else {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                }
            ) {
                Text(text = "Grant Permission")
            }
        }

}
