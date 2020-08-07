package com.example.ketemufloren.helper

class DateHelper {
    companion object {
        fun calculateDateDifferenceInMillis(targetDayInMillis: Long): Long {
            return targetDayInMillis - System.currentTimeMillis()
        }
    }
}