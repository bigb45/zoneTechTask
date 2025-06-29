package natour.dev.zonetechnologiestask.core.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import natour.dev.zonetechnologiestask.R

object SharedPreferencesUtil {
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(
            context.getString(R.string.sharedPreferencesFileKey),
            Context.MODE_PRIVATE
        )
    }
    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(key: String, default: T): T {
        return when (default) {
            is String -> preferences.getString(key, default) as T
            is Boolean -> preferences.getBoolean(key, default) as T
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }

    fun <T> setValue(key: String, value: T) {
        when(value) {
            is String -> preferences.edit {
                putString(key, value)
            }
            is Boolean -> preferences.edit {
                putBoolean(key, value)
            }
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }

}