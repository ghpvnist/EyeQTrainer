package com.example.eyeqtrainer

import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.iterator
import kotlinx.android.synthetic.main.activity_game.*
import kotlin.random.Random


class GameActivity : AppCompatActivity() {
    private var attempt = R.integer.GAME_ATTEMPTS
    private var coloredIndex = 0
    private var gameScore = 0
    private var highScore = 0
    private var color = 0
    private var colored = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        restart()
        val intent: Intent = intent
        highScore = intent.getIntExtra("highScore", R.integer.ZERO)
        highscore_text.text = this.getString(R.string.high_score_main, highScore)
    }

    private fun restart() {
        attempt = 3
        gameScore = 0
        score_text.text = this.getString(R.string.score_0_game, gameScore)
        generateLevel(gameScore)
    }

    private fun generateLevel(score: Int) {
        val gridLength = getGridLength(score)

        gridLayout.removeAllViews()
        gridLayout.columnCount = gridLength
        gridLayout.rowCount = gridLength

        val (a, b) = generateColor(score)
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
            param.columnSpec = GridLayout.spec(gridLength)
            param.rowSpec = GridLayout.spec(gridLength)
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

    private fun generateColor(score: Int): Pair<Int, Int> {
        val androidColors: IntArray = resources.getIntArray(R.array.androidcolors)

        color = androidColors[Random.nextInt(androidColors.size)]

        for (button in gridLayout) {
            button.setBackgroundColor(color)
        }

        val difficulty = getDifficulty(score)
        val r = changeColor((color shr 16) and 0xff, difficulty)
        val g = changeColor((color shr 8) and 0xff, difficulty)
        val b = changeColor((color) and 0xff, difficulty)

        colored = (0xFF-difficulty shl 24) + (r shl 16) + (g shl 8) + b

        return Pair(color, colored)
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

    private fun getGridLength(score: Int): Int {
        when (score) {
            in 0..1 -> {
                return 2
            }
            in 2..5 -> {
                return 3
            }
            in 6..15 -> {
                return 4
            }
            in 16..25 -> {
                return 5
            }
            in 26..40 -> {
                return 6
            }
            in 41..50 -> {
                return 7
            }
            in 51..60 -> {
                return 8
            }
            in 61..70 -> {
                return 9
            }
            else -> {
                return 10
            }
        }
    }

    private fun getDifficulty(score: Int): Int {
        when (score) {
            in 0..1 -> {
                return 0x12
            }
            in 2..5 -> {
                return 0x11
            }
            in 6..15 -> {
                return 0x10
            }
            in 16..25 -> {
                return 0x0F
            }
            in 26..40 -> {
                return 0x0E
            }
            in 41..50 -> {
                return 0x0D
            }
            in 51..60 -> {
                return 0x0C
            }
            in 61..70 -> {
                return 0x0B
            }
            else -> {
                return 0x0A
            }
        }
    }

    private fun showAttempt() {
        if (attempt > 0) {
            val toast = Toast.makeText(
                    applicationContext,
                    "Attempts Remaining: $attempt",
                    Toast.LENGTH_SHORT
            )
            toast.show()
            Handler(Looper.getMainLooper()).postDelayed({ toast.cancel() }, R.integer.DELAY_600.toLong())
        } else {
            gameOverAlert()
        }
    }

    private fun gameOverAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Game Over!")
        builder.setMessage("High Score: $highScore\nCurrent Score: $gameScore")
        builder.setPositiveButton(R.string.new_game) { dialog, which ->
            restart()
        }
        builder.setNegativeButton(R.string.back_to_menu) { dialog, which ->
            onBackPressed()
        }
        builder.show()
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