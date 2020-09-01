package com.verma.casalarestaurant.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.squareup.picasso.Picasso
import com.verma.casalarestaurant.R
import com.verma.casalarestaurant.activity.RestaurantDetailActivity
import com.verma.casalarestaurant.database.RestDatabase
import com.verma.casalarestaurant.database.RestEntity
import com.verma.casalarestaurant.model.Restaurants

class AllRestaurantsAdapter(private var restaurants: ArrayList<Restaurants>, val context: Context) :
    RecyclerView.Adapter<AllRestaurantsAdapter.AllRestaurantsViewHolder>() {

    class AllRestaurantsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val resThumbnail = view.findViewById(R.id.imgRestaurantThumbnail) as ImageView
        val restaurantName = view.findViewById(R.id.txtRestaurantName) as TextView
        val rating = view.findViewById(R.id.txtRestaurantRating) as TextView
        val cost = view.findViewById(R.id.txtCostForTwo) as TextView
        val cardRestaurant = view.findViewById(R.id.cardRestaurant) as CardView
        val image = view.findViewById(R.id.imgIsFav) as ImageView
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AllRestaurantsViewHolder {
        val itemView= LayoutInflater.from(p0.context)
            .inflate(R.layout.restaurants_custom_row, p0,false)
        return AllRestaurantsViewHolder(itemView)
    }

    override fun getItemCount(): Int{
        return restaurants.size
      }


    override fun onBindViewHolder(p0: AllRestaurantsViewHolder, p1: Int) {
        val resObject =restaurants[p1]

        p0.restaurantName.text=resObject.name
        p0.cost.text=resObject.costForOne.toString()
        p0.rating.text=resObject.rating
        Picasso.get().load(resObject.imageUrl).error(R.drawable.res_image).into(p0.resThumbnail)


        val listOfFavourites = GetAllFavAsyncTask(context).execute().get()

        if (listOfFavourites.isNotEmpty() && listOfFavourites.contains(resObject.id.toString())) {
            p0.image.setImageResource(R.drawable.ic_action_fav)
        } else {
            p0.image.setImageResource(R.drawable.ic_action_favr)
        }

        p0.image.setOnClickListener {
            val restEntity = RestEntity(
                resObject.id,
                resObject.name,
                resObject.rating,
                resObject.costForOne,
                resObject.imageUrl
            )

            if (!DBAsyncTask(context, restEntity, 1).execute().get()) {
                val async =
                    DBAsyncTask(context, restEntity, 2).execute()
                val result = async.get()
                if (result) {
                    p0.image.setImageResource(R.drawable.ic_action_fav)
                }
            } else {
                val async = DBAsyncTask(context, restEntity, 3).execute()
                val result = async.get()

                if (result) {
                    p0.image.setImageResource(R.drawable.ic_action_favr)
                }
            }
        }



        p0.cardRestaurant.setOnClickListener {
           val intent = Intent(context, RestaurantDetailActivity::class.java)
          intent.putExtra("res_id", resObject.id.toString())
            intent.putExtra("res_name", resObject.name)
            context.startActivity(intent)
        }
    }





    class DBAsyncTask(context: Context, val restaurantEntity: RestEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, RestDatabase::class.java, "rest-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {

                1 -> {
                    val res: RestEntity? =
                        db.restDao().getRestByID(restaurantEntity.rest_id.toString())
                    db.close()
                    return res != null
                }

                2 -> {
                    db.restDao().insertRest(restaurantEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.restDao().deleteRest(restaurantEntity)
                    db.close()
                    return true
                }
            }

            return false
        }

    }

    class GetAllFavAsyncTask(
        context: Context
    ) :
        AsyncTask<Void, Void, List<String>>() {

        val db = Room.databaseBuilder(context, RestDatabase::class.java, "rest-db").build()
        override fun doInBackground(vararg params: Void?): List<String> {

            val list = db.restDao().getAllRests()
            val listOfIds = arrayListOf<String>()
            for (i in list) {
                listOfIds.add(i.rest_id.toString())
            }
            return listOfIds
        }
    }


}