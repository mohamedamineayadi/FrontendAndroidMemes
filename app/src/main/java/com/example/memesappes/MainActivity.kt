package com.example.memesappes

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import androidx.fragment.app.Fragment
import com.example.memesappes.models.Meme
import com.example.memesappes.utils.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var bottom_nav: BottomNavigationView;
    lateinit var mSharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.app_barMemesApp)
        setSupportActionBar(toolbar)

        mSharedPref = getSharedPreferences(PREF_NAME, AppCompatActivity.MODE_PRIVATE)

        bottom_nav = findViewById(R.id.bottom_nav);
        // Set the listener to handle item selection events
        bottom_nav.setOnItemSelectedListener { item ->
            // Handle the selected item
            when (item.itemId) {
                R.id.home -> {
                    changeFragment(HomeMemesFragment(),"")
                }
                R.id.add -> {
                    println("==>"+mSharedPref.getString(EMAIL, "").toString())
                    println("==>"+mSharedPref.getString(FULLNAME, "").toString())
                    val addMemesFragment = AddMemesFragment.newInstance(
                        mSharedPref.getString(EMAIL, "").toString(),
                        mSharedPref.getString(FULLNAME, "").toString()
                    )
                    changeFragment(addMemesFragment,"")
                    println("add")
                }
                R.id.profile -> {
                    println("profile")
                    val profileMeFragment = ProfileFragment.newInstance(
                        mSharedPref.getString(EMAIL, "").toString(),
                        mSharedPref.getString(FULLNAME, "").toString()
                    )
                    changeFragment(profileMeFragment,"")
                }
            }
            true // Return true to indicate that the item has been handled
        }

        supportFragmentManager.beginTransaction().add(R.id.fragment_container,HomeMemesFragment()).commit()
    }


    private fun changeFragment(fragment: Fragment, name: String) {

        if (name.isEmpty())
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
        else
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack("").commit()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.logoutMenu -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Logout")
                builder.setMessage("Are you sure to logout?")
                builder.setPositiveButton("Yes"){ dialogInterface, which ->
                    getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit().clear().apply()
                    val mainIntent = Intent(this, LoginActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                }
                builder.setNegativeButton("No"){dialogInterface, which ->
                    dialogInterface.dismiss()
                }
                builder.create().show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

}