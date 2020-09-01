package com.verma.casalarestaurant.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText

import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.verma.casalarestaurant.R
import com.verma.casalarestaurant.util.ConnectionManager
import com.verma.casalarestaurant.util.REGISTER
import org.json.JSONObject
import java.io.ObjectInputValidation
import java.lang.Exception


class RegistrationActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var btnRegister: Button
    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etPhoneNumber: EditText
    lateinit var etAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var validation: ObjectInputValidation

    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)


        toolbar = findViewById(R.id.toolbar)
        btnRegister=findViewById(R.id.btnRegister)
        etName=findViewById(R.id.etName)
        etEmail=findViewById(R.id.etEmail)
        etPhoneNumber=findViewById(R.id.etPhoneNumber)
        etAddress=findViewById(R.id.etAddress)
        etPassword=findViewById(R.id.etPassword)
        etConfirmPassword=findViewById(R.id.etConfirmPassword)


        sharedPreferences=getSharedPreferences(getString(R.string.prefrence_file_name), Context.MODE_PRIVATE)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)





        btnRegister.setOnClickListener {
            sharedPreferences.edit().putString("Data", "Register").apply()
            val queue = Volley.newRequestQueue(this@RegistrationActivity)


            val jsonParams = JSONObject()
            jsonParams.put("name", etName.text.toString())
            jsonParams.put("mobile_number", etPhoneNumber.text.toString())
            jsonParams.put("password", etPassword.text.toString())
            jsonParams.put("address", etAddress.text.toString())
            jsonParams.put("email", etEmail.text.toString())



            if (ConnectionManager().checkConnectivity(this@RegistrationActivity)) {


                if(etPassword.text.toString()==etConfirmPassword.text.toString()){
                    if (etPassword.text.toString().length>3) {
                        if (etName.text.toString().length>3) {
                            if (etEmail.text.toString().isNotEmpty()
                                &&etPhoneNumber.text.toString().isNotEmpty()
                                && etAddress.text.toString().isNotEmpty()
                                && etPassword.text.toString().isNotEmpty()) {

                                if (etEmail.text.toString().contains('@')) {


                                    val jsonRequest =
                                        object : JsonObjectRequest(
                                            Request.Method.POST,
                                            REGISTER,
                                            jsonParams,
                                            Response.Listener {

                                                try {

                                                    val data = it.getJSONObject("data")
                                                    val success = data.getBoolean("success")
                                                    if (success) {
                                                        val resJsonObject =
                                                            data.getJSONObject("data")
                                                       /*resJsonObject.getString("user_id").toInt()
                                                        resJsonObject.getString("name")
                                                        resJsonObject.getString("email")
                                                        resJsonObject.getString("mobile_number")
                                                        resJsonObject.getString("address")


                                                       */

                                                        sharedPreferences.edit().putString(
                                                            "userId",
                                                            resJsonObject.getString("user_id")
                                                        ).apply()
                                                        sharedPreferences.edit().putString(
                                                            "Name",
                                                            resJsonObject.getString("name")
                                                        ).apply()
                                                        sharedPreferences.edit().putString(
                                                            "Email",
                                                            resJsonObject.getString("email")
                                                        ).apply()
                                                        sharedPreferences.edit().putString(
                                                            "MobileNumber",
                                                            resJsonObject.getString("mobile_number")
                                                        ).apply()
                                                        sharedPreferences.edit().putString(
                                                            "Address",
                                                            resJsonObject.getString("address")
                                                        ).apply()


                                                        val intent =
                                                            Intent(this@RegistrationActivity, HomeActivity::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    } else {
                                                        Toast.makeText(
                                                            this@RegistrationActivity,
                                                            "Some Error Occurred!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }

                                                } catch (e: Exception) {
                                                    Toast.makeText(
                                                        this@RegistrationActivity,
                                                        "Some error occurred!!!!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }

                                            },
                                            Response.ErrorListener {

                                                Toast.makeText(
                                                    this@RegistrationActivity,
                                                    "Volley Error $it",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()

                                            }) {
                                            override fun getHeaders(): MutableMap<String, String> {
                                                val headers = HashMap<String, String>()
                                                headers["Content-type"] = "application/json"
                                                headers["token"] = "b0e9e492392fa2"
                                                return headers
                                            }
                                        }

                                    queue.add(jsonRequest)



                                }else{
                                    Toast.makeText(this@RegistrationActivity,
                                        "Please Enter Correct Email",
                                        Toast.LENGTH_SHORT).show()

                                }
                            }else{
                                Toast.makeText(this@RegistrationActivity,
                                    "All information must be filled",
                                    Toast.LENGTH_SHORT).show()

                            }
                        }else{
                            Toast.makeText(this@RegistrationActivity,
                                "Name is too Short",
                                Toast.LENGTH_SHORT).show()

                        }
                    }else{
                        Toast.makeText(this@RegistrationActivity,
                            "password length too short",
                            Toast.LENGTH_SHORT).show()

                    }
                }else{
                    Toast.makeText(this@RegistrationActivity,
                        "password mismatch",
                        Toast.LENGTH_SHORT).show()
                }

            } else {
                val dialog = AlertDialog.Builder(this@RegistrationActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                }

                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@RegistrationActivity)
                }
                dialog.create()
                dialog.show()
            }


        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
