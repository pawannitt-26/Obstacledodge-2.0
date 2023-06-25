package com.example.obstacledodge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.obstacledodge.MainActivity.JumpingGameView.Companion.playername
import com.example.obstacledodge.MainActivity.JumpingGameView.Companion.score
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class leaderboard : AppCompatActivity() {

    var scoreList: ArrayList<Leaderboard> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        val homeButton = findViewById<Button>(R.id.btnHome)

        homeButton.setOnClickListener {
            val intent = Intent(this,choose::class.java)
            startActivity(intent)
        }


        hideStatusBar()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api-obstacle-dodge.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService: ApiService = retrofit.create(ApiService::class.java)



        class LeaderboardAdapter(private val playerList: ArrayList<Leaderboard>) : RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.leaderboarditem, parent, false)
                return LeaderboardViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
                val player = playerList[position]
                holder.playerNameTextView.text = player.name
                holder.scoreTextView.text = player.score.toString()
            }

            override fun getItemCount(): Int {
                return playerList.size
            }

            inner class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val playerNameTextView: TextView = itemView.findViewById(R.id.playerNameTextView)
                val scoreTextView: TextView = itemView.findViewById(R.id.scoreTextView)
            }
        }


        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        val adapter = LeaderboardAdapter(scoreList)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter



        apiService.getScores().enqueue(object : Callback<ScoreResponse> {
            override fun onResponse(call: Call<ScoreResponse>, response: Response<ScoreResponse>) {
                if (response.isSuccessful) {
                    val scoreResponse = response.body()
                    val scores = scoreResponse?.scores
                    if (scores != null) {
                        for (score in scores) {
                            scoreList.add(Leaderboard(score.name,score.score))
                            adapter.notifyDataSetChanged()
                        }
                        scoreList.add(Leaderboard(playername,(score/100).toInt()))
                        scoreList.sortByDescending { it.score }
                    } else {
                        // Handle API error
                    }
                }
            }
            override fun onFailure(call: Call<ScoreResponse>, t: Throwable) {
                // Handle network or unexpected error
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