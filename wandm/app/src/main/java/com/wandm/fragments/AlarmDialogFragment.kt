package com.wandm.fragments

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
import kotlinx.android.synthetic.main.dialog_timer.*

class AlarmDialogFragment : BaseDialogFragment(), View.OnClickListener {

    companion object {
        private var minute = 0
        private var second = 0
        private var listener: ((Boolean) -> Unit)? = null

        fun newInstance(listener: (Boolean) -> Unit): AlarmDialogFragment {
            this.listener = listener
            val fragment = AlarmDialogFragment()
            fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.EtsyBlurDialogTheme)
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.dialog_timer, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                minuteTextView.text = p2.toString() + " phút"
            }

        })


        secondNumberPicker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener {
            override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {
                secondTextView.text = p2.toString() + " giây"
            }

        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.setAlarmButton -> {
                minute = minuteNumberPicker.value
                second = secondNumberPicker.value
                PreferencesUtils.setAlarm(minute.toString() + ";;" + second.toString())
                Toast.makeText(App.instance, "Tắt nhạc sau $minute phút $second giây", Toast.LENGTH_SHORT).show()
                listener!!(true)
                dismiss()
            }

            R.id.cancelAlarmButton -> {
                PreferencesUtils.setAlarm("0;;0")
                listener!!(false)
                Toast.makeText(App.instance, "Đã huỷ hẹn giờ", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

}