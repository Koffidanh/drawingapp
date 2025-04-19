import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import kotlin.math.abs

class ShakeDetector(private val context: Context, private val onShake: () -> Unit) : SensorEventListener {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var lastUpdate: Long = 0
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var lastZ: Float = 0f

    private val SHAKE_THRESHOLD = 8 // sensitivity for how much to shake the device in the virtual sensors
    private val ROTATION_THRESHOLD = 2f // low end to ensure rotation threshold is not hit
    private val SHAKE_DURATION_MS = 400 // .4 seconds of active movement to ensure shake is intentional

    private var cumulativeShakeIntensity = 0f //the shaking that has occured
    private var shakeStartTime: Long = 0 //if a shake is starting it wil get timed
    //bool for if the shaking is occuring
    private var isShaking = false

    private val handler = Handler(Looper.getMainLooper())

    // Runnable that will reset the shake detection after a certain time
    private val resetShakeRunnable = Runnable {
        resetShake()
    }

    fun start() {
        val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        handler.removeCallbacks(resetShakeRunnable)
    }

    //overriding function fo on sensor changed to check for shaking
    override fun onSensorChanged(event: SensorEvent) {
        val curTime = System.currentTimeMillis()

        if (curTime - lastUpdate > 100) {  // Process every 100ms
            val deltaTime = curTime - lastUpdate
            lastUpdate = curTime

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Calculate the change in acceleration
            val deltaX = x - lastX
            val deltaY = y - lastY
            val deltaZ = z - lastZ

            // Focus on X and Y changes, filtering out rotations
            val shakeIntensity = Math.sqrt((deltaX * deltaX + deltaY * deltaY).toDouble()).toFloat()

            if (abs(deltaZ) < ROTATION_THRESHOLD && shakeIntensity > SHAKE_THRESHOLD) {
                // If shaking starts, record the time and accumulate intensity
                if (!isShaking) {
                    shakeStartTime = curTime
                    isShaking = true
                }

                cumulativeShakeIntensity += shakeIntensity

                // Check if the shake has lasted long enough
                if (curTime - shakeStartTime >= SHAKE_DURATION_MS) {
                    // Trigger the shake action
                    onShake()
                    resetShake() // Reset the shake data after triggering the shake
                } else {
                    // If shaking continues, keep resetting the reset timer
                    handler.removeCallbacks(resetShakeRunnable)
                    handler.postDelayed(resetShakeRunnable, SHAKE_DURATION_MS.toLong())
                }

            } else {
                // If not enough shake intensity, reset
                resetShake()
            }

            // Update last known values
            lastX = x
            lastY = y
            lastZ = z
        }
    }

    //unneeded override function
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    //function to reset the shake
    private fun resetShake() {
        cumulativeShakeIntensity = 0f
        isShaking = false
        shakeStartTime = 0
        handler.removeCallbacks(resetShakeRunnable)
    }
}
