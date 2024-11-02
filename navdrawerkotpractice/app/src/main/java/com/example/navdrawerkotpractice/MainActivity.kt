package com.example.navdrawerkotpractice

import com.bumptech.glide.Glide
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.example.navdrawerkotpractice.fragments.HomeFragment
import com.example.navdrawerkotpractice.fragments.SettingsFragment
import com.example.navdrawerkotpractice.fragments.AboutFragment
import com.example.navdrawerkotpractice.fragments.ShareFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navHeaderView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                .replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    private fun displayUserInfo() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "Nom par défaut") ?: "Nom par défaut"
        val userEmail = sharedPreferences.getString("userEmail", "Email par défaut") ?: "Email par défaut"
        val picPath = sharedPreferences.getString("picPath", null) // Récupération du picPath

        val userNameTextView = navHeaderView.findViewById<TextView>(R.id.user_name)
        val userEmailTextView = navHeaderView.findViewById<TextView>(R.id.user_email)
        val avatarImageView = navHeaderView.findViewById<ImageView>(R.id.user_avatar)

        val fullPicPath = "http://192.168.0.182/LivreApp/" + picPath

        Log.d("pathimage", "path: ${fullPicPath}")

        userNameTextView.text = userName
        userEmailTextView.text = userEmail

        // Si picPath existe, charger l'image depuis le chemin, sinon utiliser l'avatar par défaut
        if (!picPath.isNullOrEmpty()) {
            Glide.with(this)
                .load(fullPicPath)
                .error(R.drawable.profile) // Image par défaut en cas d'erreur
                .into(avatarImageView)
        } else {
            // Utiliser les initiales comme avatar par défaut
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
            R.id.nav_home -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            R.id.nav_settings -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment()).commit()
            R.id.nav_share -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ShareFragment()).commit()
            R.id.nav_about -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AboutFragment()).commit()
            R.id.nav_logout -> {
                logout()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout() {
        // Supprimer user_prefs
        val userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        userPrefs.edit().clear().apply()

        // Supprimer Favorites
        val favoritesPrefs = getSharedPreferences("Favorites", MODE_PRIVATE)
        favoritesPrefs.edit().clear().apply()

        // Supprimer BookRatings
        val ratingPrefs = getSharedPreferences("BookRatings", MODE_PRIVATE)
        ratingPrefs.edit().clear().apply()

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