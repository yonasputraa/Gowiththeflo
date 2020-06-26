package com.example.ketemufloren

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.ketemufloren.CompleteType.*
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private var datePickerDialog: DatePickerDialog? = null
    private var timePickerDialog: TimePickerDialog? = null
    private val calendar = Calendar.getInstance()
    private val targetCalendar = Calendar.getInstance()
    private val sessionPreference = SessionPreference(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBiometric()

        checkIsAlreadyMeet()
        initMeetingTime()
        initDatePicker()
        fabMeetFloren.setOnClickListener {
            showDatePicker()
            completeBackgroundLayout.visibility = View.GONE
        }
        btnKiss.setOnClickListener {
            ivCompleteImage.setImageResource(R.drawable.complete_image_woke_up)
            tvCompleteText.text = getString(R.string.complete_text_woke_up)

            fabMeetFloren.visibility = View.VISIBLE
            btnKiss.visibility = View.GONE
        }
    }

    private fun setupBiometric() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    biometricPrompt.authenticate(promptInfo)
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Are you Floren's boyfriend?")
            .setSubtitle("Please submit your biometric credential")
            .setNegativeButtonText("Use Passcode")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun checkIsAlreadyMeet() {
        if (calculateDateDifferenceInMillis(sessionPreference.getMeetingDate()) <= 0L) {
            showCompleteMeeting()
        }
    }

    private fun setOnCountdownEnd() {
        if (!completeBackgroundLayout.isShown) {
            countDownView.setOnCountdownEndListener {
                showCompleteMeeting()
            }
        }
    }

    private fun showCompleteMeeting() {
        sessionPreference.saveMeetingDate(0L)
        setCompleteTypeOfMeeting(12)
        completeBackgroundLayout.visibility = View.VISIBLE
    }

    private fun initMeetingTime() {
        if (sessionPreference.getMeetingDate() > 0L) {
            tvSelectedDate.text = convertLongToTime(sessionPreference.getMeetingDate())
            countDownView.start(calculateDateDifferenceInMillis(sessionPreference.getMeetingDate()))
            setOnCountdownEnd()
        }
    }

    private fun initDatePicker() {
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                initTimePicker(year, month, dayOfMonth)
                showTimePicker()
            }, currentYear, currentMonth, currentDay
        )
    }

    private fun initTimePicker(selectedYear: Int, selectedMonth: Int, selectedDay: Int) {
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        timePickerDialog =
            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                //Set Date Text
                val pattern = "MMM dd, yyyy, HH:mm"
                val simpleDateFormat = SimpleDateFormat(pattern, Locale.US)
                val date: String = simpleDateFormat.format(
                    getDateFromSelectedDate(
                        selectedYear,
                        selectedMonth,
                        selectedDay,
                        hourOfDay,
                        minute
                    )
                )
                tvSelectedDate.text = date

                //Calculate time difference
                val timeTargetInMillis = getDateFromSelectedDate(
                    selectedYear,
                    selectedMonth,
                    selectedDay,
                    hourOfDay,
                    minute
                ).time

                val timeDifference = calculateDateDifferenceInMillis(timeTargetInMillis)
                sessionPreference.saveMeetingDate(timeTargetInMillis)

                countDownView.start(timeDifference)
                setOnCountdownEnd()
            }, currentHour, currentMinute, true)
    }

    private fun showDatePicker() {
        datePickerDialog?.show()
    }

    private fun showTimePicker() {
        timePickerDialog?.show()
    }

    private fun getDateFromSelectedDate(
        year: Int, month: Int, day: Int,
        hours: Int, minute: Int
    ): Date {
        targetCalendar.set(year, month, day, hours, minute)
        return targetCalendar.time
    }

    private fun calculateDateDifferenceInMillis(targetDayInMillis: Long): Long {
        return targetDayInMillis - System.currentTimeMillis()
    }

    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("MMM dd, yyyy, HH:mm", Locale.US)
        return format.format(date)
    }

    private fun setCompleteTypeOfMeeting(totalPic: Int) {
        val random = Random.nextInt(0, totalPic - 1) //random number from 0-6

        @DrawableRes
        val completeImage: Int

        @StringRes
        val completeText: Int

        when (random) {
            SLEEP.value -> {
                completeImage = R.drawable.complete_image_sleep
                completeText = R.string.complete_text_sleep
            }
            SLEEP_DARK.value -> {
                completeImage = R.drawable.complete_image_sleep_dark
                completeText = R.string.complete_text_sleep_dark
                ivCompleteImage.imageTintList = null

                fabMeetFloren.visibility = View.GONE
                btnKiss.visibility = View.VISIBLE
            }
            MAD.value -> {
                completeImage = R.drawable.complete_image_mad
                completeText = R.string.complete_text_mad
            }
            NOT_IN_A_MOOD.value -> {
                completeImage = R.drawable.complete_image_not_in_a_mood
                completeText = R.string.complete_text_not_in_a_mood
            }
            PIMPLES.value -> {
                completeImage = R.drawable.complete_image_pimples
                completeText = R.string.complete_text_pimples
            }
            LIVE_TALLY.value -> {
                completeImage = R.drawable.complete_image_live_tally
                completeText = R.string.complete_text_live_tally
            }
            HOLD_HAND.value -> {
                completeImage = R.drawable.complete_image_hold_hand
                completeText = R.string.complete_text_hold_hand
            }
            PICK_UP.value -> {
                completeImage = R.drawable.complete_image_ready_picked_up
                completeText = R.string.complete_text_pick_up
            }
            SLEEP_NEXT_TIME.value -> {
                completeImage = R.drawable.complete_image_sleep_next_time
                completeText = R.string.complete_text_sleep_next_time
            }
            SWEET_KISS.value -> {
                completeImage = R.drawable.complete_image_sweet_kiss
                completeText = R.string.complete_text_sweet_kiss
            }
            else -> {
                completeImage = R.drawable.complete_image_finally_meet
                completeText = R.string.complete_text_finally_meet
                ivCompleteImage.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.transparent_light_black)
                )
            }
        }

        ivCompleteImage.setImageResource(completeImage)
        tvCompleteText.text = getString(completeText)
    }
}