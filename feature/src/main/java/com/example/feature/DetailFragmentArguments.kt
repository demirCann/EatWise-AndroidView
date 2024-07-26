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
            bundle.classLoader = DetailFragmentArguments::class.java.classLoader
            val mealId: Int
            if (bundle.containsKey("mealId")) {
                mealId = bundle.getInt("mealId")
            } else {
                throw IllegalArgumentException("Required argument \"mealId\" is missing and does not have an android:defaultValue")
            }
            return DetailFragmentArguments(mealId)
        }
    }
}

