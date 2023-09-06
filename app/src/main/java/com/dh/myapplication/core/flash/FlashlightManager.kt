package com.dh.myapplication.core.flash

import android.content.Context
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.dh.myapplication.core.FlashlightStatus
import com.dh.myapplication.core.data.Flash
import com.dh.myapplication.core.data.FlashConfig
import com.dh.myapplication.core.data.FlashStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Date


class FlashlightManager(val context: Context,val callback: FlashEvent) {

    companion object {
        private const val TAG = "FlashlightManager"
    }

    var cameraManager: CameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
        cameraManager.getCameraCharacteristics(id)
            .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
    }
    private var flashingJob: Job? = null

    private val actualPattern = mutableListOf<FlashlightStatus>()
    var interval = 200L // Time interval in milliseconds between flashes

    var triggerTimeDate = 0L // Time in milliseconds when the flash was triggered

    var temp : FlashlightStatus? = null
    var tempIndex : Int? = null
    var tempCondition : Boolean? = null


    var coroutine : CoroutineScope? = null

    init {
//        dummyFlashlightData()
//        prototype
    //        getCameraCharacteristics()
    }


     val _flashinfo = MutableStateFlow<FlashConfig?>(null)
     val _flashStatus = MutableStateFlow<FlashStatus?>(null)
     val _processCompleted = MutableStateFlow<Boolean>(false)

     fun flashConverter() {
         Log.d(TAG, "flashConverter() called")

         if (_flashinfo.value != null) {

             // update Pattern
             actualPattern.clear()

             val flashInfo = _flashinfo.value!!
             val binary = flashInfo.binary

             // update actualPattern

             val flashList = mutableListOf<FlashlightStatus>()


             for (i in binary.indices) {
                    flashList.add(FlashlightStatus(i, binary[i]))
                }
                actualPattern.addAll(flashList)

             // set time delay time
             interval = _flashinfo.value!!.milliseconds.toLong()

         }

     }


    suspend fun startTimer() {

        coroutine?.let {

            // condition for while loop currentSecond < totalSecond
            val totalSecond = _flashStatus.value!!.totalSeconds

            // update currentSecond
            val currentSecond = _flashStatus.value!!.currentSecond

            while (it.coroutineContext.isActive && currentSecond < totalSecond && !_processCompleted.value) {
                delay(1000)
                _flashStatus.value =  _flashStatus.value!!.copy( currentSecond = _flashStatus.value!!.currentSecond + 1)
            }

        }

    }



     fun startFlashing() {

         // check if _flashinfo.value is null
         if (_flashinfo.value == null) {
             // logic error
                Log.d(TAG, "startFlashing() called but _flashinfo.value == null")
             return
         }


         _processCompleted.value = false


         // add registerTorchCallback
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

             val mHandler = Handler(Looper.getMainLooper())

             mHandler.post(Runnable {
                 Log.i(TAG, "startFlashing: ")
             })
          cameraManager.registerTorchCallback(torchCallback,  mHandler)

         }

         var index = 0


         flashingJob = CoroutineScope(Dispatchers.Default).launch {

             while (isActive && index < actualPattern.size) {
                 val flash = actualPattern[index]
                 toggleFlashlight(index,flash)
                 delay(interval)
                 index++
             }

             _processCompleted.value = true
             callback.onFlashComplete()

             // turn off the flash light if the flash is on
                if (temp != null && tempCondition != null && tempIndex != null) {
                    if (temp!!.status) {
                        if (cameraId != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                cameraManager.setTorchMode(cameraId, false)
                            }
                        }
                    }
                }

         }

    }

    fun stopFlashing() {
        flashingJob?.cancel()
    }

    fun getCameraCharacteristics() {
        Log.d(TAG, "getCameraCharacteristics() called")
        // Get the CameraManager instance.

        // Get the list of camera IDs.
        val cameraIdList = cameraManager.cameraIdList

        // Iterate through the list of camera IDs and get the CameraCharacteristics for each camera.
        for (cameraId in cameraIdList) {

            // Get the CameraCharacteristics for the camera with the specified ID.
            val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)

            // Print the CameraCharacteristics to the log.
            Log.d(TAG, "CameraCharacteristics for camera ID $cameraId:")
            for (key in cameraCharacteristics.keys) {
                Log.d(TAG, "$key: ${cameraCharacteristics[key]}")
            }
        }
    }


    var torchCallback = @RequiresApi(Build.VERSION_CODES.M)
    object : CameraManager.TorchCallback() {
        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            Log.d(TAG, "onTorchModeChanged() called with: cameraId = $cameraId, enabled = $enabled")

            if (triggerTimeDate == 0L) {
                // some logic error
                Log.d(TAG, "onTorchModeChanged() called but triggerTimeDate == 0L")
                return
            }

            // increment currentFlashCount
            _flashStatus.value = _flashStatus.value!!.copy(currentFlashCount = _flashStatus.value!!.currentFlashCount + 1)

            // pass the complete pattern to the callback
            if (tempIndex != null && tempCondition != null && temp != null) {
                val tempFlash = Flash(
                    index = tempIndex!!,
                    flashOn = tempCondition!!,
                    time_trigger = triggerTimeDate,
                    verification_flash = enabled,
                    verification_time = Date().time
                )
                callback.onFlashEntry(tempFlash)

                temp = null
                tempIndex = null
                tempCondition = null

            } else {
                // some logic error
                Log.d(TAG, "onTorchModeChanged() called but tempIndex == null || tempCondition == null || temp == null")
              }

        }
    }


    private fun toggleFlashlight(index: Int, enable: FlashlightStatus) {
        Log.d(TAG, "toggleFlashlight() called with: enable = $enable")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (cameraId != null) {
                    cameraManager.setTorchMode(cameraId, enable.status)
                    triggerTimeDate = Date().time
                    temp = FlashlightStatus(index, enable.status)
                    tempIndex = index
                    tempCondition = enable.status

                }
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        } else {

            // For lower end devices, you can use the following code to toggle the flashlight
            @Suppress("DEPRECATION")
            val camera = Camera.open()
            val parameters = camera.parameters
            if (enable.status) {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                camera.parameters = parameters
                camera.startPreview()

                // check where the flashlight is on
                triggerTimeDate = Date().time

                updateEvent(index, enable, camera)


            } else {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
                camera.parameters = parameters
                camera.stopPreview()
                camera.release()


                updateEvent(index, enable, camera)

                // check where the flashlight is off
                val flashMode = camera.parameters.flashMode
                Log.i(TAG, "toggleFlashlight: flashMode = $flashMode")

            }
        }


    }


     fun  updateEvent(index: Int, enable: FlashlightStatus, camera: Camera) {
         Log.d(TAG, "updateEvent() called")

         val parameters: Camera.Parameters = camera.parameters
         val flashModes = parameters.supportedFlashModes
         if (flashModes != null) {
             val FLASH_MODE_TORCH = flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)
             val FLASH_MODE_ON = flashModes.contains(Camera.Parameters.FLASH_MODE_ON)
             val FLASH_MODE_OFF = flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)
             val FLASH_MODE_AUTO = flashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)
             val FLASH_MODE_RED_EYE = flashModes.contains(Camera.Parameters.FLASH_MODE_RED_EYE)

             println("FLASH_MODE_TORCH supported: $FLASH_MODE_TORCH")
             println("FLASH_MODE_ON supported: $FLASH_MODE_ON")
             println("FLASH_MODE_OFF supported: $FLASH_MODE_OFF")
             println("FLASH_MODE_AUTO supported: $FLASH_MODE_AUTO")
             println("FLASH_MODE_RED_EYE supported: $FLASH_MODE_RED_EYE")

             var condition =  if (flashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                 // Flash light is on
                 Log.d(TAG, "toggleFlashlight: Flash light is on")
                 true

             } else if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                 // Flash light is off
                 Log.d(TAG, "toggleFlashlight: Flash light is off")
                 false
             } else {
                 Log.e(TAG, "toggleFlashlight: FLASH_MODE_ON & FLASH_MODE_TORCH are not supported")
                 false
             }

             val tempFlash = Flash(

                 index = index,
                 flashOn = enable.status,
                 time_trigger = triggerTimeDate,
                 verification_flash = condition,
                 verification_time = Date().time
             )

             callback.onFlashEntry(tempFlash)
         }
     }



}