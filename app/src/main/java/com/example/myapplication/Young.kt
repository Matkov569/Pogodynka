package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentYoungBinding
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class Young : Fragment() {

    private var _binding: FragmentYoungBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentYoungBinding.inflate(inflater, container, false)

        val root: View = binding.root

        var json = "";
        try{
            json = URL("https://api.openweathermap.org/data/2.5/weather?q=Rybnik&lang=pl&units=metric&appid=7aa105dca7b6e8ea0769b49d8c87b2aa")
                .readText()

            println("IUIUIUIU")
        }
        catch (e: Exception){
            println("NIE PYKło xd")
        }

        //print(apiResponse.toString())

        //ikonka
        val icon = binding.weatherIcon;
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        var image: Bitmap? = null

        executor.execute {
            val imageURL = "https://openweathermap.org/img/wn/03d@4x.png"
            try {
                val `in` = URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
                handler.post {
                    icon.setImageBitmap(image)
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val themes = listOf<Int>(R.drawable.back_night,R.drawable.back_sunrise,R.drawable.back_day,R.drawable.back_sunset)
        var tryb = 0;




        binding.background.setImageResource(themes[tryb])

        binding.goBtn.setOnClickListener {
            Toast.makeText(context,"AAA",Toast.LENGTH_LONG).show()
            tryb = (tryb+1)%4;
            binding.background.setImageResource(themes[tryb])
            //findNavController().navigate(R.id.action_navigation_home_to_navigation_dashboard)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}