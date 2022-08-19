package com.example.gandline.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gandline.R
import com.example.gandline.fragments.BlogFragment
import com.example.gandline.fragments.MainFragment
import com.example.gandline.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.gandline_bottom_menu)
        val profileFragment = ProfileFragment()
        val mainFragment = MainFragment()
        val blogFragment = BlogFragment()
        replaceFragment(mainFragment)

        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.main_menu_button -> replaceFragment(mainFragment)
                R.id.blog_menu_button -> replaceFragment(blogFragment)
                R.id.profile_menu_button -> replaceFragment(profileFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_container, fragment)
        transaction.commitNow()
    }

    override fun onBackPressed() {}
}