package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentYoungBinding
import java.net.URL
import java.util.concurrent.Executors

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

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val themes = listOf<Int>(R.drawable.back_night,R.drawable.back_sunrise,R.drawable.back_day,R.drawable.back_sunset)
        var tryb = 0;




        binding.background.setImageResource(themes[tryb])

        binding.goBtn.setOnClickListener {
            tryb = (tryb+1)%4;
            binding.background.setImageResource(themes[tryb])
        }

        binding.viewBtn.setOnClickListener {
            findNavController().navigate(R.id.young_to_elder)
            //zapisz że domyślny jest dla starych
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}