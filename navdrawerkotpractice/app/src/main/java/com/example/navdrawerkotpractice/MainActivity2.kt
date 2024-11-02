package com.example.navdrawerkotpractice

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.util.Log
import com.example.navdrawerkotpractice.fragments.AddBookFragment
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.example.navdrawerkotpractice.fragments.RequestsFragment
import com.example.navdrawerkotpractice.fragments.AdminProfileFragment
import com.example.navdrawerkotpractice.fragments.ListBookFragment
import androidx.fragment.app.Fragment
import com.example.navdrawerkotpractice.fragments.ShareFragment
import android.widget.ImageView
import com.bumptech.glide.Glide

class MainActivity2 : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navHeaderView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2) // Créez un nouveau layout pour l'activité admin

        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navHeaderView = navigationView.getHeaderView(0)
        displayUserInfo()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddBookFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_add_book)
        }
    }

    private fun displayUserInfo() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "Admin") ?: "Admin"
        val userEmail = sharedPreferences.getString("userEmail", "admin@example.com") ?: "admin@example.com"
        val picPath = sharedPreferences.getString("picPath", null) // Retrieve picPath

        // Logguez les données
        Log.d("infolocal", "User Name: $userName")
        Log.d("infolocal", "User Email: $userEmail")

        val userNameTextView = navHeaderView.findViewById<TextView>(R.id.user_name)
        val userEmailTextView = navHeaderView.findViewById<TextView>(R.id.user_email)
        val avatarImageView = navHeaderView.findViewById<ImageView>(R.id.user_avatar)

        val fullPicPath = "http://192.168.0.182/LivreApp/" + picPath

        Log.d("pathimage", "path: ${fullPicPath}")

        userNameTextView.text = userName
        userEmailTextView.text = userEmail

        // If picPath exists, load image from the path, otherwise use default avatar
        if (!picPath.isNullOrEmpty()) {
            Glide.with(this)
                .load(fullPicPath)
                .error(R.drawable.profile) // Default image in case of error
                .into(avatarImageView)
        } else {
            // Use initials as default avatar
            val initials = userName
            val avatarUrl = "https://ui-avatars.com/api/?name=$initials"
            Glide.with(this)
                .load(avatarUrl)
                .error(R.drawable.profile)
                .into(avatarImageView)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_add_book -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddBookFragment()).commit()
            R.id.nav_requests -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RequestsFragment()).commit()
            R.id.nav_profile -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ShareFragment()).commit()
            R.id.nav_liste_book -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ListBookFragment()).commit()
            R.id.nav_logout -> logout()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    private fun logout() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
        val intent = Intent(this, ActivityLogin::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
