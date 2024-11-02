package com.example.navdrawerkotpractice

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.navdrawerkotpractice.databinding.ActivityLoginBinding // Assurez-vous que ce chemin est correct

class activity_login : AppCompatActivity() { // Corrigez le nom de la classe
    private lateinit var binding: ActivityLoginBinding // Déclarez la variable binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater) // Utilisez le bon binding
        setContentView(binding.root)





    }
}
