package com.verma.casalarestaurant.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.verma.casalarestaurant.R

class ProfileFragment : Fragment() {

    lateinit var name: TextView
    lateinit var phoneNumber: TextView
    lateinit var email: TextView
    lateinit var address : TextView
    lateinit var sharedPreferences: SharedPreferences



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_profile, container, false)

        name=view.findViewById(R.id.txtUserName)
        phoneNumber=view.findViewById(R.id.txtPhone)
        email=view.findViewById(R.id.txtEmail)
        address=view.findViewById(R.id.txtAddress)

        sharedPreferences =
            this.activity?.getSharedPreferences(getString(R.string.prefrence_file_name), Context.MODE_PRIVATE)!!

        val data=sharedPreferences.getString("Data","null")

        if (data=="Register"||data=="Login") {

            name.text = sharedPreferences.getString("Name", "My name")
            email.text = sharedPreferences.getString("Email", "Email")
            phoneNumber.text = "+91-${sharedPreferences.getString("MobileNumber", "+91-0000000")}"
            address.text = sharedPreferences.getString("Address", "address")
        }

        return view
    }

}
