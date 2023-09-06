package com.dh.myapplication.simple

import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dh.myapplication.core.QrCodeHandler
import com.dh.myapplication.core.data.DeviceState
import com.dh.myapplication.core.permissionsList
import com.dh.myapplication.core.utils.RequestType
import com.dh.myapplication.core.utils.TextUtils
import com.dh.myapplication.core.utils.UserVerifiedDialog
import com.dh.myapplication.core.utils.getFingerprint
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(viewmodel: simpleViewModel, callLocation: (RequestType) -> Unit, nav: () -> Unit) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TrustedCam",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) {
        DashboardTwoContent(
            Modifier
                .padding(it), viewmodel, callLocation,
            nav
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DashboardTwoContent(padding: Modifier, viewmodel: simpleViewModel, callLocation: (RequestType) -> Unit, nav: () -> Unit) {


    // get hash from blockchain
    val blockHash by viewmodel.blockHash.collectAsState()
    val scannedString by viewmodel.scannedString.collectAsState()
    val blockHashTime by viewmodel.blockHashTime.collectAsState()
    val address by viewmodel.address.collectAsState()


    // list
    var list by remember { mutableStateOf(emptyList<DeviceState>()) }


    var QrCodeImage: Bitmap? by remember { mutableStateOf(null) }
    var fingerPrint: String by remember { mutableStateOf("") }
    var pdfPath: String by remember { mutableStateOf("") }


    val qrCodeConverter by remember { mutableStateOf(QrCodeHandler()) }

    val scope = rememberCoroutineScope()

    val getHash: () -> Unit = {

        scope.launch(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                viewmodel.printBlockchainBalance()
            }
        }
    }


    var isClicked by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        list = viewmodel.initCheck()
        // reset the user verification
        viewmodel.resetUserVerification()
    }

    val context = LocalContext.current
    LaunchedEffect(key1 = scannedString) {
        if (scannedString.isNotBlank()) {

            if (scannedString == blockHash) {
                viewmodel.setHash(scannedString)

            } else {
                Toast.makeText(context, "Invalid QrCode", Toast.LENGTH_SHORT).show()
            }

        }
    }



    LaunchedEffect(key1 = blockHash) {
        scope.launch(Dispatchers.IO) {

            if (blockHash.isNotBlank()) {

                // reset the user verification
                viewmodel.resetUserVerification()

                QrCodeImage = qrCodeConverter.generateQrCode(blockHash)
                val methodResult = BitmapAsPdf().saveBitmapAsPdf(context, QrCodeImage!!, "QrCode")

                System.out.println("methodResult " + methodResult)
                if (methodResult.second) {
                    pdfPath = methodResult.first
                        TextUtils().shareVideo2(context,methodResult.first)

                }

                System.out.println("methodResult " + methodResult)

                isClicked = false
            }


        }

    }


    val userVerification by viewmodel.userVerification.collectAsState()
    val shareFile by viewmodel.shareFile.collectAsState()
    val current = LocalContext.current

    LaunchedEffect(key1 = userVerification) {
        if (userVerification) {
            isClicked = false
            viewmodel.resetUserVerification()
            Toast.makeText(current, " User Verified ", Toast.LENGTH_SHORT).show()
        }
    }

    val (dialogStatus, dialog) = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = shareFile) {

        if (shareFile != null) {
            dialog(true)
        }

    }


    val multiplePermissionsState = rememberMultiplePermissionsState(permissions = permissionsList)

    LaunchedEffect(key1 = multiplePermissionsState.allPermissionsGranted) {
        if (multiplePermissionsState.allPermissionsGranted) {

        } else {
            multiplePermissionsState.launchMultiplePermissionRequest()
        }

    }



    Column(
        modifier = padding
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp)
    ) {

        Spacer(modifier = Modifier.height(32.dp))




        list.forEach {
            displayDeviceStatus(it)
        }

        Spacer(modifier = Modifier.height(8.dp))

       /* Box(
            modifier = Modifier
                .fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center
        ) {

            Button(onClick = {

                getHash.invoke()
            }

            ) {
                Text(text = "Get Hash")
            }
        } */

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center
        ) {

            Button(onClick = {

                fingerPrint = getFingerprint(current)
            }

            ) {
                Text(text = "Fingerprint")
            }


        }
        Text(text = "Fingerprint : $fingerPrint", fontSize = 12.sp, modifier = Modifier.padding(8.dp))



        Box(
            modifier = Modifier
                .fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center
        ) {

            Button(onClick = {

                callLocation.invoke(RequestType.Location)
            }

            ) {
                Text(text = "Get Location")
            }


        }

        displayDeviceStatus(DeviceState("Latitude", address.lan.toString()))
        displayDeviceStatus(DeviceState("Longitude", address.lon.toString()))
        displayDeviceStatus(deviceState = DeviceState("Country", address.country))
        displayDeviceStatus(deviceState = DeviceState("City", address.city))
        displayDeviceStatus(deviceState = DeviceState("State", address.state))
        displayDeviceStatus(deviceState = DeviceState("Time", address.time))
        displayDeviceStatus(deviceState = DeviceState("Date", address.date))







     /*   Box(
            modifier = Modifier
                .fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center
        ) {

            Button(onClick = {

                callLocation.invoke(RequestType.Bio)
            }

            ) {
                Text(text = "Bio metric")
            }


        } */


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp), contentAlignment = androidx.compose.ui.Alignment.Center
        ) {

            Button(
                onClick = {
                    if (!isClicked) {
                        isClicked = true
                        getHash()
                    }

                },
                enabled = !isClicked,

                ) {
                Text(text = "Verify User")
            }

        }

        if (pdfPath.isNotBlank()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp), contentAlignment = androidx.compose.ui.Alignment.Center
            ) {

                Button(
                    onClick = {

                        TextUtils().shareVideo2(context,pdfPath)

                    },
                    enabled = !isClicked,

                    ) {
                    Text(text = "Share QrCode Pdf")
                }

            }

        }

        Box(
            modifier = Modifier
                .fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center
        ) {

            Button(onClick = {
                // reset the user verification
                viewmodel.resetUserVerification()
                nav.invoke()
            }

            ) {
                Text(text = "QR Code Scan")
            }


        }


        if (scannedString.isNotBlank()) {
            Box() {
                Text(text = "Scanned String : $scannedString", fontSize = 12.sp, modifier = Modifier.padding(8.dp))
            }
        }



        Column(
            Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            if (blockHash.isNotBlank()) {
                Text(text = blockHash, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = blockHashTime, textAlign = TextAlign.Center)
            }
        }


        // add bold text

        Text(text = "QR Code Preview :", modifier = Modifier.padding(start = 32.dp), style = TextStyle(fontSize = 16.sp), fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)


        Box(Modifier.fillMaxWidth(), Alignment.Center) {

            if (QrCodeImage != null) {

                Image(
                    bitmap = QrCodeImage!!.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = Modifier
                        .size(180.dp)
                        .padding(8.dp)
                )
            } else {
                // create a placeholder image if the QrCodeHandler is null color light gray
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .padding(8.dp)
                        .background(Color.LightGray)
                )

            }

        }



       /* Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp), contentAlignment = androidx.compose.ui.Alignment.Center
        ) {

            Button(
                onClick = {
                    if (!isClicked) {
                        isClicked = true
                        viewmodel.scanQRCode()
                    }

                },
                enabled = !isClicked,

                ) {
                Text(text = "2. Scan")
            }

        } */



        if (dialogStatus) {
            UserVerifiedDialog(dialogStatus, userVerification) {

                if (it) {
                    TextUtils().shareVideo(shareFile!!, current)
                }

                dialog(false)


            }

        }

    }


}


@Composable
fun displayDeviceStatus(deviceState: DeviceState) {
    Row() {

        Text(text = "${deviceState.title} :", fontWeight = FontWeight.Bold, style = TextStyle(fontSize = 14.sp))
        Text(text = deviceState.description, style = TextStyle(fontSize = 12.sp))
    }
}