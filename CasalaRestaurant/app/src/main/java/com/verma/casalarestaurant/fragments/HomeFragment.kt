package com.verma.casalarestaurant.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.verma.casalarestaurant.R
import com.verma.casalarestaurant.adapter.AllRestaurantsAdapter
import com.verma.casalarestaurant.model.Restaurants
import com.verma.casalarestaurant.util.ConnectionManager
import com.verma.casalarestaurant.util.FETCH_RESTAURANTS
import org.json.JSONException
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    lateinit var recyclerViewHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapterHome: AllRestaurantsAdapter
    val restInfoList = arrayListOf<Restaurants>()
    lateinit var progressBarHome: ProgressBar
    lateinit var progressLayoutHome: RelativeLayout




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerViewHome = view.findViewById(R.id.recyclerRestaurantsHome)

        progressBarHome = view.findViewById(R.id.progressBarHome)
        progressLayoutHome = view.findViewById(R.id.progressLayoutHome)
        progressLayoutHome.visibility = View.VISIBLE


        val queue = Volley.newRequestQueue(activity as Context)

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET,
                FETCH_RESTAURANTS,
                null,
                Response.Listener<JSONObject> {

                    try {
                        progressLayoutHome.visibility = View.GONE
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val restArray = data.getJSONArray("data")
                            for (i in 0 until restArray.length()) {
                                val restObject = restArray.getJSONObject(i)
                                val restaurant = Restaurants(
                                    restObject.getString("id").toInt(),
                                    restObject.getString("name"),
                                    restObject.getString("rating"),
                                    restObject.getString("cost_for_one"),
                                    restObject.getString("image_url")

                                )

                                restInfoList.add(restaurant)

                                if (activity!=null) {


                                    recyclerAdapterHome =
                                        AllRestaurantsAdapter(restInfoList, activity as Context)

                                    val mLayoutManager: LinearLayoutManager =
                                        LinearLayoutManager(activity)
                                    recyclerViewHome.layoutManager = mLayoutManager
                                    recyclerViewHome.itemAnimator = DefaultItemAnimator()
                                    recyclerViewHome.adapter = recyclerAdapterHome
                                    recyclerViewHome.setHasFixedSize(true)

                                }

                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some Error Occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(
                        activity as Context,
                        "Poor Internet Connection",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "b0e9e492392fa2"
                    return headers
                }
            }

            queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view
    }
}