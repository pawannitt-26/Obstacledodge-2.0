package com.example.obstacledodge

import com.google.gson.annotations.SerializedName


data class Tip(
    val tip: String
)

data class ScoreResponse(
    val scores: List<Score>
)

data class Score(
    val name: String,
    val score: Int
)

data class Word(
    val word: String
)

data class Leaderboard(
    val name: String,
    val score: Int
)

class Character {
    @SerializedName("name")
    val name: String? = null

    @SerializedName("description")
    val description: String? = null

    @SerializedName("type")
    val type: String? = null

    @SerializedName("imageUrl")
    val imageUrl: String? = null
}

class CharacterRequest(
    @field:SerializedName("type")
    val type: Int
    )

class CharacterResponse {
    @SerializedName("characters")
    val characters: ArrayList<Character>? = null
}

