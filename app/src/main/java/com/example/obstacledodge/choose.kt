package com.example.obstacledodge

import android.R.attr.type
import android.content.ContentValues
import android.content.ContentValues.TAG
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
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.String
import kotlin.Throwable
import kotlin.toString


class choose : AppCompatActivity() {

    var characterList=ArrayList<Character>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)


        hideStatusBar()

        val Charcater1 = findViewById<ImageView>(R.id.character1)

        val playButton1 = findViewById<Button>(R.id.btnPlay1)
        val playButton2 = findViewById<Button>(R.id.btnPlay2)
        val playButton3 = findViewById<Button>(R.id.btnPlay3)

        val player1Name =findViewById<TextView>(R.id.player1)
        val player2Name =findViewById<TextView>(R.id.player2)
        val player3Name =findViewById<TextView>(R.id.player3)


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

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api-obstacle-dodge.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService: ApiService = retrofit.create(ApiService::class.java)

        // Example: Fetch a random word of a specific length
        apiService.getRandomWord(5).enqueue(object : Callback<Word> {
            override fun onResponse(call: Call<Word>, response: Response<Word>) {
                if (response.isSuccessful) {
                    val word: Word? = response.body()
                    MainActivity.JumpingGameView.randomWord =word.toString()
                    // Extract the substring using a regular expression
                    val pattern = Regex("word=([A-Z]+)")
                    val matchResult = pattern.find(MainActivity.JumpingGameView.randomWord.toString())
                    MainActivity.JumpingGameView.randomWord = matchResult?.groupValues?.get(1)
                    MainActivity.JumpingGameView.randomLetterList = MainActivity.JumpingGameView.randomWord?.toList()
                } else {
                    // Handle the API error
                }
            }

            override fun onFailure(call: Call<Word>, t: Throwable) {
                // Handle the network or other errors
            }
        })

        val request = CharacterRequest(type)
        apiService.createCharacter(request)?.enqueue(object : Callback<CharacterResponse?> {
            override fun onResponse(
                call: Call<CharacterResponse?>,response: Response<CharacterResponse?>) {

                if (response.isSuccessful) {
                    // Request successful, handle the response
                    val characterResponse = response.body()
                    if (characterResponse!=null) {
                        var character: Character
                        for (i in characterResponse.characters!!.indices) {
                            character = characterResponse.characters[i]
                            characterList.add(character)
                        }
                        Picasso.get().load(characterList[0].imageUrl).into(Charcater1)
                        // Do something with the response data
                    }else{
                        Log.e("choose", "characterResponse is null")
                    }
                } else {
                    // Request failed, handle the error
                    Log.e(TAG, "Request failed: response is unsuccessful")
                    // Access error information using response.errorBody()
                }
            }

            override fun onFailure(call: Call<CharacterResponse?>, t: Throwable) {
                // Request failed, handle the failure
                Log.e(TAG, "Request failed", t)
            }
        })

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