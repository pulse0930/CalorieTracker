package com.pulse0930.calorietracker.ui.calories

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.pulse0930.calorietracker.databinding.CaloriesFragmentBinding

class CaloriesFragment : Fragment() {

    private lateinit var caloriesViewModel: CaloriesViewModel
    private var _binding: CaloriesFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        caloriesViewModel = ViewModelProvider(this).get(CaloriesViewModel::class.java)

        _binding = CaloriesFragmentBinding.inflate(inflater,container,false)

        val root: View = binding.root

        val textView: TextView = binding.textViewInfoCalorieBurntToday
        caloriesViewModel.text.observe(viewLifecycleOwner, Observer {
            if (textView != null) {
                textView.text = it
            }
        })
        
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}