package com.example.mychat

import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object{

        private val auth = FirebaseAuth.getInstance()
        private var userid: String = ""

        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2

        fun getUidLoggedIn(): String {

            if (auth.currentUser!=null){


                userid = auth.currentUser!!.uid



            }


            return userid



        }



        fun getTime(): String {


            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date: Date = Date(System.currentTimeMillis())
            val stringdate = formatter.format(date)


            return stringdate

        }
    }

}