package com.pulse0930.tracker.ui.profile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pulse0930.tracker.MainActivity
import com.pulse0930.tracker.R
import com.pulse0930.tracker.databinding.ProfileFragmentBinding
import com.pulse0930.tracker.databinding.ProfileParametersCardviewBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private var _binding: ProfileFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun getGoogleAccount() = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
    private val fitnessOptions: FitnessOptions by lazy {
        FitnessOptions.builder()
            .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .build()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = ProfileFragmentBinding.inflate(inflater, container, false)

        val root: View = binding.root
        val textView: TextView = binding.textProfile
        textView.text =getGoogleAccount().givenName+" "+getGoogleAccount().familyName
        Picasso.get().load(getGoogleAccount().photoUrl.toString()).into(binding.imageView)
        binding.signOutButton.setOnClickListener {
            (activity as MainActivity?)?.signOut()
        }

        addProfileInfo("Height", "172 cm")
        addProfileInfo("Weight", "68 kg")
        addProfileInfo("DOB", "01 Jan 1999")
        addProfileInfo("Gender", "Male")
        return root
    }

    private fun addProfileInfo(profileParameter: String, value: String) {
        val profileInfoLayout: LinearLayout = binding.profileInfoLayout
        val view = layoutInflater.inflate(R.layout.profile_parameters_cardview, null)
        var profileParametersCardviewBinding = ProfileParametersCardviewBinding.bind(view)
        profileParametersCardviewBinding.profileInfoTitle.setText(profileParameter)
        profileParametersCardviewBinding.profileInfoValue.setText(value)
        profileInfoLayout.addView(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}