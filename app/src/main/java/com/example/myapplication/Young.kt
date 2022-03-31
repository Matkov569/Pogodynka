package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentYoungBinding
import java.net.URL
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.Executors

class Young : Fragment() {

    private var _binding: FragmentYoungBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val themes = listOf<Int>(R.drawable.back_night,R.drawable.back_sunrise,R.drawable.back_day,R.drawable.back_sunset)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentYoungBinding.inflate(inflater, container, false)

        val root: View = binding.root

        val viewModel by activityViewModels<ViewModel>();

        val icon = binding.weatherIcon;
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        var image: Bitmap? = null

        executor.execute {
            val imageURL = "https://openweathermap.org/img/wn/${viewModel.Icon}@4x.png"
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

        binding.weatherType.text=viewModel.Type
        binding.weatherPlace.text=viewModel.City
        binding.weatherTemperature.text=viewModel.Temperature
        binding.weatherPressure.text=viewModel.Pressure
        binding.weatherSunrise.text=viewModel.Sunrise
        binding.weatherSunset.text=viewModel.Sunset

        var sunrise = viewModel.Sunrise.substring(0,2).toInt()
        var sunset = viewModel.Sunset.substring(0,2).toInt()

        var current = Calendar.getInstance();
        println(current.get(Calendar.HOUR_OF_DAY))
        if(current[Calendar.HOUR_OF_DAY] in sunrise-1..sunrise+1 )
            binding.background.setImageResource(themes[1])
        else if (current[Calendar.HOUR_OF_DAY] in sunrise+1..sunset-1 )
            binding.background.setImageResource(themes[2])
        else if (current[Calendar.HOUR_OF_DAY] in sunset-1..sunset+1 )
            binding.background.setImageResource(themes[3])
        else
            binding.background.setImageResource(themes[0])
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewBtn.setOnClickListener {
            var preferences = context?.getSharedPreferences("userdetails", Context.MODE_PRIVATE);
            var editor = preferences?.edit();
            editor?.putBoolean("youngOrElder",false);
            editor?.apply();
            findNavController().navigate(R.id.young_to_elder)
        }

        binding.choosePlaceBtn.setOnClickListener {
            var builder = AlertDialog.Builder(context);
            builder.setTitle("Lokalizacja");
            builder.setMessage("Wprowadź miasto dla którego chcesz sprawdzić pogodę:");
            var input = EditText(context);
            input.inputType= InputType.TYPE_CLASS_TEXT
            builder.setView(input);
            builder.setPositiveButton("Ok"){ dialog, which ->
                var youngOrElderPlace=input.text.toString();
                youngOrElderPlace=youngOrElderPlace[0].uppercaseChar()+ youngOrElderPlace.substring(1)
                    .lowercase(Locale.getDefault());
                var preferences = context?.getSharedPreferences("userdetails", Context.MODE_PRIVATE);
                var editor = preferences?.edit();
                editor?.putString("youngOrElderPlace",youngOrElderPlace);
                editor?.apply();
                findNavController().navigate(R.id.refreshY)
            }
            builder.setNegativeButton("Anuluj"){dialog, which ->}
            builder.show();
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}