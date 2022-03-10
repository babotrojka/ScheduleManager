package com.example.schedulemanager

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import java.lang.StringBuilder

//topleft 444/201
//bottomleft 444/949
//topright 1896/201
//bottomright 1896/949
class MainActivity : AppCompatActivity() {
    private final val days : Array<String> = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    private lateinit var spinner : Spinner
    private var choices : Array<String> = arrayOf("Ivan", "Domi")
    companion object{
        final const val DAYS = 7
        final const val HOURS = 24
        var scheduledHours  = Array(HOURS) {BooleanArray(DAYS)}
    }

    fun export(id : Int) : String {
        val cellWidth = 207
        val cellHalfWidth = 103
        val cellHeight = 31
        val cellHalfHeight = 15
        var result : String = ""
        val builder : StringBuilder = StringBuilder()
        builder.append("#NoEnv\nSendMode Input\nSetWorkingDir %A_ScriptDir%\n\n");
        builder.append("CoordMode, Mouse, Screen\n\n\n")
        builder.append("G::\n")
        var coordX : Int
        var coordY : Int
        val STARTWIDTH = 444
        val STARTHEIGHT = 201
        var hour : String
        for(j in 0 until DAYS)
            for(i in 0 until HOURS)
                if(scheduledHours[i][j]) {
                    coordX = STARTWIDTH + j * cellWidth + cellHalfWidth
                    coordY = STARTHEIGHT + i * cellHeight + cellHalfHeight
                    when {
                        i == 0 -> hour = "12 AM"
                        i < 12 -> hour = "$i AM"
                        i == 12 -> hour = "12 PM"
                        else -> hour =  (i % 12).toString() + " PM"
                    }
                    builder.append("Click $coordX, $coordY  ;${days.get(j).subSequence(0, 3)} $hour\n")
                    builder.append("Click 140, 618\n")
                }


        return builder.toString()
    }

    fun check(view: View) {
        val i = Intent(this, Check::class.java);
        i.putExtra("CHECK", export(0))
        startActivity(i);
    }

    fun create(view: View) {
        val i = Intent(this, ScheduleMaker::class.java);
        startActivityForResult(i, 10);
    }


    fun spinnerSetup() {
        spinner = findViewById(R.id.choice)
        if(spinner != null) {
            val adapter =
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, choices)
            spinner.adapter = adapter
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerSetup()
        val sharedPref  = getSharedPreferences("com.example.schedulemanager", Context.MODE_PRIVATE)
        scheduledHours = ObjectSerializer.deserialize(sharedPref.getString("SCHEDULED_HOURS", ObjectSerializer.serialize(Array(MainActivity.HOURS) {BooleanArray(MainActivity.DAYS)})))
                as Array<BooleanArray>

    }
}
