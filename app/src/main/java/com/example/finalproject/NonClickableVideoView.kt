package com.example.finalproject

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.VideoView
class NonClickableVideoView(context: Context, attrs: AttributeSet? = null) : VideoView(context, attrs) {

    init {
        setOnClickListener(null)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }
}