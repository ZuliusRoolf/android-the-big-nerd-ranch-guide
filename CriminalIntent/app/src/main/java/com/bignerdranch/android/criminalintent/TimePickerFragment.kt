package com.bignerdranch.android.criminalintent

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import kotlin.getValue

class TimePickerFragment : DialogFragment() {

    private val args: TimePickerFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val timeListener = TimePickerDialog.OnTimeSetListener {
                _: TimePicker, hour: Int, minutes: Int ->
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minutes)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val resultTime = calendar.time
            setFragmentResult(REQUEST_KEY_TIME, bundleOf(BUNDLE_KEY_TIME to resultTime))
        }

        val calendar = Calendar.getInstance()
        calendar.time = args.crimeTime
        val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinutes = calendar.get(Calendar.MINUTE)
        val is24hour = true

        return TimePickerDialog(
            requireContext(),
            timeListener,
            initialHour,
            initialMinutes,
            is24hour
        )
    }

    companion object {
        const val REQUEST_KEY_TIME = "REQUEST_KEY_TIME"
        const val BUNDLE_KEY_TIME = "BUNDLE_KEY_TIME"
    }
}