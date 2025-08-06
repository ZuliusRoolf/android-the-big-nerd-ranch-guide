package com.bignerdranch.android.geoquiz

import androidx.lifecycle.ViewModel
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import kotlin.text.get

private const val TAG = "QuizViewModel"
const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"
const val IS_CHEATER_KEY = "IS_CHEATER_KEY"
const val CHEAT_TOKENS_KEY = "CHEAT_TOKENS_KEY"
const val IS_CHEATER_ARRAY_KEY = "IS_CHEATER_ARRAY_KEY"

class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )
    private val answers = arrayOfNulls<Int>(questionBank.size)

    private var isCheaterArray: BooleanArray
        get() = savedStateHandle[IS_CHEATER_ARRAY_KEY] ?: BooleanArray(questionBank.size)
        set(array) = savedStateHandle.set(IS_CHEATER_ARRAY_KEY, array)

    private var currentIndex: Int
        get() = savedStateHandle.get(CURRENT_INDEX_KEY) ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    var cheatTokens: Int
        get() = savedStateHandle.get(CHEAT_TOKENS_KEY) ?: 3
        set(value) = savedStateHandle.set(CHEAT_TOKENS_KEY, value)

    fun isCheaterOnCurrentQuestion(): Boolean {
        return isCheaterArray[currentIndex]
    }
    fun cheatedOnCurrentQuestion(isCheater: Boolean) {
        // Remember that arrays are immutable and savedStateHandle do not know when value is changed
        val array = isCheaterArray
        array[currentIndex] = isCheater
        isCheaterArray = array
    }

    fun getAnswer() : Int? {
        return answers[currentIndex]
    }

    fun setAnswer(value: Int) {
        answers[currentIndex] = value
    }

    fun gradeAnswers(): Int? {
        var result: Int = 0
        if (questionBank.size == 0) return null
        for (answer in answers){
            if (answer == null) return null
            result += answer
        }
        result /= questionBank.size
        return result
    }

}