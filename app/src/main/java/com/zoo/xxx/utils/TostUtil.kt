package com.zoo.xxx.utils

import android.widget.Toast
import com.zoo.xxx.App

fun String.showToast() {
    Toast.makeText(App.instance, this, Toast.LENGTH_SHORT).show()
}
