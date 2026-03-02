package net.alhrairyalbraa.kalmani.utils

import android.content.Context

class SharedPrefs(context: Context, prefsName: String) {
    private val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    private val editor = prefs.edit()

    // test in educational section
    fun setTest(sentence: String, category: String){
        editor.putString(Constants.SENTENCE, sentence)
        editor.putString(Constants.CATEGORY, category)
        editor.apply()
    }
    fun getTest(): List<String> = listOf(prefs.getString(Constants.SENTENCE, "Welcome").toString(), prefs.getString(Constants.CATEGORY, "Intro").toString())

    // on-boarding
    fun setBoarding(done: Boolean) {
        editor.putBoolean(Constants.BOARDING, done)
        editor.apply()
    }
    fun getBoarding(): Boolean = prefs.getBoolean(Constants.BOARDING, false)
}