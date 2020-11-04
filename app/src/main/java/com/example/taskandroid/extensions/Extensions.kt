package com.example.taskandroid.extensions

import android.app.DatePickerDialog
import android.content.Context
import android.widget.TextView
import android.widget.Toast
import java.util.*

// fun defined here can be accessed from MainActivity.kt as well ass ModelTask.kt

fun Context.showToast(msg: String, duration:Int=Toast.LENGTH_SHORT){
    Toast.makeText(this,msg,duration).show()
}

fun Context.setDateOfTask(textView: TextView, idtextView: TextView?){
    val c = Calendar.getInstance()
    val yr = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    val display = DatePickerDialog(
        this,
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            if (idtextView != null) {
                idtextView.text= "$year-${monthOfYear+10}-${dayOfMonth+10}"  // idtextView stores the unique key foe each task
            }
            var monthInput = (monthOfYear + 1).toString()
            if (monthInput.toInt() == 1) {
                monthInput = "Jan"
            } else if (monthInput.toInt() == 2) {
                monthInput = "Feb"
            } else if (monthInput.toInt() == 3) {
                monthInput = "March"
            } else if (monthInput.toInt() == 4) {
                monthInput = "April"
            } else if (monthInput.toInt() == 5) {
                monthInput = "May"
            } else if (monthInput.toInt() == 6) {
                monthInput = "June"
            } else if (monthInput.toInt() == 7) {
                monthInput = "July"
            } else if (monthInput.toInt() == 8) {
                monthInput = "Aug"
            } else if (monthInput.toInt() == 9) {
                monthInput = "Sept"
            } else if (monthInput.toInt() == 10) {
                monthInput = "Oct"
            } else if (monthInput.toInt() == 11) {
                monthInput = "Nov"
            } else if (monthInput.toInt() == 12) {
                monthInput = "Dec"
            }
           textView.text = ("$dayOfMonth $monthInput, $year")
        },
        yr,
        month,
        day
    )
    display.datePicker.minDate = System.currentTimeMillis()
    display.show()
}