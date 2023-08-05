package com.example.obstacledodge

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.obstacledodge.MainActivity.JumpingGameView.Companion.playerBitmap
import com.example.obstacledodge.MainActivity.JumpingGameView.Companion.playername
import com.example.obstacledodge.MainActivity.JumpingGameView.Companion.randomLetterList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.Throwable


class choose : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)


        hideStatusBar()

        val Charcater1 = findViewById<ImageView>(R.id.character1)
        val Charcater2 = findViewById<ImageView>(R.id.character2)
        val Charcater3 = findViewById<ImageView>(R.id.character3)

        val playButton1 = findViewById<Button>(R.id.btnPlay1)
        val playButton2 = findViewById<Button>(R.id.btnPlay2)
        val playButton3 = findViewById<Button>(R.id.btnPlay3)

        val player1Name =findViewById<TextView>(R.id.player1)
        val player2Name =findViewById<TextView>(R.id.player2)
        val player3Name =findViewById<TextView>(R.id.player3)

        val description1 = findViewById<TextView>(R.id.description1)
        val description2 = findViewById<TextView>(R.id.description2)
        val description3 = findViewById<TextView>(R.id.description3)


        val leadButton1 = findViewById<Button>(R.id.leaderboard1)
        val leadButton2 = findViewById<Button>(R.id.leaderboard2)


        leadButton1.setOnClickListener {
            val intent = Intent(this,leaderboard::class.java)
            startActivity(intent)
        }

        leadButton2.setOnClickListener {
            val intent = Intent(this,leaderboard::class.java)
            startActivity(intent)
        }



        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api-obstacle-dodge.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService: ApiService = retrofit.create(ApiService::class.java)

        // Fetch a random word of a specific length
        apiService.getRandomWord(5).enqueue(object : Callback<Word> {
            override fun onResponse(call: Call<Word>, response: Response<Word>) {
                if (response.isSuccessful) {
                    val word: Word? = response.body()
                    val reqWord= word?.word
                    if (reqWord != null) {
                        randomLetterList=reqWord.toList()
                    }

                } else {
                    // Handle the API error
                }
            }

            override fun onFailure(call: Call<Word>, t: Throwable) {
                // Handle the network or other errors
            }
        })

        val requestBody = mapOf("type" to "player")

        apiService.getPlayerData(requestBody).enqueue(object : Callback<PlayerResponse> {
            override fun onResponse(call: Call<PlayerResponse>, response: Response<PlayerResponse>) {
                if (response.isSuccessful) {
                    val playerResponse = response.body()
                    if (playerResponse != null) {
                        // Access the list of players from playerResponse.characters
                        val players = playerResponse.characters

                        val player1ImageUrl = players[0].imageUrl
                        val player2ImageUrl = players[1].imageUrl
                        val player3ImageUrl = players[2].imageUrl

                        val context = this@choose

                        // Load the images for all three players using Glide
                        Glide.with(context).load(player1ImageUrl).into(Charcater1)
                        Glide.with(context).load(player2ImageUrl).into(Charcater2)
                        Glide.with(context).load(player3ImageUrl).into(Charcater3)

                        player1Name.text=players[0].name
                        player2Name.text=players[1].name
                        player3Name.text=players[2].name

                        description1.text=players[0].description
                        description2.text=players[1].description
                        description3.text=players[2].description



                        Log.d("MainActivity", "Retrieved tip: $players")
                        // Do something with the players list
                    } else {
                        // Handle null response
                    }
                } else {
                    // Handle API call unsuccessful response
                    // You can get the error code using response.code()
                }
            }

            override fun onFailure(call: Call<PlayerResponse>, t: Throwable) {
                // Handle API call failure
            }
        })


        playButton1.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            playerBitmap= BitmapFactory.decodeResource(resources, R.drawable.player1)
            playername= player1Name.text.toString()
        }

        playButton2.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            playerBitmap= BitmapFactory.decodeResource(resources, R.drawable.player2)
            playername= player2Name.text.toString()
        }

        playButton3.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            playerBitmap= BitmapFactory.decodeResource(resources, R.drawable.player3)
            playername= player3Name.text.toString()
        }


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