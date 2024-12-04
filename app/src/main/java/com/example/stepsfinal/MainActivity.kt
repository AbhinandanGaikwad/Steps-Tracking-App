package com.example.stepsfinal

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity


class MainActivity : ComponentActivity(),SensorEventListener {

    private lateinit var stepCountTextView: TextView
    private lateinit var goalSetButton: Button
    private lateinit var resetButton: Button
    private lateinit var sensorManager: SensorManager
    private lateinit var editText: EditText
    private var stepCounterSensor: Sensor? = null
    private var steps = 0
    private var previousSteps = 0
    private lateinit var progressBar: ProgressBar
    private var running = false

    private lateinit var stepCountTargetTextView: TextView
    private var stepCountTarget = 100

    override fun onStop() {
        super.onStop()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        running = true
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)

        stepCountTextView = findViewById(R.id.stepCountTextView)

        resetButton = findViewById(R.id.resetButton)
        progressBar = findViewById(R.id.progressBar)
        stepCountTargetTextView = findViewById(R.id.stepCountTargetTextView)
        goalSetButton = findViewById(R.id.goalSetButton)
        editText = findViewById(R.id.editGoal)


        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        progressBar.max = stepCountTarget

        stepCountTargetTextView.text = getString(R.string.step_goal_format, stepCountTarget)

        changeGoal()
        loadData()
        resetSteps()



        if(stepCounterSensor == null){
            stepCountTextView.text = getString(R.string.step_counter_not_available)
        }



    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(running){
            steps = event!!.values[0].toInt()
            val currentSteps = steps - previousSteps
            stepCountTextView.text = getString(R.string.steps,currentSteps)
            progressBar.progress = currentSteps

            if(currentSteps >= stepCountTarget){
                stepCountTargetTextView.text = getString(R.string.step_goal_completion)

            }
        }



    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }



    private fun resetSteps() {
        resetButton.setOnClickListener{

            previousSteps = steps
            stepCountTextView.text = getString(R.string.steps, 0)
            saveData()
            progressBar.progress = 0

            stepCountTargetTextView.text = getString(R.string.step_goal_format,stepCountTarget)

        }

    }

    private fun changeGoal(){
        goalSetButton.setOnClickListener{
            editText.isEnabled = true
            editText.visibility = View.VISIBLE


            goalSetButton.setOnClickListener{
                editText.isEnabled = false
                editText.visibility = View.GONE
                val userInput = editText.text.toString()
                if(userInput.isNotEmpty()){
                    stepCountTarget = userInput.toInt()
                    progressBar.progress = 0
                    stepCountTargetTextView.text = getString(R.string.step_goal_format,stepCountTarget)
                }
                else{
                    Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show()
                }

                changeGoal()

            }


        }
    }

    private fun saveData() {
        val sharedPreferences = getSharedPreferences("myPrefs",Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("key1",previousSteps)
        editor.apply()
    }

    private fun loadData(){
        val sharedPreferences = getSharedPreferences("myPrefs",Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getInt("key1",0)
        Log.d("MainActivity","$savedNumber")
        previousSteps = savedNumber
    }


}



// Variables and Functions of Pause Button
/*
private lateinit var pauseButton: Button
private var isPaused = false

pauseButton = findViewById(R.id.pauseButton)

fun onPauseButtonClicked(view: View){

        if(isPaused){
            isPaused = false
            pauseButton.text= getString(R.string.pause)
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
        else{
            isPaused = true
            pauseButton.text= getString(R.string.resume)
            sensorManager.unregisterListener(this)

        }

    }


<Button
    android:id="@+id/pauseButton"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="left"
    android:padding="10dp"
    android:text="Pause"
    android:onClick="onPauseButtonClicked"/>

 */
