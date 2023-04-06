import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log

class MySensorEventListener : SensorEventListener {

    private var accelerationValues: MutableList<FloatArray> = mutableListOf()
    private var pressureValues: MutableList<Float> = mutableListOf()

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    val acceleration = FloatArray(3)
                    System.arraycopy(it.values, 0, acceleration, 0, 3)
                    accelerationValues.add(acceleration)
                    Log.d("MySensorEventListener", "Acceleration values added: $acceleration")
                }
                Sensor.TYPE_PRESSURE -> {
                    pressureValues.add(it.values[0])
                    Log.d("MySensorEventListener", "Pressure value added: ${it.values[0]}")
                }
                else -> {}
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // do nothing
    }

    fun getAccelerationValues(): List<FloatArray> {
        return accelerationValues
    }

    fun getPressureValues(): List<Float> {
        return pressureValues
    }

    fun clearValues() {
        accelerationValues.clear()
        pressureValues.clear()
        Log.d("MySensorEventListener", "Values cleared.")
    }
}
