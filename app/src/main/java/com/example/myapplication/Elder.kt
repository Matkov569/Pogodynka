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
import com.example.myapplication.databinding.FragmentElderBinding
import java.net.URL
import java.util.*
import java.util.concurrent.Executors

class Elder : Fragment() {

    private var _binding: FragmentElderBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentElderBinding.inflate(inflater, container, false)
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

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewBtn.setOnClickListener {
            var preferences = context?.getSharedPreferences("userdetails", Context.MODE_PRIVATE);
            var editor = preferences?.edit();
            editor?.putBoolean("youngOrElder",true);
            editor?.apply();
            findNavController().navigate(R.id.elder_to_young)
            //zapisz że domyślny jest dla młodych

        }

        val themes = listOf<Int>(R.drawable.back_night,R.drawable.back_sunrise,R.drawable.back_day,R.drawable.back_sunset)
        var tryb = 0;

        binding.background.setImageResource(themes[tryb])

        binding.goBtn.setOnClickListener {
            tryb = (tryb+1)%4;
            binding.background.setImageResource(themes[tryb])
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
                findNavController().navigate(R.id.refreshE)
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