package com.verma.casalarestaurant.fragments

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.verma.casalarestaurant.R
import com.verma.casalarestaurant.adapter.AllRestaurantsAdapter
import com.verma.casalarestaurant.database.RestDatabase
import com.verma.casalarestaurant.database.RestEntity
import com.verma.casalarestaurant.model.Restaurants

/**
 * A simple [Fragment] subclass.
 */
class FavouritesFragment : Fragment() {

    private lateinit var recyclerRestaurant: RecyclerView
    private lateinit var allRestaurantsAdapter: AllRestaurantsAdapter
    private var restaurantList = arrayListOf<Restaurants>()
    private lateinit var rlLoading: RelativeLayout
    private lateinit var rlFav: RelativeLayout
    private lateinit var rlNoFav: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_favourites, container, false)

        rlFav = view.findViewById(R.id.rlFavorites)
        rlNoFav = view.findViewById(R.id.rlNoFavorites)
        rlLoading = view.findViewById(R.id.rlLoading)
        rlLoading.visibility = View.VISIBLE
        setUpRecycler(view)


        return view
           }


    private fun setUpRecycler(view: View) {
        recyclerRestaurant = view.findViewById(R.id.recyclerRestaurantsCart)


        val backgroundList = FavouritesAsync(activity as Context).execute().get()
        if (backgroundList.isEmpty()) {
            rlLoading.visibility = View.GONE
            rlFav.visibility = View.GONE
            rlNoFav.visibility = View.VISIBLE
        } else {
            rlFav.visibility = View.VISIBLE
            rlLoading.visibility = View.GONE
            rlNoFav.visibility = View.GONE
            for (i in backgroundList) {
                restaurantList.add(
                    Restaurants(
                        i.rest_id,
                        i.restName,
                        i.restRating,
                        i.restCostforOne,
                        i.restImage
                    )
                )

            }
                allRestaurantsAdapter = AllRestaurantsAdapter(restaurantList, activity as Context)
                val mLayoutManager = LinearLayoutManager(activity)
                recyclerRestaurant.layoutManager = mLayoutManager
                recyclerRestaurant.itemAnimator = DefaultItemAnimator()
                recyclerRestaurant.adapter = allRestaurantsAdapter
                recyclerRestaurant.setHasFixedSize(true)
            }

    }


    /*A new async class for fetching the data from the DB*/
    class FavouritesAsync(context: Context) : AsyncTask<Void, Void, List<RestEntity>>() {

        val db = Room.databaseBuilder(context, RestDatabase::class.java, "rest-db").build()

        override fun doInBackground(vararg params: Void?): List<RestEntity> {

            return db.restDao().getAllRests()
        }

    }



}
