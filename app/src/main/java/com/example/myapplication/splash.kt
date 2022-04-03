package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.*
import android.os.StrictMode.ThreadPolicy
import android.text.InputType.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException
import java.net.URL
import java.security.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.reflect.typeOf


class splash : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var preferences = context?.getSharedPreferences("userdetails",MODE_PRIVATE);
        var youngOrElder = preferences!!.getBoolean("youngOrElder",false);
        var youngOrElderPlace = preferences.getString("youngOrElderPlace","");

        //var editor = preferences.edit();
        //editor.remove("youngOrElderPlace");
        //editor.apply()


        if(youngOrElderPlace==""){
            var builder = AlertDialog.Builder(context);
            builder.setTitle("Lokalizacja");
            builder.setMessage("Wprowadź miasto dla którego chcesz sprawdzić pogodę:");
            var input = EditText(context);
            input.inputType= TYPE_CLASS_TEXT
            builder.setView(input);
            builder.setPositiveButton("Ok"){ dialog, which ->
                youngOrElderPlace=input.text.toString();
                youngOrElderPlace= youngOrElderPlace!![0].uppercaseChar()+ youngOrElderPlace!!.substring(1);
                var editor = preferences.edit();
                editor.putString("youngOrElderPlace",youngOrElderPlace);
                editor.apply();
                goToControl(view,youngOrElder,youngOrElderPlace!!);
            }
            builder.show();
        }
        else{
            goToControl(view,youngOrElder,youngOrElderPlace!!)
        }


        view.findViewById<Button>(R.id.resetWifi).setOnClickListener {
            goToControl(view,youngOrElder,youngOrElderPlace!!)
        }

    }

    fun goToControl(view: View,youngOrElder:Boolean, youngOrElderPlace:String){
        if(networkTest()){
            view.findViewById<ConstraintLayout>(R.id.alertBox).visibility=View.INVISIBLE;

            runBlocking{ getJSON(view, youngOrElderPlace, youngOrElder); }
        }
        else{
            view.findViewById<ConstraintLayout>(R.id.alertBox).visibility=View.VISIBLE;
        }
    }

    suspend fun getJSON(view:View, city: String, youngOrElder: Boolean){
        var json = "";
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val apiID:String = secrets().apiID;
        try {
            runBlocking {

                json = URL("https://api.openweathermap.org/data/2.5/weather?q=$city&lang=pl&units=metric&appid=$apiID").readText()

                val viewModel by activityViewModels<ViewModel>();

                var obj = JSONObject(json)

                var weather = obj.getJSONArray("weather").getJSONObject(0);
                var main = obj.getJSONObject("main");
                var sys = obj.getJSONObject("sys");

                var icon = weather["icon"].toString();
                var type = weather["description"].toString()[0].uppercaseChar() + weather["description"].toString().substring(1).lowercase(Locale.getDefault())
                    .lowercase(Locale.getDefault());
                var temperature = main["temp"].toString();
                var pressure = main["pressure"].toString();

                var SDF = SimpleDateFormat("HH:mm")
                SDF.timeZone= TimeZone.getDefault()

                var sunrise = SDF.format(Date(sys["sunrise"].toString().toLong()* 1000));
                var sunset = SDF.format(Date(sys["sunset"].toString().toLong()* 1000));

                viewModel.setData(icon,type,temperature,pressure,sunrise, sunset, city+", "+sys["country"].toString())

                launch {
                    if(youngOrElder)
                        findNavController().navigate(R.id.action_splash_to_young);
                    else
                        findNavController().navigate(R.id.action_splash_to_elder);
                }
            }
        }
        catch (e: FileNotFoundException){
            var builder = AlertDialog.Builder(context);
            builder.setTitle("Brak danych");
            builder.setMessage("Dane pogodowe dla podanej lokalizacji są niedostępne. Spróbuj ustawić inną lokalizację, lub sprawdź spis dostępnych lokalizacji na stronie OpenWeatherMap.");
            builder.setPositiveButton("Ok"){ dialog, which ->
                var builder = AlertDialog.Builder(context);
                builder.setTitle("Lokalizacja");
                builder.setMessage("Wprowadź miasto dla którego chcesz sprawdzić pogodę:");
                var input = EditText(context);
                input.inputType= TYPE_CLASS_TEXT
                builder.setView(input);
                builder.setPositiveButton("Ok"){ dialog, which ->
                    var youngOrElderPlace=input.text.toString();
                    youngOrElderPlace=youngOrElderPlace[0].uppercaseChar()+youngOrElderPlace.substring(1).lowercase(Locale.getDefault());
                    var preferences = context?.getSharedPreferences("userdetails",MODE_PRIVATE);
                    var editor = preferences?.edit();
                    editor?.putString("youngOrElderPlace",youngOrElderPlace);
                    editor?.apply();
                    goToControl(view,youngOrElder, youngOrElderPlace);
                }
                builder.show();
            }
            builder.show();
        }
        catch (e: Exception){
            println("JSON EXCEPTION")
            println(e.toString())
        }
    }

    fun networkTest():Boolean{
        var cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm.activeNetwork ?: return false
            val activeNetwork = cm.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                cm.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}