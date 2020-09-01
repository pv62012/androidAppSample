package com.verma.casalarestaurant.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.verma.casalarestaurant.R
import com.verma.casalarestaurant.adapter.AllRestaurantsAdapter
import com.verma.casalarestaurant.adapter.OrderHistoryRecyclerAdapter
import com.verma.casalarestaurant.model.OrderHistoryDetail
import com.verma.casalarestaurant.model.Restaurants
import com.verma.casalarestaurant.util.ConnectionManager
import com.verma.casalarestaurant.util.FETCH_RESTAURANTS
import org.json.JSONException
import org.json.JSONObject

class HistoryFragment : Fragment() {

    lateinit var llCart: LinearLayout
    lateinit var recyclerRestaurantHistory: RecyclerView
    lateinit var progressLayoutHistory: RelativeLayout
    lateinit var progressBarHistory: ProgressBar
    val orderInfoList = arrayListOf<OrderHistoryDetail>()
    lateinit var orderHistoryAdapter: OrderHistoryRecyclerAdapter
    lateinit var sharedPreferences: SharedPreferences
    lateinit var noOrder: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        sharedPreferences =
            activity?.getSharedPreferences(
                getString(R.string.prefrence_file_name),
                Context.MODE_PRIVATE
            )!!
        llCart = view.findViewById(R.id.llCart)
        noOrder=view.findViewById(R.id.txtNoOrder)
        recyclerRestaurantHistory = view.findViewById(R.id.recyclerRestaurantsHistory)
        progressLayoutHistory = view.findViewById(R.id.progressLayoutHistory)
        progressBarHistory = view.findViewById(R.id.progressBarHistory)
        progressLayoutHistory.visibility = View.VISIBLE

        val userID = sharedPreferences.getString("userId", null)


        val queue = Volley.newRequestQueue(activity as Context)
        var url = "http://13.235.250.119/v2/orders/fetch_result/$userID"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET,
                url,
                null,
                Response.Listener{

                    try {
                        progressLayoutHistory.visibility = View.GONE
                        noOrder.visibility=View.GONE
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val data = data.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val orderJsonObject = data.getJSONObject(i)
                                val foodItems = orderJsonObject.getJSONArray("food_items")
                                val orderDetails = OrderHistoryDetail(
                                    orderJsonObject.getString("order_id"),
                                    orderJsonObject.getString("restaurant_name"),
                                    orderJsonObject.getString("order_placed_at"),
                                    foodItems

                                )

                                orderInfoList.add(orderDetails)

                                orderHistoryAdapter =
                                    OrderHistoryRecyclerAdapter(activity as Context, orderInfoList)
                                val mLayoutManager: LinearLayoutManager =
                                    LinearLayoutManager(activity)
                                recyclerRestaurantHistory.layoutManager = mLayoutManager
                                recyclerRestaurantHistory.itemAnimator = DefaultItemAnimator()
                                recyclerRestaurantHistory.adapter = orderHistoryAdapter
                                recyclerRestaurantHistory.setHasFixedSize(true)
                                if (orderInfoList.isEmpty()){
                                    noOrder.visibility=View.VISIBLE
                                }else{
                                    noOrder.visibility=View.INVISIBLE
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
                        "Volley Error Occurred!!!",
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

