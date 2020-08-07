package com.example.ketemufloren.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ketemufloren.R
import com.example.ketemufloren.SessionPreference
import com.example.ketemufloren.event.AddScheduleEvent
import com.example.ketemufloren.event.HideCompleteViewEvent
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor


class MainActivity : AppCompatActivity() {
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private val calendar = Calendar.getInstance()
    private var datePickerDialog: DatePickerDialog? = null
    private var timePickerDialog: TimePickerDialog? = null
    private val targetCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setupBiometric()
        setupNavController()
        initDatePicker()
    }

    private fun setupNavController() {
        val navController = findNavController(R.id.navController)
        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.homeFragment, R.id.profileFragment))

        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavigationMain.setupWithNavController(navController)

        navAddButton.setOnClickListener {
            showDatePicker()
            EventBus.getDefault().post(HideCompleteViewEvent())
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
            TimePickerDialog(
                this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
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

                    //Calculate time difference
                    val timeTargetInMillis = getDateFromSelectedDate(
                        selectedYear,
                        selectedMonth,
                        selectedDay,
                        hourOfDay,
                        minute
                    ).time

                    EventBus.getDefault().post(AddScheduleEvent(date, timeTargetInMillis))

                },
                currentHour,
                currentMinute,
                true
            )
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
}