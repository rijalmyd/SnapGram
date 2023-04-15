package com.rijaldev.snapgram.presentation.addstory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rijaldev.snapgram.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddStoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)
    }
}