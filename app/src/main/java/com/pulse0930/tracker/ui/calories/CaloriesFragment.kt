package com.pulse0930.tracker.ui.calories

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.Task
import com.pulse0930.tracker.databinding.CaloriesFragmentBinding
import com.pulse0930.tracker.util.*
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


const val TAG = "CalorieFragment"
const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE: Int = 100

class CaloriesFragment : Fragment() {
    private lateinit var caloriesViewModel: CaloriesViewModel
    private var _binding: CaloriesFragmentBinding? = null
    private val binding get() = _binding!!
    private fun getGoogleAccount() = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
    private val fitnessOptions: FitnessOptions by lazy {
        FitnessOptions.builder()
            .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .build()
    }
    private val dateFormat = DateFormat.getDateTimeInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        caloriesViewModel = ViewModelProvider(this).get(CaloriesViewModel::class.java)

        _binding = CaloriesFragmentBinding.inflate(inflater, container, false)

        val root: View = binding.root
        caloriesViewModel.textDate.observe(viewLifecycleOwner, {
            binding.textViewDate.text = it
        })
        initializeDaySlotsUI()
        accessGoogleFit()
        return root
    }

    private fun initializeDaySlotsUI() {
        binding.morning.slotNameTextView.text = "Morning"
        binding.morning.slotTimeTextView.text = "9 AM - Noon"
        binding.afternoon.slotNameTextView.text = "Afternoon"
        binding.afternoon.slotTimeTextView.text = "Noon - 4 PM"
        binding.evening.slotNameTextView.text = "Evening"
        binding.evening.slotTimeTextView.text = "4 AM - 9 PM"
        binding.night.slotNameTextView.text = "Night"
        binding.night.slotTimeTextView.text = "9 PM - Midnight"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun accessGoogleFit() {
        if (!GoogleSignIn.hasPermissions(getGoogleAccount(), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                activity, // your activity
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE, // e.g. 1
                getGoogleAccount(),
                fitnessOptions
            )
        } else {
            readGoogleFitData()
            dailyTotalCalorieExpended()
        }
    }

    /**
     * Handles the callback from the OAuth sign in flow, executing the post sign in function
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                    readGoogleFitData()
                    dailyTotalCalorieExpended()
                }
            }
            else -> oAuthErrorMsg(requestCode, resultCode)
        }
    }

    private fun oAuthErrorMsg(requestCode: Int, resultCode: Int) {
        val message = """
            There was an error signing into Fit. Check the troubleshooting section of the README
            for potential issues.
            Request code was: $requestCode
            Result code was: $resultCode
        """.trimIndent()
        Log.e(TAG, message)
    }

    /**
     * Asynchronous task to read the history data. When the task succeeds, it will print out the
     * data.
     */
    private fun readGoogleFitData(): Task<DataReadResponse>? {
        if (!IsBetweenTimeRange(
                getCalendar(8, 59, 59),
                getCalendar(23, 59, 59),
                getCalendarNow()
            )
        ) return null
        val readRequest = queryFitnessData()
        return Fitness.getHistoryClient(activity, getGoogleAccount())
            .readData(readRequest)
            .addOnSuccessListener { dataReadResponse ->
                printData(dataReadResponse)
                updateUI(dataReadResponse)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem reading the data.", e)
            }
    }

    /** Returns a [DataReadRequest] for all step count changes in the past week.  */
    private fun queryFitnessData(): DataReadRequest {
        val calendar = getCalendarNow()
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        if(calendar.get(Calendar.HOUR_OF_DAY).equals(9))
            calendar.add(Calendar.HOUR_OF_DAY,1)
        val endTime = calendar.timeInMillis
        val startTime = getCalendar(9,0,0).timeInMillis

        Log.i(TAG, "Range Start: ${dateFormat.format(startTime)}")
        Log.i(TAG, "Range End: ${dateFormat.format(endTime)}")

        return DataReadRequest.Builder()
            .aggregate(DataType.TYPE_CALORIES_EXPENDED)
            .bucketByTime(1, TimeUnit.HOURS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()
    }

    /**
     * Asynchronous task to read the history data. When the task succeeds, it will print out the
     * data.
     */
    private fun dailyTotalCalorieExpended(): Task<DataSet> {
        return Fitness.getHistoryClient(activity, getGoogleAccount())
            .readDailyTotal(DataType.TYPE_CALORIES_EXPENDED)
            .addOnSuccessListener { dataReadResponse ->
                val dp: DataPoint = dataReadResponse.dataPoints.get(0)
                val calorieBurnt = dp.getValue(dp.dataType.fields.get(0)).asFloat().roundToInt()
                caloriesViewModel.text.observe(viewLifecycleOwner, {
                    binding.textViewInfoCalorieBurntToday.text = it.format(calorieBurnt)
                })
                //dumpDataSet(dataReadResponse)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem reading the data.", e)
            }
    }

    private fun updateUI(dataReadResponse: DataReadResponse) {
        if (dataReadResponse.buckets.isNotEmpty()) {
            var slot = arrayOf(0.0f, 0.0f, 0.0f, 0.0f)
            for (bucket in dataReadResponse.buckets) {
                val dp: DataPoint = bucket.dataSets.get(0).dataPoints.get(0)
                if (arrayOf("09:00:00", "10:00:00", "11:00:00").contains(dp.getStartTimeString())) {
                    slot[0] += dp.getValue(dp.dataType.fields.get(0)).asFloat()
                } else if (arrayOf(
                        "12:00:00",
                        "13:00:00",
                        "14:00:00",
                        "15:00:00"
                    ).contains(dp.getStartTimeString())
                ) {
                    slot[1] += dp.getValue(dp.dataType.fields.get(0)).asFloat()
                } else if (arrayOf(
                        "21:00:00",
                        "22:00:00",
                        "23:00:00"
                    ).contains(dp.getStartTimeString())
                ) {
                    slot[3] += dp.getValue(dp.dataType.fields.get(0)).asFloat()
                } else {
                    slot[2] += dp.getValue(dp.dataType.fields.get(0)).asFloat()
                }
            }
            binding.morning.calorieBurntTextView.text = slot[0].roundToInt().toString() + " kcal"
            binding.afternoon.calorieBurntTextView.text = slot[1].roundToInt().toString() + " kcal"
            binding.evening.calorieBurntTextView.text = slot[3].roundToInt().toString() + " kcal"
            binding.night.calorieBurntTextView.text = slot[2].roundToInt().toString() + " kcal"
        }
    }
}