package com.example.qrcodesample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qrcodesample.databinding.ActivityZxingLibMainBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions


class ZxingLibMainActivity : AppCompatActivity() {
    /*
    * <application android:hardwareAccelerated="true" ... > 추가 해야함 라이브러리 내부적으로 TextureView를 사용함
    *
    *
    *
    *       방향변경
    *       <activity
            android:name="com.journeyapps.barcodescanner.CaptureActivity"
            android:screenOrientation="sensorPortrait"
            tools:replace="screenOrientation" />
    * */
    private val binding by lazy { ActivityZxingLibMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btQRStart.setOnClickListener {
            val options = ScanOptions()
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            options.setPrompt("Scan a barcode")
            options.setCameraId(0) // Use a specific camera of the device

            options.setBeepEnabled(false)
            options.setBarcodeImageEnabled(true)
            options.setOrientationLocked(false)
            barcodeLauncher.launch(options)
        }
    }

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(this@ZxingLibMainActivity, "Cancelled", Toast.LENGTH_LONG).show()
        } else {
            binding.textQRResult.text = result.contents
//            Toast.makeText(
//                this@ZxingLibMainActivity,
//                "Scanned: " + result.contents,
//                Toast.LENGTH_LONG
//            ).show()
        }
    }
}