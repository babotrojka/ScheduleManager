package com.example.schedulemanager

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.gridlayout.widget.GridLayout

class ScheduleMaker : AppCompatActivity() {

    private final val days : Array<String> = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    private var timesMode = false
    private lateinit var spinner: Spinner
    private lateinit var  sharedPref : SharedPreferences

    fun buttonPressed(view : View) {
        val currentDayIndex = days.indexOf(spinner.selectedItem.toString())
        val state = MainActivity.scheduledHours[(view.tag as String).toInt()][currentDayIndex]
        val pressed : Button = view as Button
        Log.i("state", state.toString())
        if(!state)
            pressed.setBackgroundColor(Color.rgb(255, 165, 0))
          else
            pressed.setBackgroundColor(Color.rgb(254, 216, 177))

        MainActivity.scheduledHours[(view.tag as String).toInt()][days.indexOf(spinner.selectedItem.toString())] = !state

    }

    private fun updateText() {
        val layout : GridLayout = findViewById(R.id.gridTextLayout)
        val timesLayout : GridLayout = findViewById(R.id.gridTimesLayout)
        for(j in 0 until MainActivity.DAYS) {
            val textView: TextView = layout.getChildAt(j) as TextView
            textView.text = days[j].substring(0, 3)
            var counter = 0
            val textViewTime = timesLayout.getChildAt(j) as TextView
            textViewTime.text = days[j].substring(0, 3)
            for (i in 0 until MainActivity.HOURS) {
                if (MainActivity.scheduledHours[i][j]) {
                    counter++
                    when {
                        i == 0 -> textViewTime.append("\n12 AM")
                        i < 12 -> textViewTime.append("\n$i AM")
                        i == 12 -> textViewTime.append("\n12 PM")
                        else -> textViewTime.append("\n" + (i % 12).toString() + " PM")
                    }
                }
            }
            if(counter > 0)
                textView.append("\n$counter")
        }
    }
    fun add(view : View) {
        updateText()
        updateGridLayout(spinner.selectedItem.toString())
        with (sharedPref.edit()) {
            putString("SCHEDULED_HOURS", ObjectSerializer.serialize(MainActivity.scheduledHours))
            commit()
        }
    }

    private fun switchSetup() {
        val buttonLayout : GridLayout = findViewById(R.id.gridButtonLayout)
        val timesLayout : GridLayout = findViewById(R.id.gridTimesLayout)
        val toggle : Switch = findViewById(R.id.timesSwitch)
        toggle.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked) {
                buttonLayout.visibility = View.INVISIBLE
                timesLayout.visibility = View.VISIBLE
            } else {
                buttonLayout.visibility = View.VISIBLE
                timesLayout.visibility = View.INVISIBLE
            }
        }

    }

    private fun spinnerSetup () {
        spinner =  findViewById(R.id.spinner)
        if(spinner != null) {
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, days)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    updateGridLayout(days[0])
                }
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    updateGridLayout(days[position])
                }

            }
        }

    }

    private fun updateGridLayout(day : String) {
        val currentDayIndex = days.indexOf(day)
        val layout : GridLayout = findViewById(R.id.gridButtonLayout)
        for(i in 0 until layout.childCount)  {
            val button = layout.getChildAt(i) as Button
            if(MainActivity.scheduledHours[(button.tag as String).toInt()][currentDayIndex])
                button.setBackgroundColor(Color.rgb(57, 255, 20))
            else
                button.setBackgroundResource(android.R.drawable.btn_default)
        }
    }

    public fun arrowSelection(view : View) {
        when (view.id) {
            R.id.right -> spinner.setSelection(spinner.selectedItemPosition + 1, true)
            R.id.left -> spinner.setSelection(spinner.selectedItemPosition - 1, true)
        }
    }

    public fun clear(view : View) {
        MainActivity.scheduledHours  = Array(MainActivity.HOURS) {BooleanArray(MainActivity.DAYS)}
        with (sharedPref.edit()) {
            putString("SCHEDULED_HOURS", ObjectSerializer.serialize(MainActivity.scheduledHours))
            commit()
        }
        refresh() //updates text and grid layout
    }

    private fun refresh() {
        updateText()  //updates text layouts
        for(day in days)
            updateGridLayout(day)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_maker)

        sharedPref = getSharedPreferences("com.example.schedulemanager", Context.MODE_PRIVATE)
        MainActivity.scheduledHours = ObjectSerializer.deserialize(sharedPref.getString("SCHEDULED_HOURS", ObjectSerializer.serialize(Array(MainActivity.HOURS) {BooleanArray(MainActivity.DAYS)})))
                                        as Array<BooleanArray>
        refresh()
        spinnerSetup()
        switchSetup()
    }
}
