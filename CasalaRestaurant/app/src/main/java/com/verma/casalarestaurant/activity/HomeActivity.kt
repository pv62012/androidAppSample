package com.verma.casalarestaurant.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.verma.casalarestaurant.R
import com.verma.casalarestaurant.fragments.*

class HomeActivity : AppCompatActivity() {


    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinator: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frame: FrameLayout
    private lateinit var navigationView: NavigationView



    private lateinit var sharedPreferences: SharedPreferences

    private var previousMenuItem :MenuItem? =null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout=findViewById(R.id.drawer_layout)
        coordinator=findViewById(R.id.coordinator)
        toolbar=findViewById(R.id.toolbar)
        frame=findViewById(R.id.frame)
        navigationView=findViewById(R.id.navigation_view)
        drawerLayout

        sharedPreferences=getSharedPreferences(getString(R.string.prefrence_file_name),Context.MODE_PRIVATE)

        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

        val convertView = LayoutInflater.from(this@HomeActivity).inflate(R.layout.drawer_header, null)
        val userName: TextView = convertView.findViewById(R.id.txtDrawerText)
        val userPhone: TextView = convertView.findViewById(R.id.txtDrawerSecondaryText)
        userName.text = sharedPreferences.getString("Name", null)
        val phoneText = "+91-${sharedPreferences.getString("MobileNumber", null)}"
        userPhone.text = phoneText
        navigationView.addHeaderView(convertView)

        setUpToolbar()
        openHome()


        val actionBarDrawerToggle= ActionBarDrawerToggle(
            this@HomeActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem!=null){
                previousMenuItem?.isChecked=false
            }
            it.isCheckable=true
            it.isChecked=true
            previousMenuItem=it

            when(it.itemId){
                R.id.home -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }
                R.id.myProfile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame,
                            ProfileFragment()
                        )
                        .commit()
                    supportActionBar?.title="My Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.favRes -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame,
                            FavouritesFragment()
                        )
                        .commit()

                    supportActionBar?.title="Favourite Restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.faqs -> {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame,
                            FAQFragment()
                        )
                        .commit()

                    supportActionBar?.title="FAQs"
                    drawerLayout.closeDrawers()
                }
                R.id.orderHistory -> {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame,
                            HistoryFragment()
                        )
                        .commit()

                    supportActionBar?.title="My Previous Orders"
                    drawerLayout.closeDrawers()
                }
                R.id.logout -> {
                    /*Creating a confirmation dialog*/
                    val builder = AlertDialog.Builder(this@HomeActivity)
                    builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want exit?")
                        .setPositiveButton("Yes") { _, _ ->

                                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                                sharedPreferences.edit().clear().apply()
                                finish()
                        }
                        .setNegativeButton("No") { _, _ ->
                            openHome()
                        }
                        .create()
                        .show()

                }

            }
            return@setNavigationItemSelectedListener true
        }
    }

    fun openHome(){
        val fragment= HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, HomeFragment())
        transaction.commit()
        navigationView.setCheckedItem(R.id.home)
        supportActionBar?.title="Home"
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)

        }
        return super.onOptionsItemSelected(item)
    }


    private fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="Main"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onBackPressed() {
        val frag= supportFragmentManager.findFragmentById(R.id.frame)
        when(frag){
            !is HomeFragment -> openHome()
            else -> super.onBackPressed()
        }
    }
}

