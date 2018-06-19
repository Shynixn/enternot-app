package at.jku.enternot.service

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import at.jku.enternot.R
import at.jku.enternot.contract.CameraMovementService
import at.jku.enternot.contract.ConnectionService
import org.jetbrains.anko.doAsync
import kotlin.math.roundToInt

class CameraMovementServiceImpl(private val applicationContext: Application,
                                private val connectionService: ConnectionService)
    : CameraMovementService, SensorEventListener {

    private val logTag = this::class.java.simpleName
    private val sensorManager = applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val axisData = MutableLiveData<Triple<Float, Float, Float>>()

    private lateinit var calibrationValues: Triple<Float, Float, Float>

    init {
        this.loadCalibrationValues()
    }

    /**
     * Enables or disables the camera movement.
     * @param b True if the camera movement should be enabled otherwise false.
     */
    override fun enableCameraMovement(b: Boolean) {
        if(b) {
            sensorManager.registerListener(this, gyroscope,
                    SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            sensorManager.unregisterListener(this, gyroscope)
        }
    }

    /**
     * Gets the axis moving data.
     */
    override fun getAxisData(): MutableLiveData<Triple<Float, Float, Float>> {
        return axisData
    }

    /**
     * Reads a set of axis data, calculate the average value for all three axis
     * and saves these values to the local storage.
     */
    override fun calibrateSensor(f: () -> Unit) {
        sensorManager.registerListener(CalibrationListener { listener, data ->
            sensorManager.unregisterListener(listener, gyroscope)
            val average = Triple(
                    data.map { it.first }.average().toFloat(),
                    data.map { it.second }.average().toFloat(),
                    data.map { it.third }.average().toFloat()
            )
            saveCalibrationValues(average)
            f.invoke()
        }, gyroscope, SensorManager.SENSOR_DELAY_FASTEST)
    }

    /**
     * Sends axis data to the pi.
     */
    override fun sendAxisData(data: Triple<Float, Float, Float>) {
        doAsync {
            val x: Int
            val y: Int
            if(isInPortrait()) {
                x = data.first.roundToInt()
                y = data.second.roundToInt()
            } else {
                x = data.second.roundToInt()
                y = data.first.roundToInt()
            }
            if(x > 0 || x < 0 || y > 0 || y < 0) {
                val responseCode = connectionService.post("/camera/position",
                        applicationContext, CameraPosition((x * 36).toFloat(), (y * 36).toFloat()))
                Log.d(logTag, "Response code: $responseCode")
            }
        }
    }

    data class CameraPosition(val x_angle: Float, val y_angle: Float)

    private fun isInPortrait(): Boolean =
            applicationContext.resources.configuration.orientation ==
                    android.content.res.Configuration.ORIENTATION_PORTRAIT

    /**
     * Saves the calibration values to the shared preferences.
     */
    private fun saveCalibrationValues(average: Triple<Float, Float, Float>) {
        val sharedPref = applicationContext.getSharedPreferences(
                applicationContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        this.calibrationValues = average
        with(sharedPref.edit()) {
            putFloat("averageX", average.first)
            putFloat("averageY", average.second)
            putFloat("averageZ", average.third)
            apply()
        }
    }

    /**
     * Loads the calibration values from the shared preferences.
     */
    private fun loadCalibrationValues() {
        val sharedPref = applicationContext.getSharedPreferences(
                applicationContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )

        val x = sharedPref.getFloat("averageX", 0F)
        val y = sharedPref.getFloat("averageY", 0F)
        val z = sharedPref.getFloat("averageZ", 0F)
        this.calibrationValues = Triple(x, y, z)
    }

    class CalibrationListener(val callback: (listener: CalibrationListener,
                                             data: List<Triple<Float, Float, Float>>) -> Unit)
        : SensorEventListener {
        private val samples = 250
        private val calibrationData: MutableList<Triple<Float, Float, Float>> = mutableListOf()

        /**
         * Called when the accuracy of the registered sensor has changed.  Unlike
         * onSensorChanged(), this is only called when this accuracy value changes.
         *
         *
         * See the SENSOR_STATUS_* constants in
         * [SensorManager][android.hardware.SensorManager] for details.
         *
         * @param accuracy The new accuracy of this sensor, one of
         * `SensorManager.SENSOR_STATUS_*`
         */
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Again we don't need this. Why there is no abstract adapter so we can only implement
            // the onSensorChange method.
        }

        /**
         * Called when there is a new sensor event.  Note that "on changed"
         * is somewhat of a misnomer, as this will also be called if we have a
         * new reading from a sensor with the exact same sensor values (but a
         * newer timestamp).
         *
         *
         * See [SensorManager][android.hardware.SensorManager]
         * for details on possible sensor types.
         *
         * See also [SensorEvent][android.hardware.SensorEvent].
         *
         *
         * **NOTE:** The application doesn't own the
         * [event][android.hardware.SensorEvent]
         * object passed as a parameter and therefore cannot hold on to it.
         * The object may be part of an internal pool and may be reused by
         * the framework.
         *
         * @param event the [SensorEvent][android.hardware.SensorEvent].
         */
        override fun onSensorChanged(event: SensorEvent?) {
            if (calibrationData.size <= samples && event != null) {
                calibrationData.add(Triple(event.values[0], event.values[1], event.values[2]))
            } else if (calibrationData.size >= samples) {
                callback.invoke(this, calibrationData)
            }
        }
    }

    /**
     * Called when the accuracy of the registered sensor has changed.  Unlike
     * onSensorChanged(), this is only called when this accuracy value changes.
     *
     *
     * See the SENSOR_STATUS_* constants in
     * [SensorManager][android.hardware.SensorManager] for details.
     *
     * @param accuracy The new accuracy of this sensor, one of
     * `SensorManager.SENSOR_STATUS_*`
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // We don't need this method. :D
    }

    /**
     * Called when there is a new sensor event.  Note that "on changed"
     * is somewhat of a misnomer, as this will also be called if we have a
     * new reading from a sensor with the exact same sensor values (but a
     * newer timestamp).
     *
     *
     * See [SensorManager][android.hardware.SensorManager]
     * for details on possible sensor types.
     *
     * See also [SensorEvent][android.hardware.SensorEvent].
     *
     *
     * **NOTE:** The application doesn't own the
     * [event][android.hardware.SensorEvent]
     * object passed as a parameter and therefore cannot hold on to it.
     * The object may be part of an internal pool and may be reused by
     * the framework.
     *
     * @param event the [SensorEvent][android.hardware.SensorEvent].
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if(event != null) {
            this.axisData.postValue(Triple(
                    event.values[0] - calibrationValues.first,
                    event.values[1] - calibrationValues.second,
                    event.values[2] - calibrationValues.third
            ))
        }
    }
}