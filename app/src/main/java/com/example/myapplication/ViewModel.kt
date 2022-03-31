package com.example.myapplication

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel

class ViewModel:ViewModel() {

   // var Icon:Bitmap? = null;
    var Icon:String = "";
    var Type:String = "";
    var Temperature:String = "";
    var Pressure:String = "";
    var Sunrise:String = "";
    var Sunset:String = "";
    var City:String = "";

    public fun setData(
        //icon:Bitmap?,
        icon:String,
        type:String,
        temperature:String,
        pressure:String,
        sunrise:String,
        sunset:String,
        city:String
    ){
        Icon = icon;
        Type = type;
        Temperature = temperature;
        Pressure = pressure;
        Sunrise = sunrise;
        Sunset = sunset;
        City = city;
    }

}