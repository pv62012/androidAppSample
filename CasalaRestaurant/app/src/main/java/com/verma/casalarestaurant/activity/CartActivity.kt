package com.verma.casalarestaurant.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.verma.casalarestaurant.R
import com.verma.casalarestaurant.adapter.CartAdapter
import com.verma.casalarestaurant.adapter.RestaurantDetailAdapter
import com.verma.casalarestaurant.database.OrderEntity
import com.verma.casalarestaurant.database.RestDatabase
import com.verma.casalarestaurant.model.RestaurantDetail
import com.verma.casalarestaurant.util.PLACE_ORDER
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class CartActivity : AppCompatActivity() {

    lateinit var recyclerCart: RecyclerView
    lateinit var progressLayoutCart: RelativeLayout
    lateinit var progressBarCart: ProgressBar
    lateinit var cartResName: TextView

    lateinit var btnConfirmOrder: Button
    lateinit var rlRecycler: RelativeLayout
    lateinit var toolbar: Toolbar
    lateinit var recyclerAdapterCart: CartAdapter
    var foodList = ArrayList<RestaurantDetail>()
    lateinit var sharedPreferences: SharedPreferences
    var resId: String? = null
    var resName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        progressLayoutCart = findViewById(R.id.progressLayoutCart)
        progressBarCart = findViewById(R.id.progressBarCart)
        rlRecycler = findViewById(R.id.rlRecycler)
        cartResName=findViewById(R.id.txtCartRest)
        recyclerCart = findViewById(R.id.recyclerRestaurantsCart)
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val data = intent.getBundleExtra("Detail")

        resId = data?.getString("restaurant_id")
        resName = data.getString("restaurant_name")


        cartResName.text=resName

        val dbList = RetrieveFood(applicationContext).execute().get()

        for (element in dbList) {
            foodList.addAll(
                Gson().fromJson(element.foodItems, Array<RestaurantDetail>::class.java).asList()
            )
        }
        if (foodList.isEmpty()) {
            rlRecycler.visibility = View.GONE
            progressLayoutCart.visibility = View.VISIBLE
        } else {
            rlRecycler.visibility = View.VISIBLE
            progressLayoutCart.visibility = View.GONE
        }

        if (intent!=null) {
            recyclerAdapterCart = CartAdapter(this@CartActivity, foodList)
            val mLayoutManager = LinearLayoutManager(this@CartActivity)
            recyclerCart.layoutManager = mLayoutManager
            recyclerCart.itemAnimator = DefaultItemAnimator()
            recyclerCart.adapter = recyclerAdapterCart
       //here we start
            var sum = 0
            for (i in 0 until foodList.size) {
                sum += foodList[i].CostForOne.toInt()
            }
            val total = "Place Order(Total: Rs. $sum)"
            btnConfirmOrder.text = total

            btnConfirmOrder.setOnClickListener {
                progressLayoutCart.visibility = View.GONE
                rlRecycler.visibility = View.INVISIBLE
                sharedPreferences =
                    getSharedPreferences("Registration Detail", Context.MODE_PRIVATE)

                val queue = Volley.newRequestQueue(this)

                val userID = sharedPreferences.getString("userId", "Error")

                val jsonParams = JSONObject()
                jsonParams.put("user_id", userID)
                jsonParams.put("restaurant_id", resId)
                var sum = 0
                for (i in 0 until foodList.size) {
                    sum += foodList[i].CostForOne.toInt()
                }
                jsonParams.put("total_cost", sum.toString())
                val foodArray = JSONArray()
                for (i in 0 until foodList.size) {
                    val foodIds = JSONObject()
                    foodIds.put("food_item_id", foodList[i].foodId)
                    foodArray.put(i, foodIds)

                }
                jsonParams.put("food", foodArray)


                val jsonObjectRequest =
                    object : JsonObjectRequest(Method.POST,
                        PLACE_ORDER, jsonParams, Response.Listener {

                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                   ClearDBAsync(applicationContext, resId.toString()).execute()
                                        .get()
                                    RestaurantDetailAdapter.isCartEmpty = true
                                    val dialog=AlertDialog.Builder(this@CartActivity)
                                    dialog.setTitle("Success")
                                    dialog.setMessage("Your order has been Successfully Placed")
                                    dialog.setIcon(R.drawable.ic_action_checked)
                                    dialog.setCancelable(false)
                                    dialog.setPositiveButton("OK") { text, listener ->
                                        val intent= Intent(this@CartActivity, HomeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    dialog.create()
                                    dialog.show()
                                } else {
                                    rlRecycler.visibility = View.VISIBLE
                                    Toast.makeText(
                                        this@CartActivity,
                                        "Some Error Occurred!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                recyclerCart.visibility = View.VISIBLE
                                e.printStackTrace()
                            }
                        }, Response.ErrorListener {

                            recyclerCart.visibility = View.VISIBLE
                            Toast.makeText(this@CartActivity, it.message, Toast.LENGTH_SHORT).show()
                        }) {

                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "b0e9e492392fa2"
                            return headers
                        }

                    }
                queue.add(jsonObjectRequest)

            }
            }

        }

        class RetrieveFood(val context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {

            override fun doInBackground(vararg p0: Void?): List<OrderEntity> {
                val db =
                    Room.databaseBuilder(context, RestDatabase::class.java, "rest-db").build()

                return db.orderDao().getAllOrders()
            }

        }

    class ClearDBAsync(context: Context, private val resId : String) : AsyncTask<Void, Void, Boolean>(){
        val db =Room.databaseBuilder(context, RestDatabase::class.java, "rest-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.orderDao().deleteOrders(resId)
            db.close()
            return true

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        ClearDBAsync(applicationContext, resId.toString()).execute().get()
        RestaurantDetailAdapter.isCartEmpty=true
        onBackPressed()
        return true
    }

}