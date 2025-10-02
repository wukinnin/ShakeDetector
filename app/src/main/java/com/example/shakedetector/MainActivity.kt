package com.example.shakedetector

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var shakeTimestamp: Long = 0
    private var shakeDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        shakeDialog?.dismiss()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this app
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val shakeThreshold = 12.0f

            if (acceleration > shakeThreshold) {
                val now = System.currentTimeMillis()
                if (now - shakeTimestamp > 1000) {
                    shakeTimestamp = now
                    if (shakeDialog?.isShowing != true) {
                        showShakeDialog()
                    }
                }
            }
        }
    }

    private fun showShakeDialog() {
        val dialogView = layoutInflater.inflate(R.layout.custom_toast, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val dialog = builder.create()
        shakeDialog = dialog

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val closeButton: Button = dialogView.findViewById(R.id.toast_button)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
