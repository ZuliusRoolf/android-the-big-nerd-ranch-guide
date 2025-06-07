package com.bignerdranch.android.criminalintent

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bignerdranch.android.criminalintent.databinding.FragmentCrimeDetailBinding
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class CrimeDetailFragment : Fragment() {

    private var _binding: FragmentCrimeDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val args: CrimeDetailFragmentArgs by navArgs()

    private val crimeDetailViewModel: CrimeDetailViewModel by viewModels{
        CrimeDetailViewModelFactory(args.crimeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrimeDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            crimeTitle.doOnTextChanged { text, _, _, _ ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(title = text.toString())
                }
            }

            crimeSolved.setOnCheckedChangeListener { _, isChecked ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(isSolved = isChecked)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeDetailViewModel.crime.collect { crime ->
                    crime?.let { updateUi(it) }
                }
            }
        }

        setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE
        ) { requestKey, bundle ->
            val newDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE, Date::class.java)
                    ?: throw IllegalStateException("Date is missing or not of type Date")
            } else {
                @Suppress("DEPRECATION")
                bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            }
            crimeDetailViewModel.updateCrime { it.copy(date = newDate) }
        }

        setFragmentResultListener(
            TimePickerFragment.REQUEST_KEY_TIME
        ) { requestKey, bundle ->
            val newDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getSerializable(TimePickerFragment.BUNDLE_KEY_TIME, Date::class.java)
                    ?: throw IllegalStateException("Date is missing or not of type Date")
            } else {
                @Suppress("DEPRECATION")
                bundle.getSerializable(TimePickerFragment.BUNDLE_KEY_TIME) as Date
            }
            crimeDetailViewModel.updateCrime { it.copy(date = newDate) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUi(crime: Crime) {
        binding.apply {
            if (crimeTitle.text.toString() != crime.title) {
                crimeTitle.setText(crime.title)
            }
            crimeDate.text = crime.date.toString()
            crimeDate.setOnClickListener {
                findNavController().navigate(
                    CrimeDetailFragmentDirections.selectDate(crime.date)
                )
            }
            crimeTime.setOnClickListener {
                findNavController().navigate(
                    CrimeDetailFragmentDirections.selectTime(crime.date)
                )
            }

            crimeSolved.isChecked = crime.isSolved
        }
    }
}