package com.bignerdranch.android.geoquiz

import android.app.Activity
import android.content.Intent
import android.graphics.Shader
import android.graphics.RenderEffect
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bignerdranch.android.geoquiz.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val quizViewModel: QuizViewModel by viewModels()

    private val answers = arrayOfNulls<Int>(questionBank.size)

    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle result
        if (result.resultCode == Activity.RESULT_OK) {
            val cheated = result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            if (cheated) {
                quizViewModel.cheatedOnCurrentQuestion(cheated)
                quizViewModel.cheatTokens -= 1
                val cheatTokenString = "Cheat Tokens: ${quizViewModel.cheatTokens}"
                binding.cheatTokensTextView.text = cheatTokenString
                if (quizViewModel.cheatTokens == 0) {
                    binding.cheatButton.isEnabled = false
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        enableEdgeToEdge()
//        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
        }

        binding.falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
        }

        binding.nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        binding.cheatButton.setOnClickListener {
            // Start CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)
        }

        updateQuestion()
        val cheatTokenString = "Cheat Tokens: ${quizViewModel.cheatTokens}"
        binding.cheatTokensTextView.text = cheatTokenString

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurCheatButton()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)
        updateButtonState()
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = when {
            quizViewModel.isCheaterOnCurrentQuestion() -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        answers[currentIndex] = if (userAnswer == correctAnswer) {100} else {0}
        updateButtonState()

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
        gradeAnswers()
    }

    private fun updateButtonState() {
        binding.trueButton.isEnabled = (answers[currentIndex] == null)
        binding.falseButton.isEnabled = (answers[currentIndex] == null)
    }

    private fun gradeAnswers() {
        var result: Int = 0
        if (questionBank.size == 0) return
        for (answer in answers){
            if (answer == null) return
            result += answer
        }
        result /= questionBank.size
        Toast.makeText(this, "$result% correct!", Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun blurCheatButton() {
        val effect = RenderEffect.createBlurEffect(
            2.0f,
            2.0f,
            Shader.TileMode.CLAMP
        )
        binding.cheatButton.setRenderEffect(effect)
    }
}