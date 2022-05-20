package com.example.qrcodesample

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.qrcodesample.databinding.ActivityQrcodeScanBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class QRCodeScanActivity : PermissionActivity() {
    /*
    * Android BarcodeDetector 사용
    * com.google.android.gms:play-services-vision
    * com.google.android.gms.vision.barcode
    * 직접 구현
    * 1. 카메라 권한 요청
    * 2. BarcodeDetector 실행
    * */

    companion object {
        private val PERMISSION_STORAGE = 199;
        private val PERMISSION_CAMERA = 200;
    }


    private val binding by lazy { ActivityQrcodeScanBinding.inflate(layoutInflater) }

    private var cameraSource: CameraSource? = null
    private var barcodeDetector: BarcodeDetector? = null

    private var qrcodeArray = mutableListOf<String>()
    var globalQueue : Queue<String> = ConcurrentLinkedQueue<String>()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //강제 세로 고정
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setViews()
        requirePermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_STORAGE)
    }

    private fun setViews() {
        binding.buttonCamera.setOnClickListener {
            requirePermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_CAMERA)
        }
    }

    override fun permissionGranted(requestCode: Int) {
        when (requestCode) {
            PERMISSION_STORAGE -> setViews()
            PERMISSION_CAMERA -> openCamera()
        }
    }

    override fun permissionDenied(requestCode: Int) {
        when (requestCode) {
            PERMISSION_STORAGE -> {
                Toast.makeText(baseContext, "저장소 권한이 있어야 기능을 사용할수 있습니다", Toast.LENGTH_LONG).show()
                finish()
            }
            PERMISSION_CAMERA -> {
                Toast.makeText(baseContext, "권한이 있어야 카메라를 사용할수 있습니다", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openCamera() {



        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        barcodeDetector =
            BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.FORMAT_QR_CODE).build()

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedFps(0.1f)
            .setRequestedPreviewSize(640, 640)
            .setAutoFocusEnabled(true) 
            .build()

        (binding.cameraSurfaceView as CameraSourcePreview).start(cameraSource)

        barcodeDetector?.setProcessor(object :
            com.google.android.gms.vision.Detector.Processor<com.google.android.gms.vision.barcode.Barcode> {
            override fun release() {
                Toast.makeText(applicationContext, "Scanner has been closed", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun receiveDetections(detections: Detector.Detections<com.google.android.gms.vision.barcode.Barcode>) {
                val barcodes: SparseArray<com.google.android.gms.vision.barcode.Barcode> =
                    detections.detectedItems
                if (barcodes.size() != 0) {
                    val barcodeContents = barcodes.valueAt(0).displayValue!! // 바코드 인식 결과물
                    Log.d("Detection", barcodeContents)

                    //연속 QR코드 찍기
                    //일단 간단히 중복 제거
                    if (!globalQueue.contains(barcodeContents)){
                        globalQueue.add(barcodeContents)

                        val tg = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
                        tg.startTone(ToneGenerator.TONE_PROP_BEEP)

                        binding.textResult.text = globalQueue.toString()
                    }
                }
            }
        })
    }
    override fun onPause() {
        super.onPause()
        (binding.cameraSurfaceView as CameraSourcePreview).stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource?.stop()
        (binding.cameraSurfaceView as CameraSourcePreview).release();
    }
}

