package com.example.obstacledodge
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiService {

        @GET("tip")
        fun getRandomTip(): Call<Tip>

        @POST("characters")
        fun createCharacter(@Body request: CharacterRequest?): Call<CharacterResponse?>?

        @GET("/scores")
        fun getScores(): Call<ScoreResponse>

        @GET("word")
        fun getRandomWord(@Query("length") length: Int?): Call<Word>

}

