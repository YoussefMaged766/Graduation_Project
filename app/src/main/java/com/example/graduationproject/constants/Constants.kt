package com.example.graduationproject.constants

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.graduationproject.R
import com.gmail.samehadar.iosdialog.CamomileSpinner

class Constants {
    companion object {
        fun String.validateEmail(): Boolean {
            return if (this.isEmpty()) {
                false
            } else {
                android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
            }
        }

        fun String.validatePass(): Boolean {
            return !(this.isEmpty() || this.length < 8)
        }

        fun String.checkPass(): Boolean {
            val symbols = listOf(
                '!',
                '@',
                '#',
                '$',
                '%',
                '^',
                '&',
                '*',
                '(',
                ')',
                '_',
                '+',
                '-',
                '=',
                '{',
                '}',
                '[',
                ']',
                '|',
                ':',
                ';',
                '<',
                '>',
                ',',
                '.',
                '?',
                '/'
            )
            val hasLetter = this.any { it.isLetter() && it.isUpperCase() }
            val hasNumber = this.any { it.isDigit() }
            return hasLetter && hasNumber && this.all { it.isLetter() || it.isDigit() || it in symbols }
        }


        fun String.validateFirstname(): Boolean {
            return !(this.isEmpty() || this.length < 3)
        }

        fun String.validateLastname(): Boolean {
            return !(this.isEmpty() || this.length < 4)
        }

        fun customToast(context: Context, activity: Activity, msg: String) {
            val inflater: LayoutInflater = activity.layoutInflater
            val layout: View = inflater.inflate(
                R.layout.customtoast,
                activity.findViewById(R.id.toast_layout_root)
            ) as ViewGroup

            val text: TextView = layout.findViewById(R.id.txt_msg);
            text.text = msg

            val toast = Toast(context)
            toast.duration = Toast.LENGTH_LONG
            toast.view = layout
            toast.show()
        }

        val Context.dataStore: DataStore<Preferences> by preferencesDataStore("save")
        lateinit var dialog: Dialog
        fun showCustomAlertDialog(
            activity: Activity,
            layout: Int,
            checkCancel: Boolean,
        ) {
            if (!activity.isFinishing && !activity.isDestroyed) {
                dialog = Dialog(activity)
                dialog.setContentView(layout)
                val loader: CamomileSpinner = dialog.findViewById(R.id.progress)
                loader.start()
                dialog.setCancelable(checkCancel)

                dialog.show()

            }
        }

        fun showRatingAlertDialog(
            activity: Activity,
            layout: Int,
            checkCancel: Boolean,
            submitListener: (String) -> Unit
        ) {
            if (!activity.isFinishing && !activity.isDestroyed) {
                dialog = Dialog(activity)
                dialog.setContentView(layout)
                val submit = dialog.findViewById<Button>(R.id.btnSubmit)
                val cancel = dialog.findViewById<Button>(R.id.btnCancel)
                val rate = dialog.findViewById<RatingBar>(R.id.ratingBar)

                cancel.setOnClickListener {
                    dialog.dismiss()
                }
                submit.setOnClickListener {
                    val rating = rate.rating.toString()
                    submitListener.invoke(rating)
                    dialog.dismiss()
                }
                dialog.setCancelable(checkCancel)
                dialog.show()

            }
        }

        fun hideCustomAlertDialog() {
            try {
                if (::dialog.isInitialized && dialog.isShowing) {
                    dialog.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("hideCustomAlertDialog: ", e.message.toString())
            }


        }

        const val userToken = "userToken"
        const val userId = "userId"
        const val CAMERA_REQUEST_CODE = 1
        const val GALLERY_REQUEST_CODE = 2
    }
}