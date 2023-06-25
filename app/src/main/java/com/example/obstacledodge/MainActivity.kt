package com.example.obstacledodge

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.sin
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(JumpingGameView(this))

        hideStatusBar()
    }


    class JumpingGameView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {


        companion object {
            private const val OBSTACLE_WIDTH_SHORT = 150
            private const val OBSTACLE_WIDTH_LONG = 200
            private const val OBSTACLE_SIZE = 200
            private const val PLAYER_SIZE = 150
            private const val CHASER_SIZE = 200
            private const val GENERATE_DELAY=3500
            private const val JUMP_HEIGHT = 400
            private const val GROUND_HEIGHT =800
            private var CHASER_SPEED = 8
            private var PLAYER_SPEED = 8
            private var OBSTACLE_SPEED = 5
            private var CLOUD_SPEED = 3
            var playerBitmap: Bitmap? = null
            var playername=""
            var score=0f
            var randomWord :String ?= null
            var randomLetterList:List<Char>?=null
        }

        private var obstacleShortBitmap: Bitmap? = null
        private var obstacleLongBitmap: Bitmap? = null
        private var backgroundBitmap: Bitmap? = null
        private var groundBitmap: Bitmap? = null
        private var cloud1Bitmap: Bitmap? = null
        private var cloud2Bitmap: Bitmap? = null
        private var cloud3Bitmap: Bitmap? = null

        private var chaserBitmap: Bitmap? = null
        private var grass1Bitmap: Bitmap? = null
        private var grass2Bitmap: Bitmap? = null
        private var goldenEggBitmap: Bitmap? = null
        private var birdBitmap: Bitmap? = null
        private var playerY: Int = GROUND_HEIGHT - PLAYER_SIZE + 70
        private var playerX: Float = 800f
        private var chaserY: Int = GROUND_HEIGHT- OBSTACLE_SIZE + 70
        private var chaserX: Float = 300f
        private var obstacleY: Int = GROUND_HEIGHT - OBSTACLE_SIZE+40
        private var obstacleList: MutableList<Obstacle> = mutableListOf()
        private var cloudList: MutableList<Cloud> = mutableListOf()
        private var rectLeft = 0
        private var isChaserJumping: Boolean = false
        private var isPlayerJumping:Boolean = false
        private var chaserJumpCount: Int = 0
        private val CHASER_JUMP_HEIGHT: Int = 400
        private var jumpCount=0
        private var x1=200
        private var x2=500
        private var x3=800
        private var x4=1100
        private var x5=1400
        private var birdX=500
        private var birdY=200f
        private var eggX=1100
        private var mediaPlayer: MediaPlayer? = null
        var randomX =1500
        val randomY= (GROUND_HEIGHT-100).toFloat()
        var letter:Char?=null


        init {
            obstacleShortBitmap = BitmapFactory.decodeResource(resources, R.drawable.shortobstacle)
            obstacleLongBitmap = BitmapFactory.decodeResource(resources, R.drawable.longobstacle)
//            playerBitmap = BitmapFactory.decodeResource(resources, R.drawable.player)
            chaserBitmap = BitmapFactory.decodeResource(resources, R.drawable.chaser)
            birdBitmap = BitmapFactory.decodeResource(resources, R.drawable.bird)
            groundBitmap = BitmapFactory.decodeResource(resources, R.drawable.ground)
            backgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.background)
            cloud1Bitmap= BitmapFactory.decodeResource(resources, R.drawable.cloud1)
            cloud2Bitmap= BitmapFactory.decodeResource(resources, R.drawable.cloud2)
            cloud3Bitmap= BitmapFactory.decodeResource(resources, R.drawable.cloud3)
            grass1Bitmap= BitmapFactory.decodeResource(resources, R.drawable.grass1)
            grass2Bitmap= BitmapFactory.decodeResource(resources, R.drawable.grass2)
            goldenEggBitmap= BitmapFactory.decodeResource(resources, R.drawable.goldenegg)

            mediaPlayer = MediaPlayer.create(context, R.raw.templerun)
            mediaPlayer?.isLooping = true

            // Start generating obstacles
            Thread {
                while (true) {
                    generateObstacle()
                    Thread.sleep(GENERATE_DELAY.toLong())
                }
            }.start()
            // Start generating cloud
            Thread {
                while (true) {
                    generateCloud()
                    Thread.sleep(GENERATE_DELAY+2000.toLong())
                }
            }.start()

            // Start moving chaser
            Thread {
                while (true) {
                    moveChaser()
                    Thread.sleep(16)
                }
            }.start()

        }

        private inner class Obstacle(val bitmap: Bitmap, var x: Int) {
            val width: Int = bitmap.width
        }

        private inner class Cloud(val cloudbitmap: Bitmap, var x: Int) {
            val width: Int = cloudbitmap.width
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)


            if (!mediaPlayer?.isPlaying!!) {
                mediaPlayer?.start()
            }


            // Draw the background image as a full-screen background
            backgroundBitmap?.let {
                canvas.drawBitmap(it, null, Rect(0, 0, width, height), null)
            }

            score++


            //draw text to store score value
            val textPaint = Paint().apply {
                color = Color.BLACK
                textSize = 60f
                textAlign = Paint.Align.CENTER
            }
            val text = "SCORE: $score"
            val x = (width / 2-50).toFloat()
            val y = (height / 2+50).toFloat()
            canvas.drawText(text, x, y, textPaint)


            // Draw the base
            val paint1 = Paint()
            val brownColor = Color.rgb(139, 69, 19)
            paint1.color = brownColor
            paint1.style = Paint.Style.FILL
            val rect = Rect(rectLeft, GROUND_HEIGHT, width, height)
            canvas.drawRect(rect, paint1)
            grass2Bitmap?.let {
                val grassBitmapRect = Rect(rectLeft, GROUND_HEIGHT-50, width, height)
                canvas.drawBitmap(it, null, grassBitmapRect, null)
            }


            // Draw moving illusion
            val paint2 = Paint()
            paint2.color = Color.TRANSPARENT
            paint2.style = Paint.Style.FILL

            val cube1 = Rect(x1+0, GROUND_HEIGHT,  x1+200, GROUND_HEIGHT+150)
            canvas.drawRect(cube1,paint2)
            grass1Bitmap?.let {
                val grass1BitmapRect=Rect(x1+0, GROUND_HEIGHT,  x1+200, GROUND_HEIGHT+150)
                canvas.drawBitmap(it,null,grass1BitmapRect,null) }
            x1-=5
            if(x1+200<0) x1=width

            val cube2 = Rect(x2+0, GROUND_HEIGHT+50,x2+150, GROUND_HEIGHT+200)
            canvas.drawRect(cube2,paint2)
            grass1Bitmap?.let {
                val grass2BitmapRect= Rect(x2+0, GROUND_HEIGHT+50,x2+150, GROUND_HEIGHT+200)
                canvas.drawBitmap(it,null,grass2BitmapRect,null) }
            x2-=5
            if(x2+150<0) x2=width

            val cube3 = Rect(x3+0, GROUND_HEIGHT+30,x3+100, GROUND_HEIGHT+130)
            canvas.drawRect(cube3,paint2)
            grass1Bitmap?.let {
                val grass3BitmapRect=Rect(x3+0, GROUND_HEIGHT+30,x3+100, GROUND_HEIGHT+130)
                canvas.drawBitmap(it,null,grass3BitmapRect,null) }
            x3-=5
            if(x3+100<0) x3=width

            val cube4 = Rect(x4+0, GROUND_HEIGHT+50,x4+150, GROUND_HEIGHT+200)
            canvas.drawRect(cube4,paint2)
            grass1Bitmap?.let {
                val grass4BitmapRect=Rect(x4+0, GROUND_HEIGHT+50,x4+150, GROUND_HEIGHT+200)
                canvas.drawBitmap(it,null,grass4BitmapRect,null) }
            x4-=5
            if(x4+150<0) x4=width

            val cube5 = Rect(x5+0, GROUND_HEIGHT,  x5+200, GROUND_HEIGHT+150)
            canvas.drawRect(cube5,paint2)
            grass1Bitmap?.let {
                val grass5BitmapRect=Rect(x5+0, GROUND_HEIGHT,  x5+200, GROUND_HEIGHT+150)
                canvas.drawBitmap(it,null,grass5BitmapRect,null) }
            x5-=5
            if(x5+200<0) x5=width




            // Draw the player rectangle
            val playerRect = Rect(playerX.toInt(), playerY, (playerX + PLAYER_SIZE).toInt(), playerY + PLAYER_SIZE)
            val playerPaint = Paint().apply {
                color = Color.TRANSPARENT
            }
            canvas.drawRect(playerRect, playerPaint)
            // Draw the player bitmap
            playerBitmap?.let {
                val playerBitmapRect =
                    Rect(playerX.toInt(), playerY, (playerX + PLAYER_SIZE).toInt(), playerY + PLAYER_SIZE)
                canvas.drawBitmap(it, null, playerBitmapRect, null)
            }


            //update player position
            playerX+=(2.0* sin(score)).toFloat()

            //draw the bird
            birdBitmap?.let {
                val birdBitmapRect =
                    Rect(birdX,birdY.toInt(),(birdX+200),(birdY+200).toInt())
                canvas.drawBitmap(it,null,birdBitmapRect,null)
            }
            //update bird position
            birdX+=2
            birdY+= 10*sin(birdY)
            if (birdX>width){
                birdX=0
            }


            // Draw the chaser rectangle
            val chaserRect = Rect(chaserX.toInt(), chaserY, (chaserX + CHASER_SIZE).toInt(), chaserY + CHASER_SIZE)
            val chaserPaint = Paint().apply {
                color = Color.TRANSPARENT
            }
            canvas.drawRect(chaserRect, chaserPaint)
            // Draw the chaser bitmap
            chaserBitmap?.let {
                val chaserBitmapRect =
                    Rect(chaserX.toInt(), chaserY, (chaserX + CHASER_SIZE).toInt(), chaserY + CHASER_SIZE)
                canvas.drawBitmap(it, null, chaserBitmapRect, null)
            }

            //update chaser position
            chaserX+=(2.0* sin(score)).toFloat()



            // Draw obstacles
            for (obstacle in obstacleList) {

                //Draw obstacle rectangle
                val obstacleRect = Rect(obstacle.x, obstacleY, obstacle.x + OBSTACLE_SIZE,obstacleY + OBSTACLE_SIZE)
                val obstaclePaint = Paint().apply {
                    color = Color.TRANSPARENT
                }
                canvas.drawRect(obstacleRect, obstaclePaint)
                // Draw the obstacle bitmap
                obstacle.bitmap?.let {
                    val obstacleBitmapRect = Rect(obstacle.x, obstacleY, obstacle.x + OBSTACLE_SIZE, obstacleY + OBSTACLE_SIZE)
                    canvas.drawBitmap(it, null, obstacleBitmapRect, null)
                }
            }

            if(score==1500f) OBSTACLE_SPEED = 8
            if(score==3000f) OBSTACLE_SPEED = 12

            if (score>1000f && score<1500f){
                letter = randomLetterList?.get(0)
                canvas.drawText(letter.toString(), randomX.toFloat(), randomY, textPaint)
            }
            if (score>1500f && score<2000f){
                letter = randomLetterList?.get(1)
                canvas.drawText(letter.toString(), randomX.toFloat(), randomY, textPaint)
            }
            if (score>2000f && score<2500f){
                letter = randomLetterList?.get(2)
                canvas.drawText(letter.toString(), randomX.toFloat(), randomY, textPaint)
            }
            if (score>2500f && score<3000f){
                letter = randomLetterList?.get(3)
                canvas.drawText(letter.toString(), randomX.toFloat(), randomY, textPaint)
            }
            if (score>3000f && score<3500f){
                letter = randomLetterList?.get(4)
                canvas.drawText(letter.toString(), randomX.toFloat(), randomY, textPaint)
                Toast.makeText(context, "you got additiona 400 credit", Toast.LENGTH_SHORT).show()
            }



            if(score>4000f){
            // Draw the egg rectangle
            val eggRect = Rect(eggX, GROUND_HEIGHT-200, eggX+200, GROUND_HEIGHT)
            val eggPaint = Paint().apply {
                color = Color.TRANSPARENT
            }
            canvas.drawRect(eggRect, eggPaint)
            // Draw the egg bitmap
            goldenEggBitmap?.let {
                val eggBitmapRect =
                    Rect(eggX, GROUND_HEIGHT-150, eggX+150, GROUND_HEIGHT)
                canvas.drawBitmap(it, null, eggBitmapRect, null)
            }
                //update egg position
                eggX-=2
                if(eggRect.intersect(playerRect)){
                    goldenEggBitmap?.recycle()
                    goldenEggBitmap=null
                    OBSTACLE_SPEED=5
                }


            }



            val obstaclesToRemove = mutableListOf<Obstacle>()

            for (obstacle in obstacleList) {
                obstacle.let {
                    it.x -= OBSTACLE_SPEED

                    // Check if the obstacle has moved off the screen
                    if (it.x + it.width < 0) {
                        obstaclesToRemove.add(it)
                    }
                }
            }

            // Remove obstacles that have moved off the screen
            obstacleList.removeAll(obstaclesToRemove)


            //draw the cloud Bitmap
            for (cloud in cloudList){
                cloud.cloudbitmap?.let {
                    val cloudBitmapRect =  Rect(cloud.x, -30, cloud.x+500, 200)
                    canvas.drawBitmap(it,null,cloudBitmapRect,null)

                }
            }

            val cloudToRemove = mutableListOf<Cloud>()

            for (cloud in cloudList){
                cloud.let {
                    it.x -= CLOUD_SPEED

                    // Check if the cloud has moved off the screen
                    if (it.x + it.width < 0) {
                        cloudToRemove.add(it)
                    }
                }
            }

            // Remove cloud that have moved off the screen
            cloudList.removeAll(cloudToRemove)


//            checkCollision()

            invalidate()


        }


        override fun onTouchEvent(event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_DOWN && !isPlayerJumping) {
                playerJump()
            }
            return true
        }


        private fun generateObstacle() {
            val randomWidth =
                if (Random.nextBoolean()) OBSTACLE_WIDTH_SHORT else OBSTACLE_WIDTH_LONG
            val randomBitmap =
                if (randomWidth == OBSTACLE_WIDTH_SHORT) obstacleShortBitmap else obstacleLongBitmap

            randomBitmap?.let {
                obstacleList.add(Obstacle(it, width))
            }
        }

        private fun generateCloud() {

            val random = (0..2).random()
            val cloudBitmap: Bitmap? =
            if (random == 0) cloud1Bitmap
            else if (random == 1) cloud2Bitmap
            else cloud3Bitmap

            cloudBitmap?.let {
                cloudList.add(Cloud(it,width))
            }
        }


        private fun playerJump() {
            isPlayerJumping = true
            jumpCount = 0

            // Perform the jump animation
            Thread {
                while (jumpCount < JUMP_HEIGHT) {
                    playerY -= PLAYER_SPEED
                    jumpCount += PLAYER_SPEED
                    Thread.sleep(16)
                }

                // Reverse the jump animation
                while (jumpCount > 0) {
                    playerY += PLAYER_SPEED-3
                    jumpCount -= PLAYER_SPEED
                    Thread.sleep(16)
                }

                // Reset the player position and jumping flag
                playerY = GROUND_HEIGHT - PLAYER_SIZE
                isPlayerJumping = false
            }.start()
        }



        private fun moveChaser(){

            // Check if an obstacle is close to the chaser
            val obstacleThreshold = chaserX + PLAYER_SIZE + OBSTACLE_SIZE
            for (obstacle in obstacleList) {
                if (obstacle.x < obstacleThreshold + 5 && obstacle.x > chaserX) {
                    // Start chaser jump
                    if (!isChaserJumping) {
                        startChaserJump()
                    }
                    break
                }
            }
        }

        private fun startChaserJump(){
            isChaserJumping = true
            chaserJumpCount = 0

            // Perform the chaser jump animation
            Thread {
                while (chaserJumpCount < CHASER_JUMP_HEIGHT) {
                    chaserY -= CHASER_SPEED
                    chaserJumpCount += CHASER_SPEED
                    Thread.sleep(16)
                }

                // Reverse the chaser jump animation
                while (chaserJumpCount > 0) {
                    chaserY += CHASER_SPEED
                    chaserJumpCount -= CHASER_SPEED
                    Thread.sleep(16)
                }

                // Reset the chaser jump flag
                isChaserJumping = false
            }.start()
        }

//        private var count = 0
//        private fun checkCollision(){
//
//            for(obstacle in obstacleList) {
//
//                if (obstacle.x < playerX + PLAYER_SIZE && obstacle.x + OBSTACLE_SIZE > playerX &&
//                    obstacleY < playerY + PLAYER_SIZE && obstacleY + OBSTACLE_SIZE > playerY){
//
//                    obstacleList.remove(obstacle)
//                    while (chaserX<550) {
//                        chaserX+=1
//                    }
//                    count++
//                    Toast.makeText(context,
//                        "you lost $count time,you have only one chance left",
//                        Toast.LENGTH_SHORT
//                        ).show()
//                }
//            }
//
//        }

//        private var count = 0
//        private fun checkCollision(){
//
//            val playerRect = Rect(playerX.toInt(), playerY, (playerX + PLAYER_SIZE).toInt(), playerY + PLAYER_SIZE)
//
//            for(obstacle in obstacleList) {
//                val obstacleRect = Rect(obstacle.x, obstacleY, obstacle.x + OBSTACLE_SIZE,obstacleY + OBSTACLE_SIZE)
//
//                if (playerRect.intersect(obstacleRect)){
//
//                    obstacleList.remove(obstacle)
//                    while (chaserX<550f) {
//                        chaserX+=1f
//                    }
//                    count++
//                    Toast.makeText(context,
//                        "you lost $count time,you have only one chance left",
//                        Toast.LENGTH_SHORT
//                        ).show()
//                }
//            }
//
//        }



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