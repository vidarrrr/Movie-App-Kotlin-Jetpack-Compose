package com.movie.app.jetpack.data.source.local

import android.content.Context
import androidx.preference.PreferenceManager
import com.movie.app.jetpack.common.Constants


class SharedPrefs {

    inline fun <reified T> getParam(context:Context, param:String):T{
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return when(T::class){
            String::class -> {
                sharedPreferences.getString(param,"") as T
            }
            Boolean::class ->{
                sharedPreferences.getBoolean(param,false) as T
            }
            Int::class ->{
                sharedPreferences.getInt(param,0) as T
            }
            else ->{
                throw IllegalArgumentException("Undefined type")
            }
        }
    }

    inline fun <reified T> setParam(context: Context, param:String, value:T):Unit{
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        when(T::class){
            String::class -> {
                sharedPreferences.edit().putString(param,value as String).apply()
            }
            Boolean::class -> {
                sharedPreferences.edit().putBoolean(param,value as Boolean).apply()
            }
            Int::class -> {
                sharedPreferences.edit().putInt(param,value as Int).apply()
            }
            else -> {
                throw IllegalArgumentException("Undefined type")
            }
        }
    }


    fun removeParam(context: Context, param: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().remove(param).apply()
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