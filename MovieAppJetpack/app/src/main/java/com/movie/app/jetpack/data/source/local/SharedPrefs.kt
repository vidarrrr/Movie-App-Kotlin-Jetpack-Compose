package com.movie.app.jetpack.data.source.local

import android.content.Context
import androidx.preference.PreferenceManager
import com.movie.app.jetpack.common.Constants


class SharedPrefs {

    fun getParamBoolean(context: Context, param: String): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean(param, false)
    }

    fun setParamBoolean(context: Context, param: String, value: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putBoolean(param, value).apply()
    }

    fun removeParam(context: Context, param: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().remove(param).apply()
    }

    fun getParamString(context: Context, param: String): String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(param,"")
    }


    fun setParamString(context: Context, param: String, value: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putString(param, value).apply()
    }

    fun getParamMenuIndex(context: Context): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getInt(Constants.MENU_INDEX,0)
    }


    fun setParamMenuIndex(context: Context, value: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putInt(Constants.MENU_INDEX, value).apply()
    }

    fun getParamMenuName(context: Context,name:String, range: Int):Int{
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        for(value in 0..range){
            val nameValue = sharedPreferences.getString("${Constants.MENU_ITEM_SUFFIX}${value}","")
            nameValue?.let {
                if(it==name) return value
            }
        }
        return -1
    }

    fun reorderParamMenu(context: Context,start: Int,range: Int){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        for(value in start+1..range){
            val nameValue = sharedPreferences.getString("${Constants.MENU_ITEM_SUFFIX}${value}","")
            sharedPreferences.edit().putString("${Constants.MENU_ITEM_SUFFIX}${value-1}",nameValue).apply()
        }
        sharedPreferences.edit().remove("${Constants.MENU_ITEM_SUFFIX}${range}").apply()
    }


}