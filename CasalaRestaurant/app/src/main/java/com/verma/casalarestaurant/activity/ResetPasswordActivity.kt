package com.verma.casalarestaurant.activity

import android.app.AlertDialog
import android.content.Intent
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
import com.verma.casalarestaurant.util.RESET_PASSWORD
import org.json.JSONObject
import java.lang.Exception

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var btnSubmit: Button
    lateinit var etOtp: EditText
    lateinit var etNewPassword:  EditText
    lateinit var etConfirmPassword: EditText
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Reset Password"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnSubmit=findViewById(R.id.btnSubmit)
        etOtp=findViewById(R.id.etOTP)
        etNewPassword=findViewById(R.id.etNewPassword)
        etConfirmPassword=findViewById(R.id.etConfirmPassword)
        val detail=intent.getBundleExtra("Detail")
        val mobile= detail.getString("Mobile")


        btnSubmit.setOnClickListener {

        val jsonParams = JSONObject()

        jsonParams.put("mobile_number", mobile.toString())
        jsonParams.put("password", etNewPassword.text.toString())
            jsonParams.put("otp", etOtp.text.toString())


        if (ConnectionManager().checkConnectivity(this@ResetPasswordActivity)) {


            val queue = Volley.newRequestQueue(this@ResetPasswordActivity)
            if (etOtp.text.toString().isNotEmpty()
                && etNewPassword.text.toString().isNotEmpty()) {
                if (etNewPassword.text.toString()==etConfirmPassword.text.toString()) {


                    val jsonRequest =
                        object : JsonObjectRequest(
                            Request.Method.POST,
                            RESET_PASSWORD,
                            jsonParams,
                            Response.Listener {

                                try {

                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")
                                    if (success) {

                                        val message= data.getString("successMessage")

                                        val intent =
                                                Intent(
                                                    this@ResetPasswordActivity,
                                                    LoginActivity::class.java
                                                )
                                        Toast.makeText(this@ResetPasswordActivity,
                                            "${message}",
                                            Toast.LENGTH_SHORT).show()

                                        startActivity(intent)
                                        finish()
                                        } else {

                                            Toast.makeText(
                                                this@ResetPasswordActivity,
                                                "Please Enter Correct OTP",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                } catch (e: Exception) {
                                    Toast.makeText(
                                        this@ResetPasswordActivity,
                                        "Some error occurred!!!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            },
                            Response.ErrorListener {

                                Toast.makeText(
                                    this@ResetPasswordActivity,
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

                    Toast.makeText(this@ResetPasswordActivity,
                        "Password Mismatch",
                        Toast.LENGTH_SHORT).show()

                }

            }else{
                Toast.makeText(this@ResetPasswordActivity,
                    "All information must be filled",
                    Toast.LENGTH_SHORT).show()

            }


        } else {
            val dialog = AlertDialog.Builder(this@ResetPasswordActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
            }

            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@ResetPasswordActivity)
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
