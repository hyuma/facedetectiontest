package com.chammarit.facedetectiontest


import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        if (null == savedInstanceState) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FaceRecognitionFragment.newInstance())
                .commit()
        }
    }

}
