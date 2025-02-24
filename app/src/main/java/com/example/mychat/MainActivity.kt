@file:Suppress("DEPRECATION")

package com.example.mychat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {

    private lateinit var navController : NavController



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFrag = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFrag.navController
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        if(supportFragmentManager.backStackEntryCount > 0){

            super.onBackPressed()




        }else{

            if(navController.currentDestination?.id == R.id.homeFragment){

                moveTaskToBack(true)

            }else{
                super.onBackPressed()

            }


        }
    }

}