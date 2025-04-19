package com.example.makart.utils

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose

class SensorUtils {
    fun getGravityData(
        gravitySensor: Sensor?,
        sensorManager: SensorManager?
    ): Flow<FloatArray> = callbackFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor == gravitySensor) {
                    trySend(event.values)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }
        sensorManager?.registerListener(listener, gravitySensor, SensorManager.SENSOR_DELAY_GAME)

        awaitClose {
            sensorManager?.unregisterListener(listener)
        }
    }
}