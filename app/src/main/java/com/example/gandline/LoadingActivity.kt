package com.example.gandline

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gandline.activity.LoginActivity
import com.example.gandline.activity.MainActivity
import com.example.gandline.http.AminoRequest

class LoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        val prefs = getSharedPreferences("account", Context.MODE_PRIVATE)
        if (prefs.getBoolean("authorized", false)) {
            AminoRequest.sid = prefs.getString("sid", null)!!
            AminoRequest.uid = prefs.getString("uid", null)!!
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}