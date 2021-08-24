package com.pulse0930.calorietracker.ui.calories

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import com.pulse0930.calorietracker.R
import com.pulse0930.calorietracker.databinding.CaloriesFragmentBinding
import com.pulse0930.calorietracker.databinding.DaySlotCardviewBinding

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

        val textViewInfoCalorieBurntToday: TextView = binding.textViewInfoCalorieBurntToday
        caloriesViewModel.text.observe(viewLifecycleOwner, Observer {
            textViewInfoCalorieBurntToday.text = it
        })
        addDaySlot("Morning", "09:00-12:00","100")
        addDaySlot("Afternoon", "09:00-12:00","120")
        addDaySlot("Evening", "09:00-12:00","140")
        addDaySlot("Night", "09:00-12:00","230")
        return root
    }
    fun addDaySlot(slotName:String?,slotTime:String?,calorieBurnt:String?){
        val daySlotLayout: LinearLayout = binding.daySlotLayout
        val view = layoutInflater.inflate(R.layout.day_slot_cardview, null)
        var daySlotCardviewBinding = DaySlotCardviewBinding.bind(view)
        daySlotCardviewBinding.slotNameTextView.setText(slotName)
        daySlotCardviewBinding.slotTimeTextView.setText(slotTime)
        daySlotCardviewBinding.calorieBurntTextView.setText(calorieBurnt)
        daySlotLayout.addView(view)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}