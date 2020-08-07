package com.example.ketemufloren.fragment

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ketemufloren.CompleteType
import com.example.ketemufloren.R
import com.example.ketemufloren.SessionPreference
import com.example.ketemufloren.event.AddScheduleEvent
import com.example.ketemufloren.event.HideCompleteViewEvent
import com.example.ketemufloren.helper.DateHelper
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_complete_meet.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random


class HomeFragment : Fragment() {

    private lateinit var sessionPreference: SessionPreference
    private lateinit var fragmentContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentContext = view.context
        sessionPreference = SessionPreference(fragmentContext)

        checkIsAlreadyMeet()
        initMeetingTime()
        setupSpecialButton()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe
    fun onAddScheduleEvent(event: AddScheduleEvent) {
        tvSelectedDate.text = event.date
        val timeDifference = DateHelper.calculateDateDifferenceInMillis(event.timeTargetInMillis)
        sessionPreference.saveMeetingDate(event.timeTargetInMillis)

        countDownView.start(timeDifference)
        setOnCountdownEnd()
    }

    @Subscribe
    fun onHideCompleteViewEvent(event: HideCompleteViewEvent) {
        layoutCompleteMeet.visibility = View.GONE
    }

    private fun initMeetingTime() {
        if (sessionPreference.getMeetingDate() > 0L) {
            tvSelectedDate.text = convertLongToTime(sessionPreference.getMeetingDate())
            countDownView.start(calculateDateDifferenceInMillis(sessionPreference.getMeetingDate()))
            setOnCountdownEnd()
        }
    }

    private fun checkIsAlreadyMeet() {
        if (calculateDateDifferenceInMillis(sessionPreference.getMeetingDate()) <= 0L) {
            showCompleteMeeting()
        }
    }

    private fun setOnCountdownEnd() {
        if (!layoutCompleteMeet.isShown) {
            countDownView.setOnCountdownEndListener {
                showCompleteMeeting()
            }
        }
    }

    private fun showCompleteMeeting() {
        sessionPreference.saveMeetingDate(0L)
        setCompleteTypeOfMeeting(12)
        layoutCompleteMeet.visibility = View.VISIBLE
    }

    private fun setCompleteTypeOfMeeting(totalPic: Int) {
        val random = Random.nextInt(0, totalPic - 1) //random number from 0-6

        @DrawableRes
        val completeImage: Int

        @StringRes
        val completeText: Int

        when (random) {
            CompleteType.SLEEP.value -> {
                completeImage =
                    R.drawable.complete_image_sleep
                completeText =
                    R.string.complete_text_sleep
            }
            CompleteType.SLEEP_DARK.value -> {
                completeImage =
                    R.drawable.complete_image_sleep_dark
                completeText =
                    R.string.complete_text_sleep_dark
                ivCompleteImage.imageTintList = null

                btnKiss.visibility = View.VISIBLE
            }
            CompleteType.MAD.value -> {
                completeImage =
                    R.drawable.complete_image_mad
                completeText =
                    R.string.complete_text_mad
            }
            CompleteType.NOT_IN_A_MOOD.value -> {
                completeImage =
                    R.drawable.complete_image_not_in_a_mood
                completeText =
                    R.string.complete_text_not_in_a_mood
            }
            CompleteType.PIMPLES.value -> {
                completeImage =
                    R.drawable.complete_image_pimples
                completeText =
                    R.string.complete_text_pimples
            }
            CompleteType.LIVE_TALLY.value -> {
                completeImage =
                    R.drawable.complete_image_live_tally
                completeText =
                    R.string.complete_text_live_tally
            }
            CompleteType.HOLD_HAND.value -> {
                completeImage =
                    R.drawable.complete_image_hold_hand
                completeText =
                    R.string.complete_text_hold_hand
            }
            CompleteType.PICK_UP.value -> {
                completeImage =
                    R.drawable.complete_image_ready_picked_up
                completeText =
                    R.string.complete_text_pick_up
            }
            CompleteType.SLEEP_NEXT_TIME.value -> {
                completeImage =
                    R.drawable.complete_image_sleep_next_time
                completeText =
                    R.string.complete_text_sleep_next_time
            }
            CompleteType.SWEET_KISS.value -> {
                completeImage =
                    R.drawable.complete_image_sweet_kiss
                completeText =
                    R.string.complete_text_sweet_kiss
            }
            else -> {
                completeImage =
                    R.drawable.complete_image_finally_meet
                completeText =
                    R.string.complete_text_finally_meet
                ivCompleteImage.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        fragmentContext,
                        R.color.transparent_light_black
                    )
                )
            }
        }

        ivCompleteImage.setImageResource(completeImage)
        tvCompleteText.text = getString(completeText)
    }

    private fun setupSpecialButton() {
        btnKiss.setOnClickListener {
            ivCompleteImage.setImageResource(R.drawable.complete_image_woke_up)
            tvCompleteText.text = getString(R.string.complete_text_woke_up)

            btnKiss.visibility = View.GONE
        }
    }

    private fun calculateDateDifferenceInMillis(targetDayInMillis: Long): Long {
        return targetDayInMillis - System.currentTimeMillis()
    }

    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("MMM dd, yyyy, HH:mm", Locale.US)
        return format.format(date)
    }

}