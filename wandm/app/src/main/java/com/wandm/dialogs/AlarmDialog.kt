package com.wandm.dialogs

import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import com.wandm.App
import com.wandm.R
import com.wandm.utils.PreferencesUtils
import com.wandm.utils.Utils
import kotlinx.android.synthetic.main.dialog_timer.*
import org.jetbrains.anko.textColor

class AlarmDialog : BaseDialog(), View.OnClickListener {

    private var colorResId = R.color.color_dark_theme
    private var textSize = 18

    companion object {
        private val TAG = "AlarmDialog"

        private var minute = 0
        private var second = 0
        private var listener: ((Boolean) -> Unit)? = null

        private var minuteStr = ""
        private var secondStr = ""

        fun newInstance(listener: (Boolean) -> Unit): AlarmDialog {
            Companion.listener = listener
            val fragment = AlarmDialog()
            fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.EtsyBlurDialogTheme)
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.dialog_timer, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTheme()

        setAlarmButton.setOnClickListener(this)
        cancelAlarmButton.setOnClickListener(this)

        minuteNumberPicker.minValue = 0
        minuteNumberPicker.maxValue = 60
        minuteNumberPicker.wrapSelectorWheel = true
        secondNumberPicker.minValue = 0
        secondNumberPicker.maxValue = 60
        secondNumberPicker.wrapSelectorWheel = true

        minuteNumberPicker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener {
            override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {
                if (p2 <= 1)
                    minuteStr = p2.toString() + " " + activity.resources.getString(R.string.minute)
                else
                    minuteStr = p2.toString() + " " + activity.resources.getString(R.string.minutes)

                minuteTextView.text = minuteStr
            }

        })


        secondNumberPicker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener {
            override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {
                if (p2 <= 1)
                    secondStr = p2.toString() + " " + activity.resources.getString(R.string.second)
                else
                    secondStr = p2.toString() + " " + activity.resources.getString(R.string.seconds)

                secondTextView.text = secondStr
            }

        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.setAlarmButton -> {
                minute = minuteNumberPicker.value
                second = secondNumberPicker.value

                if (second <= 1)
                    secondStr = second.toString() + " " + activity.resources.getString(R.string.second)
                else
                    secondStr = second.toString() + " " + activity.resources.getString(R.string.seconds)

                if (minute <= 1)
                    minuteStr = minute.toString() + " " + activity.resources.getString(R.string.minute)
                else
                    minuteStr = minute.toString() + " " + activity.resources.getString(R.string.minutes)


                PreferencesUtils.setAlarm(minute.toString() + ";;" + second.toString())

                Toast.makeText(App.instance, activity.resources.getString(R.string.alarm_toast, minuteStr, secondStr),
                        Toast.LENGTH_SHORT).show()
                listener!!(true)
                dismiss()
            }

            R.id.cancelAlarmButton -> {
                PreferencesUtils.setAlarm("0;;0")
                listener!!(false)
                Toast.makeText(App.instance, activity.resources.getString(R.string.turn_off_alarm), Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

    private fun setupTheme() {
        val isLightTheme = PreferencesUtils.getLightTheme()
        Utils.applyLightTheme(activity)

        textSize = PreferencesUtils.getTextSize()
        if (isLightTheme) {
            colorResId = R.color.color_light_theme
        }

        minuteTextView.textColor = resources.getColor(colorResId)
        secondTextView.textColor = resources.getColor(colorResId)

        minuteTextView.textSize = (textSize + 6).toFloat()
        secondTextView.textSize = (textSize + 6).toFloat()

        cancelAlarmButton.setColor(resources.getColor(colorResId))
        setAlarmButton.setColor(resources.getColor(colorResId))
    }

}