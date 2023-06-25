package com.example.obstacledodge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val splashDurationMillis = 3000L
        val tipText = findViewById<TextView>(R.id.tip)
        tipText.text=""
        var message = ""

        hideStatusBar()



        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api-obstacle-dodge.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService: ApiService = retrofit.create(ApiService::class.java)


        apiService.getRandomTip().enqueue(object : Callback<Tip> {
            override fun onResponse(call: Call<Tip>, response: Response<Tip>) {
                if (response.isSuccessful) {
                    val tip: Tip? = response.body()
                    if (tip != null) {
                        message = tip.tip
                        tipText.text=message
                        Log.d("MainActivity", "Retrieved tip: $message")
                    } else {
                        Log.e("MainActivity", "Tip object is null")
                    }
                } else {
                    Log.e("MainActivity", "API call unsuccessful: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Tip>, t: Throwable) {
                Log.e("MainActivity", "API call failed", t)
            }
        })

        Thread {
            Thread.sleep(splashDurationMillis)
            // Start the main activity after the splash screen duration
            val intent = Intent(this, choose::class.java)
            startActivity(intent)
            // Close the splash screen activity
            finish()
        }.start()

    }


    fun hideStatusBar() {
        // Hide the status bar
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)

        // Make the activity fullscreen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
}