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
import com.verma.casalarestaurant.util.FORGOT_PASSWORD
import org.json.JSONObject
import java.lang.Exception

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var btnNext: Button
    lateinit var etForgotMObile : EditText
    lateinit var etForgotEmail: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Forgot Password"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnNext=findViewById(R.id.btnForgotNext)
        etForgotEmail=findViewById(R.id.etForgotEmail)
        etForgotMObile=findViewById(R.id.etForgotMobile)

        val bundle =Bundle()

        btnNext.setOnClickListener {



            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", etForgotMObile.text.toString())
            jsonParams.put("email", etForgotEmail.text.toString())


            if (ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)) {


                val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
                if (etForgotMObile.text.toString().isNotEmpty()
                    && etForgotEmail.text.toString().isNotEmpty()) {
                    val jsonRequest =
                        object : JsonObjectRequest(
                            Request.Method.POST,
                            FORGOT_PASSWORD,
                            jsonParams,
                            Response.Listener {

                                try {

                                    val data = it.getJSONObject("data")
                                    val success=data.getBoolean("success")
                                    val firstTry= data.getBoolean("first_try")
                                    if (success){
                                        if (firstTry){

                                            bundle.putString("Mobile", etForgotMObile.text.toString())
                                            val intent =
                                                Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                                            intent.putExtra("Detail",bundle)
                                            startActivity(intent)
                                        }else{

                                            Toast.makeText(
                                                this@ForgotPasswordActivity,
                                                "Please use Previous OTP",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                           bundle.putString("Mobile", etForgotMObile.text.toString())
                                            val intent =
                                                Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                                            intent.putExtra("Detail",bundle)
                                            startActivity(intent)
                                        }
                                    }else{
                                        Toast.makeText(
                                            this@ForgotPasswordActivity,
                                            "Check Internet Connection",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        this@ForgotPasswordActivity,
                                        "Some error occurred!!!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            },
                            Response.ErrorListener {

                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Volley Error $it",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                            }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = "9bf534118365f1"
                                return headers
                            }
                        }

                    queue.add(jsonRequest)




                }else{
                    Toast.makeText(this@ForgotPasswordActivity,
                        "All information must be filled",
                        Toast.LENGTH_SHORT).show()

                }


            } else {
                val dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                }

                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@ForgotPasswordActivity)
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