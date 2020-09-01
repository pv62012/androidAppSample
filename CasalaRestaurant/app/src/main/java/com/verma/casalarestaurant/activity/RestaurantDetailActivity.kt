package com.verma.casalarestaurant.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.verma.casalarestaurant.R
import com.verma.casalarestaurant.adapter.RestaurantDetailAdapter
import com.verma.casalarestaurant.database.OrderEntity
import com.verma.casalarestaurant.database.RestDatabase
import com.verma.casalarestaurant.database.RestEntity
import com.verma.casalarestaurant.model.RestaurantDetail
import com.verma.casalarestaurant.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class RestaurantDetailActivity : AppCompatActivity() {


    lateinit var recyclerViewMenu: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapterMenu: RestaurantDetailAdapter
    val restFoodList = arrayListOf<RestaurantDetail>()
    var orderList = arrayListOf<RestaurantDetail>()
    lateinit var toolbarDetail:Toolbar
    lateinit var btnProceedToCart: Button
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout

    lateinit var imgFav : ImageView

    var resId : String?="100"
    var resName: String?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        recyclerViewMenu = findViewById(R.id.recyclerRestaurantsMenu)

        progressBar = findViewById(R.id.progressBarMenu)
        progressLayout = findViewById(R.id.progressLayoutMenu)
        progressLayout.visibility = View.VISIBLE

        btnProceedToCart=findViewById(R.id.btnProceedToCart)

        btnProceedToCart.visibility=View.GONE

        imgFav = findViewById(R.id.topFav)
        //    txtSerialNo=findViewById(R.id.txtSerialNo)


        toolbarDetail = findViewById(R.id.toolbarDetail)



        if (intent != null) {
            resId = intent.getStringExtra("res_id")
            resName = intent.getStringExtra("res_name")
        } else {
            finish()
            Toast.makeText(
                this@RestaurantDetailActivity,
                "Some unexpected error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (resId == "100" || resName == null) {
            finish()
            Toast.makeText(
                this@RestaurantDetailActivity,
                "Some unexpected error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }

        setSupportActionBar(toolbarDetail)
        supportActionBar?.title = resName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val async = GetAllFavAsyncTask(this).execute()
        val result = async.get()

        if (resId in result) {
            imgFav.setImageResource(R.drawable.ic_action_fav)
        } else {
            imgFav.setImageResource(R.drawable.ic_action_favr)
        }

        btnProceedToCart.setOnClickListener {
            proceedToCart()
        }


        var url = "http://13.235.250.119/v2/restaurants/fetch_result/$resId"

        val queue = Volley.newRequestQueue(this@RestaurantDetailActivity)

        if (ConnectionManager().checkConnectivity(this@RestaurantDetailActivity)) {
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET,
                url,
                null,
                Response.Listener<JSONObject> {

                    try {
                        progressLayout.visibility = View.GONE
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val restArray = data.getJSONArray("data")
                            for (i in 0 until restArray.length()) {
                                val restObject = restArray.getJSONObject(i)
                                val food = RestaurantDetail(
                                    restObject.getString("id"),
                                    restObject.getString("name"),
                                    restObject.getString("cost_for_one"),
                                    restObject.getString("restaurant_id")
                                )
                                restFoodList.add(food)


                                recyclerAdapterMenu =
                                    RestaurantDetailAdapter(
                                        this@RestaurantDetailActivity,
                                                restFoodList,
                                        object : RestaurantDetailAdapter.OnItemClickListener {
                                            override fun onAddItemClick(foodItem: RestaurantDetail) {
                                                orderList.add(foodItem)
                                                if (orderList.size > 0) {
                                                    btnProceedToCart.visibility = View.VISIBLE
                                                    RestaurantDetailAdapter.isCartEmpty = false
                                                }
                                            }

                                            override fun onRemoveItemClick(foodItem: RestaurantDetail) {
                                                orderList.remove(foodItem)
                                                if (orderList.isEmpty()) {
                                                    btnProceedToCart.visibility = View.GONE
                                                    RestaurantDetailAdapter.isCartEmpty = true
                                                }
                                            }
                                        }
                                    )

                                val mLayoutManager = LinearLayoutManager(this@RestaurantDetailActivity)

                                recyclerViewMenu.layoutManager= mLayoutManager
                                recyclerViewMenu.itemAnimator=DefaultItemAnimator()
                                recyclerViewMenu.adapter = recyclerAdapterMenu

                            }
                        } else {
                            Toast.makeText(
                                this@RestaurantDetailActivity,
                                "Some Error Occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@RestaurantDetailActivity,
                            "Some Error Occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(
                        this@RestaurantDetailActivity,
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
            val dialog = AlertDialog.Builder(this@RestaurantDetailActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                this@RestaurantDetailActivity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@RestaurantDetailActivity as Activity)
            }
            dialog.create()
            dialog.show()
        }


    }
    override fun onSupportNavigateUp(): Boolean {
       onBackPressed()
        return true
    }


//this is for favourites
    class GetAllFavAsyncTask(
        context: Context
    ) :
        AsyncTask<Void, Void, List<String>>() {

        private val db = Room.databaseBuilder(context, RestDatabase::class.java, "rest-db").build()
        override fun doInBackground(vararg params: Void?): List<String> {

            val list = db.restDao().getAllRests()
            val listOfIds = arrayListOf<String>()
            for (i in list) {
                listOfIds.add(i.rest_id.toString())
            }
            return listOfIds
        }
    }

    private fun proceedToCart() {

        val gson = Gson()

        val orderItems = gson.toJson(orderList)

        val async=CartItems(this@RestaurantDetailActivity,resId.toString(),orderItems,1).execute()
        val result=async.get()

        if(result)
        {
            val bundle=Bundle()
            bundle.putString("restaurant_name",resName )
            bundle.putString("restaurant_id",resId)
            val intent= Intent(this@RestaurantDetailActivity,CartActivity::class.java)
            intent.putExtra("Detail",bundle)
            startActivity(intent)

        }
        else{
            Toast.makeText(this@RestaurantDetailActivity, "Some unexpected error", Toast.LENGTH_SHORT).show()
        }


    }



 class CartItems(val context: Context, private val resId: String, private val orderItems: String, private val mode: Int) :
     AsyncTask<Void, Void, Boolean>() {

     private val db= Room.databaseBuilder(context, RestDatabase::class.java,"rest-db").build()

     override fun doInBackground(vararg params: Void?): Boolean {
         when(mode)
         {
             1->{
                 db.orderDao().insertOrder(OrderEntity(resId,orderItems))
                 db.close()
                 return true
             }
             2->
             {
                 db.orderDao().deleteOrder(OrderEntity(resId,orderItems))
                 db.close()
                 return true
             }

         }

         return false
     }
 }
}
        


