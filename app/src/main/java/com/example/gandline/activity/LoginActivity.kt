package com.example.gandline.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.content.edit
import com.example.gandline.LibConstants
import com.example.gandline.R
import com.example.gandline.http.AminoRequest
import com.example.gandline.utils.Serialization
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailInput = findViewById<TextInputEditText>(R.id.emailEditText)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordEditText)
        val button = findViewById<Button>(R.id.loginSubmitButton)

        val emailLay = findViewById<TextInputLayout>(R.id.email_lay)
        val passwordLay = findViewById<TextInputLayout>(R.id.password_lay)

        button.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (passwordInput.text!!.isEmpty() and emailInput.text!!.isEmpty()) {
                passwordLay.error = "Please enter a password"
                emailLay.error = "Please enter a email"
            } else if (emailInput.text!!.isEmpty()) {
                emailLay.error = "Please enter a email"
            } else if (passwordInput.text!!.isEmpty()) {
                passwordLay.error = "Please enter a password"
            } else {

                AminoRequest.initRequest("POST", "/g/s/auth/login", this)
                    .async()
                    .addBody(Serialization.createAuthBody(email, password))
                    .send {
                        val account = Serialization.extractAccount(
                            it,
                            email,
                            password,
                            LibConstants.DEFAULT_DEVICE_ID
                        )
                        Toast.makeText(this,
                            "Авторизован как ${account.nickname}",
                            Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        AminoRequest.sid = "sid=${account.sid}"
                        AminoRequest.uid = account.uid
                        getSharedPreferences("account", Context.MODE_PRIVATE).edit {
                            putBoolean("authorized", true)
                            putString("email", email)
                            putString("password", password)
                            putString("nickname", account.nickname)
                            putString("sid", AminoRequest.sid)
                            putString("uid", AminoRequest.uid)
                        }
                    }
            }
        }
    }
}