package com.verma.casalarestaurant.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.verma.casalarestaurant.R
import com.verma.casalarestaurant.util.ConnectionManager
import com.verma.casalarestaurant.util.LOGIN
import org.json.JSONObject
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    private lateinit var registerYourself: TextView
    private lateinit var login: Button
    private lateinit var txtForgotPassword: TextView
    lateinit var etMobileNumber: TextView
    lateinit var etPassword: TextView
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        sharedPreferences=getSharedPreferences(getString(R.string.prefrence_file_name), Context.MODE_PRIVATE)
        val isLoggedIn =sharedPreferences.getBoolean("isLoggedIn",false)
        if (isLoggedIn){
            val intent=Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }


        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        registerYourself = findViewById(R.id.txtRegisterYourself)
        login = findViewById(R.id.btnLogin)
        etMobileNumber=findViewById(R.id.etMobileNumber)
        etPassword=findViewById(R.id.etPassword)


        txtForgotPassword.setOnClickListener {

            val intent= Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)


        }
        registerYourself.setOnClickListener {

        val intent=Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intent)

        }


        login.setOnClickListener {
            sharedPreferences.edit().putString("Data", "Login").apply()
            val queue = Volley.newRequestQueue(this@LoginActivity)

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", etMobileNumber.text.toString())
            jsonParams.put("password", etPassword.text.toString())


            if (ConnectionManager().checkConnectivity(this@LoginActivity)) {


                            if (etMobileNumber.text.toString().isNotEmpty()
                                && etPassword.text.toString().isNotEmpty()) {
                                val jsonRequest =
                                        object : JsonObjectRequest(
                                            Request.Method.POST,
                                            LOGIN,
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
                                                            Intent(this@LoginActivity, HomeActivity::class.java)
                                                        startActivity(intent)
                                                        finish()

                                                    } else {
                                                        Toast.makeText(
                                                            this@LoginActivity,
                                                            "Please Enter Correct Number or Password",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }

                                                } catch (e: Exception) {
                                                    Toast.makeText(
                                                        this@LoginActivity,
                                                        "Some error occurred!!!!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }

                                            },
                                            Response.ErrorListener {

                                                Toast.makeText(
                                                    this@LoginActivity,
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
                                Toast.makeText(this@LoginActivity,
                                    "All information must be filled",
                                    Toast.LENGTH_SHORT).show()

                            }


            } else {
                val dialog = AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                }

                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@LoginActivity)
                }
                dialog.create()
                dialog.show()
            }


        }


    }

        }