package com.pulse0930.tracker.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.Task
import com.pulse0930.tracker.MainActivity
import com.pulse0930.tracker.R
import com.pulse0930.tracker.databinding.ProfileFragmentBinding
import com.pulse0930.tracker.ui.calories.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE
import com.pulse0930.tracker.ui.calories.TAG
import com.pulse0930.tracker.util.printData
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!

    private fun getGoogleAccount() = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
    private val fitnessOptions: FitnessOptions by lazy {
        FitnessOptions.builder()
            .addDataType(DataType.AGGREGATE_HEIGHT_SUMMARY, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_WEIGHT_SUMMARY, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
            .build()
    }
    private val dateFormat = DateFormat.getDateTimeInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = ProfileFragmentBinding.inflate(inflater, container, false)

        val root: View = binding.root
        val textView: TextView = binding.textProfile
        textView.text = getGoogleAccount().givenName + " " + getGoogleAccount().familyName
        Picasso.get().load(getGoogleAccount().photoUrl.toString()).into(binding.imageView)
        binding.signOutButton.setOnClickListener {
            (activity as MainActivity?)?.signOut()
        }
        initializeProfileUI()
        accessGoogleFit()
        return root
    }

    private fun initializeProfileUI() {
        binding.profileInfoLayout.getChildAt(0).findViewById<TextView>(R.id.profile_info_title).text = "Height"
        binding.profileInfoLayout.getChildAt(1).findViewById<TextView>(R.id.profile_info_title).text = "Weight"
        binding.profileInfoLayout.getChildAt(2).findViewById<TextView>(R.id.profile_info_title).text = "DOB"
        binding.profileInfoLayout.getChildAt(3).findViewById<TextView>(R.id.profile_info_title).text = "Gender"
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
    private fun readGoogleFitData(): Task<DataReadResponse> {
        val readRequest = queryFitnessData()
        return Fitness.getHistoryClient(activity, getGoogleAccount())
            .readData(readRequest)
            .addOnSuccessListener { dataReadResponse ->
                printData(dataReadResponse)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem reading the data.", e)
            }
    }

    /** Returns a [DataReadRequest] for all step count changes in the past week.  */
    private fun queryFitnessData(): DataReadRequest {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"))
        return DataReadRequest.Builder()
            .read(DataType.TYPE_WEIGHT)
            .read(DataType.TYPE_HEIGHT)
            .setTimeRange(1, calendar.timeInMillis, TimeUnit.MILLISECONDS)
            .setLimit(1)
            .build()
    }
}