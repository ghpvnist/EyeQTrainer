package com.gpdev.eyeqtrainer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {

    private var highScore: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val filename = getString(R.string.highscore_data)
        try {
            val fileInputStream: FileInputStream? = openFileInput(filename)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder: StringBuilder = StringBuilder()
            var text: String? = null
            while ({ text = bufferedReader.readLine(); text }() != null) {
                stringBuilder.append(text)
            }
            highScore = stringBuilder.toString().toInt()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            score_text.text = getString(R.string.high_score_main, highScore)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        actionBar?.hide()
    }

    fun startButton(v: View) {
        if (v.id == R.id.start_button) {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("highScore", this.highScore)
            startActivityForResult(intent, 0)
        }
    }

    fun resetScoreButton(v: View) {
        if (v.id == R.id.clear_score_button) {
            writeData("0")
            highScore = 0
            score_text.text = getString(R.string.high_score_main, 0)
        }
    }

    private fun writeData(data: String) {
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = openFileOutput("highscore_data", Context.MODE_PRIVATE)
            fileOutputStream.write(data.toByteArray())
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            highScore = data!!.getIntExtra("highScore", 0)
        }
        score_text.text = getString(R.string.high_score_main, highScore)
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = openFileOutput("highscore_data", Context.MODE_PRIVATE)
            fileOutputStream.write(highScore.toString().toByteArray())
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            writeData(highScore.toString())
        }
    }
}