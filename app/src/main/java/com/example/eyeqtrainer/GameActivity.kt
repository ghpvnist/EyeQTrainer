package com.example.eyeqtrainer

import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.iterator
import kotlinx.android.synthetic.main.activity_game.*
import kotlin.random.Random


class GameActivity : AppCompatActivity() {
    private var attempt = 1000
    private var coloredIndex = 0
    private var gameScore = 0
    private var highScore = 0
    private var color = 0
    private var colored = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            generateLevel(gameScore)
        }
        val intent: Intent = getIntent()
        highScore = intent.getIntExtra("highScore", 0)
        highscore_text.text = this.getString(R.string.high_score_main, highScore)
        score_text.text = this.getString(R.string.score_0_game, gameScore)
    }

//    val Int.dp: Int
//        get() = (this / Resources.getSystem().displayMetrics.density).toInt()
//    val Int.px: Int
//        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    private fun generateLevel(score: Int) {
        val gridLength: Int
        when (score) {
            in 0..1 -> {
                gridLength = 2
            }
            in 2..5 -> {
                gridLength = 3
            }
            in 6..15 -> {
                gridLength = 4
            }
            in 16..25 -> {
                gridLength = 5
            }
            in 26..40 -> {
                gridLength = 6
            }
            in 41..50 -> {
                gridLength = 7
            }
            in 51..60 -> {
                gridLength = 8
            }
            in 61..70 -> {
                gridLength = 9
            }
            else -> {
                gridLength = 10
            }
        }
        gridLayout.removeAllViews()
        gridLayout.columnCount = gridLength
        gridLayout.rowCount = gridLength
        val (a, b) = generateColor(score)
//        Log.d("COLOR", color.toUInt().toString(16) + "\n" + colored.toUInt().toString(16))
        color = a
        colored = b
        val gridSize = gridLength*gridLength
        coloredIndex = (0 until gridSize).random()
        var i = 0
        var r = 0
        var c = 0
        while (i < gridSize) {
            if (c == gridLength) {
                c = 0
                r++
            }
            val btn = Button(this)
            btn.id = View.generateViewId()
            if (i == coloredIndex) {
                btn.setBackgroundColor(colored)
            } else {
                btn.setBackgroundColor(color)
            }
            val param = GridLayout.LayoutParams()
            param.leftMargin = 5
            param.topMargin = 5
            param.rightMargin = 5
            param.bottomMargin = 5
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                param.width = windowManager.currentWindowMetrics.bounds.width()/gridLength - 2*param.leftMargin
                param.height = windowManager.currentWindowMetrics.bounds.width()/gridLength - 2*param.leftMargin
            } else {
                @Suppress("DEPRECATION")
                val display = windowManager.defaultDisplay
                val size = Point()
                @Suppress("DEPRECATION")
                display.getSize(size)
                param.width = size.x/gridLength - 2*param.leftMargin
                param.height = size.x/gridLength - 2*param.leftMargin
            }
//            val height = displayMetrics.heightPixels
//            val width = displayMetrics.widthPixels
            param.columnSpec = GridLayout.spec(gridLength)
            param.rowSpec = GridLayout.spec(gridLength)
            btn.layoutParams = param
            btn.setOnClickListener { view: View ->
                if ((view.background as ColorDrawable).color == colored) {
                    gameScore++
                    checkScore()
                    generateLevel(gameScore)
                } else {
                    attempt--
                    showAttempt()
                }
            }
            gridLayout.addView(btn)
            i++
            c++
        }
    }

    private fun changeColor(base: Int, difficulty: Int): Int {
        val rng = Random.nextInt(2)
        var baseColor = base
        when (base) {
            in 0x00..difficulty-> {
                baseColor += difficulty
            }
            in 0xFF-difficulty..0xFF-> {
                baseColor -= difficulty
            }
            else -> {
                baseColor = if (rng == 0) base + difficulty else base - difficulty
            }
        }
        return baseColor
    }

//    blue hardest green easiest red middle
    private fun generateColor(score: Int): Pair<Int, Int> {
        val androidColors: IntArray = getResources().getIntArray(R.array.androidcolors)
        color = androidColors[Random.nextInt(androidColors.size)]
        for (button in gridLayout) {
            button.setBackgroundColor(color)
        }
//        val rgb = Random.nextInt(3)
//        val rng = Random.nextInt(2)
        var R = (color shr 16) and 0xff
        var G = (color shr 8) and 0xff
        var B = (color) and 0xff
    val difficulty: Int
    when (score) {
            in 0..1 -> {
                difficulty = 0x16
            }
            in 2..5 -> {
                difficulty = 0x15
            }
            in 6..15 -> {
                difficulty = 0x14
            }
            in 16..25 -> {
                difficulty = 0x13
            }
            in 26..40 -> {
                difficulty = 0x12
            }
            in 41..50 -> {
                difficulty = 0x11
            }
            in 51..60 -> {
                difficulty = 0x10
            }
            in 61..70 -> {
                difficulty = 0x0F
            }
            else -> {
                difficulty = 0x0E
            }
        }
//        when (rgb) {
//            0 -> {
//                R = changeColor(R, difficulty)
//                G = changeColor(G, difficulty)
//                B = changeColor(B, difficulty)
//            }
//            1 -> {
//                R = changeColor(R, difficulty)
//                G = changeColor(G, difficulty)
//                B = changeColor(B, difficulty)
//            }
//            2 -> {
//                R = changeColor(R, difficulty)
//                G = changeColor(G, difficulty)
//                B = changeColor(B, difficulty)
//            }
//        }
        R = changeColor(R, difficulty)
        G = changeColor(G, difficulty)
        B = changeColor(B, difficulty)
//        when (R) {
//            in 0x00..difficulty-> {
//                R += difficulty
//            }
//            in 0xFF-difficulty..0xFF-> {
//                R -= difficulty
//            }
//            else -> {
//                R = if (rng == 0) R + difficulty else R - difficulty
//            }
//        }
//        when (G) {
//            in 0x00..difficulty-> {
//                G += difficulty
//            }
//            in 0xFF-difficulty..0xFF-> {
//                G -= difficulty
//            }
//            else -> {
//                G = if (rng == 0) G + difficulty else G - difficulty
//            }
//        }
//        when (B) {
//            in 0x00..difficulty-> {
//                B += difficulty
//            }
//            in 0xFF-difficulty..0xFF-> {
//                B -= difficulty
//            }
//            else -> {
//                B = if (rng == 0) B + difficulty else B - difficulty
//            }
//        }
        colored = (0xff shl 24) + (R shl 16) + (G shl 8) + B
        return Pair(color, colored)
    }

    private fun showAttempt() {
        if (attempt > 0) {
            val toast = Toast.makeText(
                    getApplicationContext(),
                    "Attempts Remaining: $attempt",
                    Toast.LENGTH_SHORT
            )
            toast.show()
            Handler(Looper.getMainLooper()).postDelayed({ toast.cancel() }, 600)
        } else {
            val toast = Toast.makeText(
                    getApplicationContext(),
                    "Game Over!",
                    Toast.LENGTH_SHORT
            )
            toast.show()
            Handler(Looper.getMainLooper()).postDelayed({ toast.cancel() }, 600)
            onBackPressed()
        }
    }

    private fun checkScore() {
        if (gameScore > highScore) {
            highScore = gameScore
            highscore_text.text = this.getString(R.string.high_score_main, highScore)
        }
        score_text.text = this.getString(R.string.score_0_game, gameScore)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("highScore", highScore)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}