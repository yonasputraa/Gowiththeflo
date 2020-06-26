package com.example.ketemufloren

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.ketemufloren.base.BasePreference

class SessionPreference(private var context: Context) : BasePreference() {

    override val sharedPreference: SharedPreferences
        get() = context.getSharedPreferences(
            "app_session",
            Context.MODE_PRIVATE
        )

    fun saveMeetingDate(date: Long?) {
        putData("meeting_date", date)
    }

    fun getMeetingDate(): Long {
        return getLongValue("meeting_date", 0)
    }

    fun setCompletedImage(numCompleted: String) {
        putData("number_completed", numCompleted)
    }

    fun getCompletedImageIndex(): String? {
        return getStringData("number_completed", "")
    }
}