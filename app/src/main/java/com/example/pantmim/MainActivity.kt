package com.example.pantmim

import android.content.ContentValues.TAG
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import kotlin.math.log
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var pressure: Sensor
    private lateinit var sensorEventListener: MySensorEventListener
    private var isRunning: Boolean = false
    private var accelData: MutableList<Float> = mutableListOf()
    private var pressData: MutableList<FloatArray> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)

        // センサーマネージャの取得
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // 加速度センサと気圧センサの取得
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        // イベントリスナーの生成
        sensorEventListener = MySensorEventListener()

        // 開始ボタンのクリックリスナーの設定
        startButton.setOnClickListener {
            if (isRunning) {
                // すでにセンサの取得を開始している場合は何もしない
                Toast.makeText(this, "Already Running", Toast.LENGTH_SHORT).show()
            } else {
                // センサの取得を開始する
                sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
                sensorManager.registerListener(sensorEventListener, pressure, SensorManager.SENSOR_DELAY_GAME)
                isRunning = true
                accelData.clear()
                pressData.clear()
                Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show()
                stopButton.isEnabled = true
            }
        }

        // 停止ボタンのクリックリスナーの設定
        stopButton.setOnClickListener {
            if (isRunning) {
                // センサの取得を停止する
                sensorManager.unregisterListener(sensorEventListener)
                isRunning = false
                Toast.makeText(this, "Stopped", Toast.LENGTH_SHORT).show()
                stopButton.isEnabled = false
                var isStart = true
                var AddNorm = 0.0
                var countGetNorm = 0.0
                for (data in accelData) {
                    if(!isStart){
                        if(data > 0.05){
                            isStart = true
                        }
                    }
                    if(isStart) {
                        AddNorm += data
                        countGetNorm += 1.0
                    }
                    if(isStart){
                        if(data < 0.04){
                            isStart = false
                        }
                    }
                }
                var averageNorm: Double = AddNorm/countGetNorm
                var result: Double = (averageNorm - 2.4727)/(-2.625)
                Log.d("averageNorm", "averageNorm: $averageNorm")
                Log.d("result", "result: $result")
            } else {
                // センサの取得が開始されていない場合は何もしない
                Toast.makeText(this, "Not Running", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // センサーイベントリスナーの実装クラス
    inner class MySensorEventListener : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event != null) {
                when (event.sensor.type) {
                    Sensor.TYPE_LINEAR_ACCELERATION -> {
                        val data = event.values.clone()
                        val norm = sqrt(
                            event.values[0].pow(2) +
                                    event.values[1].pow(2) +
                                    event.values[2].pow(2)
                        )
                        Log.d("ACCELEROMETER", "norm: $norm")
                        // 加速度センサーの値を保存
                        accelData.add(norm)
                    }
                    Sensor.TYPE_PRESSURE -> {
                        // 気圧センサーの値を保存
                        pressData.add(event.values.clone())
                    }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // 何もしない
        }
    }
}