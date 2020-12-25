package com.gpdev.eyeqtrainer

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
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
        fullscreen()
        setContentView(R.layout.activity_game)

        if (savedInstanceState != null) {
            attempt = savedInstanceState.getInt("attempt")
            coloredIndex = savedInstanceState.getInt("coloredIndex")
            gameScore = savedInstanceState.getInt("gameScore")
            highScore = savedInstanceState.getInt("highScore")
            color = savedInstanceState.getInt("color")
            colored = savedInstanceState.getInt("colored")
            score_text.text = this.getString(R.string.score_0_game, gameScore)
            highscore_text.text = this.getString(R.string.high_score_main, highScore)
            generateLevel(getGridLength(), color, colored, coloredIndex)
        } else {
            val intent: Intent = intent
            highScore = intent.getIntExtra("highScore", R.integer.ZERO)
            highscore_text.text = this.getString(R.string.high_score_main, highScore)
            restart()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("attempt", attempt)
        outState.putInt("coloredIndex", coloredIndex)
        outState.putInt("gameScore", gameScore)
        outState.putInt("highScore", highScore)
        outState.putInt("color", color)
        outState.putInt("colored", colored)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val gridLength = getGridLength()
        var i = 0

        for (btn in gridLayout) {
            val param = GridLayout.LayoutParams()
            val orientation = newConfig.orientation
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    btn.layoutParams.width = windowManager.currentWindowMetrics.bounds.height()/gridLength - 2*param.leftMargin
                    btn.layoutParams.height = windowManager.currentWindowMetrics.bounds.height()/gridLength - 2*param.leftMargin
                } else {
                    btn.layoutParams.width = windowManager.currentWindowMetrics.bounds.width()/gridLength - 2*param.leftMargin
                    btn.layoutParams.height = windowManager.currentWindowMetrics.bounds.width()/gridLength - 2*param.leftMargin
                }
            } else {
                @Suppress("DEPRECATION")
                val display = windowManager.defaultDisplay
                val size = Point()
                @Suppress("DEPRECATION")
                display.getSize(size)
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    btn.layoutParams.width = size.y/gridLength - 2*param.leftMargin
                    btn.layoutParams.height = size.y/gridLength - 2*param.leftMargin
                } else {
                    btn.layoutParams.width = size.x/gridLength - 2*param.leftMargin
                    btn.layoutParams.height = size.x/gridLength - 2*param.leftMargin
                }
            }
            i++
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        fullscreen()
    }

    private fun fullscreen() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        actionBar?.hide()
    }

    private fun restart() {
        attempt = resources.getInteger(R.integer.GAME_ATTEMPTS)
        gameScore = 0
        score_text.text = this.getString(R.string.score_0_game, gameScore)
        generateNewLevel()
    }

    private fun generateNewLevel() {
        val gridLength = getGridLength()
        val gridSize = getGridSize()
        val (newColor, newColored) = generateColor(gameScore)
        val newColoredIndex = (0 until gridSize).random()
        generateLevel(gridLength, newColor, newColored, newColoredIndex)
    }

    private fun generateLevel(gridLength: Int, newColor: Int, newColored: Int, newColoredIndex: Int) {

        gridLayout.removeAllViews()
        gridLayout.columnCount = gridLength
        gridLayout.rowCount = gridLength

        color = newColor
        colored = newColored
        coloredIndex = newColoredIndex
        rearrangeColor(coloredIndex)
    }

    private fun generateColor(score: Int): Pair<Int, Int> {
        val androidColors: IntArray = resources.getIntArray(R.array.androidcolors)

        color = androidColors[Random.nextInt(androidColors.size)]

        for (button in gridLayout) {
            button.setBackgroundColor(color)
        }

        val difficulty = getDifficulty(score)
        val rng = Random.nextInt(2)
        val r = changeColor((color shr 16) and 0xff, difficulty, rng)
        val g = changeColor((color shr 8) and 0xff, difficulty, rng)
        val b = changeColor((color) and 0xff, difficulty, rng)

        colored = (0xFF shl 24) + (r shl 16) + (g shl 8) + b
        return Pair(color, colored)
    }

    private fun changeColor(base: Int, difficulty: Int, rng: Int): Int {
        var baseColor = base
        when (base) {
            in 0x00..difficulty -> {
                baseColor += difficulty
            }
            in 0xFF - difficulty..0xFF -> {
                baseColor -= difficulty
            }
            else -> {
                baseColor = if (rng == 0) base + difficulty else base - difficulty
            }
        }
        return baseColor
    }

    private fun rearrangeColor(index: Int) {
        val gridLength = getGridLength()
        val gridSize = getGridSize()
        var i = 0
        while (i < gridSize) {

            val btn = Button(this)
            btn.id = View.generateViewId()

            if (i == index) {
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
            val orientation = resources.configuration.orientation
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    param.width = windowManager.currentWindowMetrics.bounds.height()/gridLength - 2*param.topMargin
                    param.height = windowManager.currentWindowMetrics.bounds.height()/gridLength - 2*param.topMargin
                } else {
                    param.width = windowManager.currentWindowMetrics.bounds.width()/gridLength - 2*param.leftMargin
                    param.height = windowManager.currentWindowMetrics.bounds.width()/gridLength - 2*param.leftMargin
                }
            } else {
                @Suppress("DEPRECATION")
                val display = windowManager.defaultDisplay
                val size = Point()
                @Suppress("DEPRECATION")
                display.getSize(size)
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    param.width = size.y/gridLength - 2*param.leftMargin
                    param.height = size.y/gridLength - 2*param.leftMargin
                } else {
                    param.width = size.x/gridLength - 2*param.leftMargin
                    param.height = size.x/gridLength - 2*param.leftMargin
                }
            }
            btn.layoutParams = param
            btn.setOnClickListener { view: View ->
                if ((view.background as ColorDrawable).color == colored) {
                    gameScore++
                    checkScore()
                    generateNewLevel()
                } else {
                    attempt--
                    showAttempt()
                }
            }
            gridLayout.addView(btn)
            i++
        }
    }

    private fun getGridLength(): Int {
        when (gameScore) {
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

    private fun getGridSize(): Int {
        val gridLength = getGridLength()
        return gridLength * gridLength
    }

    private fun getDifficulty(score: Int): Int {
        when (score) {
            in 0..1 -> {
                return 0x16
            }
            in 2..5 -> {
                return 0x15
            }
            in 6..15 -> {
                return 0x14
            }
            in 16..25 -> {
                return 0x13
            }
            in 26..40 -> {
                return 0x12
            }
            in 41..50 -> {
                return 0x11
            }
            in 51..60 -> {
                return 0x10
            }
            in 61..70 -> {
                return 0x0F
            }
            else -> {
                return 0x0E
            }
        }
    }

    private fun showAttempt() {
        if (attempt > 0) {
            makeToast("Attempts Remaining: $attempt", 600)
        } else {
            gameOverAlert()
        }
    }

    private fun makeToast(text: String, duration: Long) {
        val toast = Toast.makeText(
            applicationContext,
            text,
            Toast.LENGTH_SHORT
        )
        toast.show()
        Handler(Looper.getMainLooper()).postDelayed({ toast.cancel() }, duration)
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