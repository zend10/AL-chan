package com.zen.alchan.ui.settings.app

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.zen.alchan.R
import kotlinx.android.synthetic.main.dialog_number_picker.view.*

class PushNotificationMinHoursDialog : DialogFragment() {

    interface PushNotificationMinHoursListener {
        fun passHour(hour: Int)
    }

    private lateinit var listener: PushNotificationMinHoursListener
    private val hourArray = Array(24) { it + 1 }
    private var currentHour = 1

    companion object {
        const val CURRENT_HOUR = "currentHour"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_number_picker, null)

        if (!this::listener.isInitialized) {
            dismiss()
        }

        currentHour = arguments?.getInt(CURRENT_HOUR, 1) ?: 1

        view.numberPicker.apply {
            minValue = 1
            maxValue = 24
            displayedValues = hourArray.map { it.toString() }.toTypedArray()
            wrapSelectorWheel = false
            value = currentHour
        }

        builder.setView(view)
        builder.setPositiveButton(R.string.set) { _, _ ->
            listener.passHour(view.numberPicker.value)
        }
        builder.setNegativeButton(R.string.cancel, null)
        return builder.create()
    }

    fun setListener(pushNotificationMinHoursListener: PushNotificationMinHoursListener) {
        listener = pushNotificationMinHoursListener
    }
}