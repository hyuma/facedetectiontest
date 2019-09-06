package com.chammarit.facedetectiontest

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val ACTION_CAMERA_PERMISSION = 1


    private val textureView: TextureView by lazy {
        findViewById<TextureView>(R.id.texture_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }



    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            ACTION_CAMERA_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(TAG,"Permission Granted!")
                    setUpTextureView()
                } else {
                    Toast.makeText(this, "This App Requires Camera Permission", Toast.LENGTH_LONG).show()
                    android.os.Process.killProcess(android.os.Process.myPid())
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission( this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                ACTION_CAMERA_PERMISSION
            )
        } else {
            setUpTextureView()
        }




//        //CameraManagerの取得
//        val mCameraManager = baseContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
//        //利用可能なカメラIDのリストを取得
//        val cameraIdList = mCameraManager.cameraIdList
//        //用途に合ったカメラIDを設定
//        var mCameraId: String? = null
//        for (cameraId in cameraIdList) {
//            //カメラの向き(インカメラ/アウトカメラ)は以下のロジックで判別可能です。(今回はアウトカメラを使用します)
//            val characteristics = mCameraManager.getCameraCharacteristics(cameraId)
//            when (characteristics.get(CameraCharacteristics.LENS_FACING)) {
//                CameraCharacteristics.LENS_FACING_FRONT -> {
//                    mCameraId = cameraId
//                }
//                CameraCharacteristics.LENS_FACING_BACK -> {}
//            }
//        }
//
//
//        var mCameraDevice: CameraDevice? = null
//
//        //CameraDevice.StateCallback詳細
//        val mStateCallback = object : CameraDevice.StateCallback() {
//            override fun onOpened(cameraDevice: CameraDevice) {
//                //接続成功時、CameraDeviceのインスタンスを保持させる
//                mCameraDevice = cameraDevice
//                //createCameraPreviewSession()    //次フェーズにて説明します。
//            }
//
//            override fun onDisconnected(cameraDevice: CameraDevice) {
//                //接続切断時、CameraDeviceをクローズし、CameraDeviceのインスタンスをnullにする
//                cameraDevice.close()
//                mCameraDevice = null
//            }
//
//            override fun onError(cameraDevice: CameraDevice, error: Int) {
//                //エラー発生時、CameraDeviceをクローズし、CameraDeviceのインスタンスをnullにする
//                cameraDevice.close()
//                mCameraDevice = null
//            }
//        }
//        //CameraDeviceをオープン
//        mCameraManager.openCamera(mCameraId, mStateCallback, null)

        super.onResume()
    }

    private fun setUpTextureView(){
        if (textureView.isAvailable) {
            openCamera()
        } else {
            textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(texture: SurfaceTexture?, p1: Int, p2: Int) {
                    openCamera()
                }

                override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture?, p1: Int, p2: Int) {}
                override fun onSurfaceTextureUpdated(texture: SurfaceTexture?) {}
                override fun onSurfaceTextureDestroyed(texture: SurfaceTexture?): Boolean = true
            }
        }

    }

    private var cameraDevice: CameraDevice? = null

    private val cameraManager: CameraManager by lazy {
        getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED )
        {
            //利用可能なカメラIDのリストを取得
            val cameraIdList = cameraManager.cameraIdList
            //用途に合ったカメラIDを設定
            var mCameraId: String? = null
            for (cameraId in cameraIdList) {
                //カメラの向き(インカメラ/アウトカメラ)は以下のロジックで判別可能です。(今回はアウトカメラを使用します)
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                when (characteristics.get(CameraCharacteristics.LENS_FACING)) {
                    CameraCharacteristics.LENS_FACING_FRONT -> {
                        mCameraId = cameraId
                    }
                    CameraCharacteristics.LENS_FACING_BACK -> {}
                }
            }

            cameraManager.openCamera(mCameraId, object: CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    createCameraPreviewSession()
                }

                override fun onDisconnected(camera: CameraDevice) {
                    cameraDevice?.close()
                    cameraDevice = null
                }

                override fun onError(camera: CameraDevice, p1: Int) {
                    cameraDevice?.close()
                    cameraDevice = null
                }
            }, null)
        }

    }
    private var captureSession: CameraCaptureSession? = null

    private fun createCameraPreviewSession() {
        if (cameraDevice == null) {
            return
        }
        val texture = textureView.surfaceTexture
        texture.setDefaultBufferSize(640, 480)
        val surface = Surface(texture)

        val previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        previewRequestBuilder.addTarget(surface)

        cameraDevice?.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                captureSession = session
                captureSession?.setRepeatingRequest(previewRequestBuilder.build(), null, null)
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {}
        }, null)
    }
}
