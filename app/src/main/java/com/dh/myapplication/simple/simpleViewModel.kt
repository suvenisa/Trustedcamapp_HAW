package com.dh.myapplication.simple

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dh.myapplication.R
import com.dh.myapplication.core.BinaryConverter
import com.dh.myapplication.core.data.DataRepository
import com.dh.myapplication.core.data.DeviceState
import com.dh.myapplication.core.data.Flash
import com.dh.myapplication.core.data.FlashConfig
import com.dh.myapplication.core.data.UserAddress
import com.dh.myapplication.core.flash.FlashEvent
import com.dh.myapplication.core.network.Blockchain
import com.dh.myapplication.core.utils.*
import com.dh.myapplication.core.utils.RootDetection.isDeviceRooted
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class simpleViewModel @Inject constructor(
    @ApplicationContext var context: Context,
    private val dataRepository: DataRepository
) : ViewModel() {

    companion object {
        private const val TAG = "simpleViewModel"
    }

    private val _file = MutableStateFlow<File?>(null)
    val shareFile: StateFlow<File?> = _file.asStateFlow()

    private val _userVerification = MutableStateFlow<Boolean>(false)
    val userVerification: StateFlow<Boolean> = _userVerification.asStateFlow()

    private val _blockHash = MutableStateFlow<String>("")
    private val _scannedString = MutableStateFlow<String>("")
    private val _blockHashStart = MutableStateFlow<Long>(0L)
    private val _blockHashTime = MutableStateFlow<String>("")

    var blockHash: StateFlow<String> = _blockHash.asStateFlow()
    var scannedString: StateFlow<String> = _scannedString.asStateFlow()
    var blockHashTime: StateFlow<String> = _blockHashTime.asStateFlow()

    val address: MutableStateFlow<UserAddress>
        get() = location._address


    val location = Location(context)

    private var callback = object : FlashEvent {

        override fun onFlashEntry(flash: Flash) {
            Log.d(TAG, "onFlashCompleted() called with: flash = $flash")
            // insert flash to database
            insertFlash(flash)

        }

        override fun onFlashComplete() {

            getFlashes()

        }

    }

     fun resetUserVerification() {
         Log.d(TAG, "resetUserVerification() called")
         _userVerification.value = false
         _file.value = null
     }

    fun initCheck(): List<DeviceState> {
        Log.d(TAG, "initCheck() called")

        val list = mutableListOf<DeviceState>()

        // check for root
        val description = if (isEmulator(context)) {
            context.getString(R.string.emulator_used_text)
        } else {
            context.getString(R.string.no_emulator_used_text)

        }
        list.add(DeviceState("Emulator", description))

        // Root detection check
        val isDeviceRooted = if (isDeviceRooted()) {
            context.getString(R.string.device_rooted_text)
        } else {
            context.getString(R.string.device_not_rooted_text)
        }

        list.add(DeviceState("Rooted", isDeviceRooted))

        // Check for Frida detection
        val isFridaRunning = FridaDetection.isFridaServerRunning(context)
        var isFridaRunningDesciptiopn = if (isFridaRunning) {

            context.getString(R.string.fridaserver_detected_text)
        } else {
            context.getString(R.string.fridaserver_not_detected_text)
        }

        list.add(DeviceState("Frida Running ", isFridaRunningDesciptiopn))


        val isFridaDetected = FridaDetection.isFridaDetected(context)
        var isFridaDetectedDescription = if (isFridaDetected) {

            context.getString(R.string.frida_detected_text)
        } else {
            context.getString(R.string.frida_not_detected_text)
        }

        list.add(DeviceState("Frida", isFridaDetectedDescription))


        val isDebuggerConnected = FridaDetection.isDebuggerConnected()
        val isDebuggerConnectedDescription = if (isDebuggerConnected) {

            "Debugger Connected!"
        } else {
            "Debugger Not Connected!"
        }

        list.add(DeviceState("Debugger", isDebuggerConnectedDescription))




        val isMagiskDetected = MagiskDetection.isMagiskDetected(context)
        val isMagiskDetectedDescription = if (isMagiskDetected) {

            context.getString(R.string.magisk_detected_text)
        } else {
            context.getString(R.string.magisk_not_detected_text)
        }

        list.add(DeviceState("Magisk", isMagiskDetectedDescription))

        return list
    }



    fun locationProvider(fusedLocationClient: FusedLocationProviderClient) {
        Log.d(TAG, "locationProvider() called with: fusedLocationClient = $fusedLocationClient")
        viewModelScope.launch {
            location.getLocationMain(fusedLocationClient)
        }
    }

    fun getFlashes() {
        viewModelScope.launch(Dispatchers.IO) {

            val list: List<Flash> = dataRepository.getAllFlashes()

            // verification of flashes
            // 1. all flashes should be equal to verification flashes

            val allFlashOnEqualVerificationFlash = list.all { it.flashOn == it.verification_flash }
            _userVerification.value = allFlashOnEqualVerificationFlash


            Log.i(TAG, "getFlashes: allFlashOnEqualVerificationFlash ${allFlashOnEqualVerificationFlash}")

            val timestamp = SimpleDateFormat("yy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

            val stringBuilder = StringBuilder()
            list.sortedBy { it.id }.forEach {
                stringBuilder.append("${it.id},${it.index},${it.flashOn},${timestamp.format(it.time_trigger)},${it.verification_flash},${timestamp.format(it.verification_time)} \n")
            }


            // share this file

            val cacheFile: File? = TextUtils().createFileInCache(context, "flash_logs.txt", stringBuilder.toString())

            _file.value = cacheFile

        }
    }


    var flashlight = SimpleifiedFlashlightManager(context, callback)


    fun deleteAllFlashes() {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.clearAllFlashes()
        }
    }

    fun setHash(hash: String) {
        Log.i(TAG, "setHash: $hash")

        viewModelScope.launch(Dispatchers.IO) {

            val binaryConverter = BinaryConverter()
            // Binary Converter
            val binaryValue: String = binaryConverter.stringToBinary(hash)

            val binary: List<Boolean> = binaryValue.map { char ->
                char == '1'
            }


            val flashConfig = FlashConfig(hash = hash, binary = binary, milliseconds = 50)

            setFlashInfo(flashConfig)

            delay(100)

            flashlight.startFlashing()


        }

    }

    fun scan(Id: String) {
        Log.d(TAG, "scan() called")
        viewModelScope.launch {

            _scannedString.value = Id

        }
    }
    fun scanQRCode() {
        Log.d(TAG, "scanQRCode() called")
        viewModelScope.launch(Dispatchers.IO) {

            val hash = _blockHash.value
            if (hash.isNotBlank()) {

                setHash(hash)
            }
        }

    }

    fun setFlashInfo(flashConfig: FlashConfig) {
        Log.i(TAG, "setFlashInfo: ")

        // step 1 :

        flashlight._flashinfo.value = flashConfig

        // Step 2 :  update FlashStatus

        flashlight.flashConverter()

    }

    fun insertFlash(flash: Flash) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.insertFlash(flash)
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun printBlockchainBalance() {
        Log.d(TAG, "printBlockchainBalance() called")
        viewModelScope.launch(Dispatchers.IO) {

            // clear all flashes
            _blockHashStart.value = System.currentTimeMillis()
            _blockHash.value = ""
            deleteAllFlashes()

            try {
                val address = "0xcAF6dc093e5690dEb0f8A084179841FC1934E57E"
                //val mnemonicPhrase = "kitten expose uncle blue flash recipe decade nominee thought best robot blue"
                val toAddress = "0xcAF6dc093e5690dEb0f8A084179841FC1934E57E"
                // Derive the private key from the mnemonic phrase
                // val wallet = WalletUtils.loadBip39Credentials("", mnemonicPhrase)
                val privateKey = "13bc764a42f12d25d245b16433ab9e6bf33e222750ebff4a432fa0a98f8e7b58"

                val blockchain = Blockchain()
                val balance = blockchain.getBalance(address)
                val fingerprint = getFingerprint(context)
                //val fingerprint = "device_kfingerprint"

                Log.i(TAG, "printBlockchainBalance: Blockchain Balance of $address: $balance")

                // Transfer the fingerprint as a message

                blockchain.transfer2(address, toAddress, privateKey, fingerprint) {

                    if (it != null) {
                        Log.i(TAG, "printBlockchainBalance: optimized Code  ${it.blockHash}")


                        // calculate time difference in seconds
                        val diff = System.currentTimeMillis() - _blockHashStart.value
                        val seconds = diff / 1000

                        _blockHash.value = it.blockHash
                        _blockHashTime.value = "$seconds seconds"
                    }
                }


            } catch (e: Exception) {
                // Handle the exception here
                e.printStackTrace()
                // log error
                Log.i(TAG, "printBlockchainBalance: ${e.message}")

            }
        }
    }


}