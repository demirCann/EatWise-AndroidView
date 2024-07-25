package com.example.feature

import android.os.Bundle
import androidx.navigation.NavArgs
import kotlin.Int
import kotlin.jvm.JvmStatic

data class DetailFragmentArguments(val mealId: Int) : NavArgs {

    fun toBundle(): Bundle {
        val result = Bundle()
        result.putInt("mealId", this.mealId)
        return result
    }

    companion object {
        @JvmStatic
        fun fromBundle(bundle: Bundle): DetailFragmentArguments {
            bundle.setClassLoader(DetailFragmentArguments::class.java.classLoader)
            val _mealId: Int
            if (bundle.containsKey("mealId")) {
                _mealId = bundle.getInt("mealId")
            } else {
                throw IllegalArgumentException("Required argument \"mealId\" is missing and does not have an android:defaultValue")
            }
            return DetailFragmentArguments(_mealId)
        }
    }
}

