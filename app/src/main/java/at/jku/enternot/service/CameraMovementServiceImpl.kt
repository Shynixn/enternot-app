package at.jku.enternot.service

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import at.jku.enternot.R
import at.jku.enternot.contract.CameraMovementService

class CameraMovementServiceImpl(private val applicationContext: Application) : CameraMovementService,
        SensorEventListener {

    private val sensorManager = applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
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
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            sensorManager.unregisterListener(this, accelerometer)
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
    override fun calibrateSensor() {
        sensorManager.registerListener(CalibrationListener {
            sensorManager.unregisterListener(this, accelerometer)
            val average = Triple(
                    it.map { it.first }.average().toFloat(),
                    it.map { it.second }.average().toFloat(),
                    it.map { it.third }.average().toFloat()
            )
            saveCalibrationValues(average)
        }, accelerometer, SensorManager.SENSOR_DELAY_FASTEST)
    }

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

    class CalibrationListener(val callback: (data: List<Triple<Float, Float, Float>>) -> Unit)
        : SensorEventListener {
        private val samples = 25
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
                callback.invoke(calibrationData)
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
            this.axisData.postValue(Triple(event.values[0], event.values[1], event.values[2]))
        }
    }
}