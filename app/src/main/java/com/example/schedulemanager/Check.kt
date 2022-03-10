package com.example.schedulemanager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class Check : AppCompatActivity() {

    private var text : String? = null


    private class GetLink : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg input: String?): String {
            val url = URL(input[0])
            val connection = url.openConnection() as HttpURLConnection
            lateinit var data : String
            try {
                connection.setChunkedStreamingMode(0);
                connection.requestMethod = "POST"
                connection.doOutput = true;
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Accept", "application/text");

                var dataPOST = URLEncoder.encode("text", "UTF-8") + "=" + URLEncoder.encode(input[1], "UTF-8")
                dataPOST += "&" + URLEncoder.encode("expire", "UTF-8") + "=" + URLEncoder.encode(1440.toString(), "UTF-8")

                val out = BufferedOutputStream(connection.outputStream);
                out.write(dataPOST.toByteArray())
                out.flush()

                data = connection.inputStream.bufferedReader().readText()
            } finally {
                connection.disconnect()
            }
            return data
        }

    }
    fun copyClipboard(view : View) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("text", text))
        Toast.makeText(applicationContext, "Text copied!", Toast.LENGTH_SHORT).show()
    }

    fun copyLink(view: View) {
        val url = "https://pastecode.xyz/api/create"
        val link = GetLink().execute(url, text).get()
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("text", link))
        Toast.makeText(applicationContext, "Link copied!", Toast.LENGTH_SHORT).show()

        val whatsappUrl = "https://api.whatsapp.com/send?phone=+385914459027&text=$link"
        try {
            val pm: PackageManager = applicationContext.packageManager
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(whatsappUrl)
            startActivity(i)
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(applicationContext, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)

        text = intent.getStringExtra("CHECK")
        if(text == null)
            Toast.makeText(applicationContext, "Go back, null error!", Toast.LENGTH_SHORT).show()
        val textView = findViewById<TextView>(R.id.textViewCheck)
        textView.movementMethod = ScrollingMovementMethod()
        textView.text = text


    }
}
