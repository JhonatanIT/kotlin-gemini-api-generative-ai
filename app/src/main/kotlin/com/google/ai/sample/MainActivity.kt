/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ai.sample

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.ai.sample.feature.chat.ChatRoute
import com.google.ai.sample.feature.multimodal.PhotoReasoningRoute
import com.google.ai.sample.feature.structuredoutput.StructuredOutputRoute
import com.google.ai.sample.feature.text.SummarizeRoute
import com.google.ai.sample.ui.theme.GenerativeAISample

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request permission for activity recognition (STEP_COUNTER)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACTIVITY_RECOGNITION,
            ),
            0
        )

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        setContent {
            GenerativeAISample {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "menu") {
                        composable("menu") {
                            MenuScreen(onItemClicked = { routeId ->
                                navController.navigate(routeId)
                            })
                        }
                        composable("summarize") {
                            SummarizeRoute()
                        }
                        composable("structured_output") {
                            StructuredOutputRoute()
                        }
                        composable("photo_reasoning") {
                            PhotoReasoningRoute()
                        }
                        composable("chat") {
                            ChatRoute()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_LIGHT)

        //Motorola have wake-up sensors and Samsung don't
        for (sensor in deviceSensors) {
            println(
                "Sensor: ${sensor.name} - " +
                        "${sensor.type} - " +
                        "${sensor.id} - " +
                        "${sensor.minDelay} - " +   //0: not streaming
                        "${sensor.reportingMode} - " + //0: continuous, 1: on change, 2: one shot
                        "${sensor.isWakeUpSensor} - " + //true: wake up SoC (save battery)
                        "${sensor.isDynamicSensor} - " + //Can be added or removed at runtime
                        "${sensor.isAdditionalInfoSupported} - "
            )

            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        // Many sensors return 3 values, one for each axis.
        println("${event.sensor.name}:")
        event.values.forEach {
            print("  $it")
        }
        println()
    }

    override fun onAccuracyChanged(sensor: Sensor, p1: Int) {
        println( "Sensor accuracy changed: ${sensor.name} - $p1")
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}
