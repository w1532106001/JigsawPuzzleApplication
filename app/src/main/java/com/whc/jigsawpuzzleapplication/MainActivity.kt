package com.whc.jigsawpuzzleapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

@ExperimentalStdlibApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent()
        intent.setClass(applicationContext,JigsawPuzzleActivity::class.java)
        startActivity(intent)
    }
}