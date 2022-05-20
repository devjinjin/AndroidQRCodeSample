package com.example.qrcodesample

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.qrcodesample.databinding.ActivityMainBinding
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import java.util.*

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //구글서비스 사용
        val barcodeResultView = findViewById<TextView>(R.id.barcode_result_view1)

        binding.scanBarcodeButton1.setOnClickListener {
                val gmsBarcodeScanner: GmsBarcodeScanner = GmsBarcodeScanning.getClient(this)
                gmsBarcodeScanner
                    .startScan()
                    .addOnSuccessListener { barcode ->
                        barcodeResultView.text = getSuccessfulMessage(
                            barcode
                        )
                    }
                    .addOnFailureListener { e -> barcodeResultView.text = getErrorMessage(e as MlKitException) }
            }
        //직접 구현한 QRCode Reader
        binding.scanBarcodeButton2.setOnClickListener {
            val intent = Intent(this, QRCodeScanActivity::class.java)
            startActivity(intent)
        }

        //Zxing 사용 ( https://github.com/journeyapps/zxing-android-embedded )
        binding.scanBarcodeButton3.setOnClickListener {
            val intent = Intent(this, ZxingLibMainActivity::class.java)
            startActivity(intent)
        }


    }
    //구글서비스 사용
    private fun getSuccessfulMessage(barcode: Barcode): String? {
        val barcodeValue = String.format(
            Locale.US,
            "Display Value: %s\nRaw Value: %s\nFormat: %s\nValue Type: %s",
            barcode.displayValue,
            barcode.rawValue,
            barcode.format,
            barcode.valueType
        )
        return "성공 : $barcodeValue"
    }
    //구글서비스 사용
    @SuppressLint("SwitchIntDef")
    private fun getErrorMessage(e: MlKitException): String? {
        return when (e.errorCode) {
            MlKitException.CODE_SCANNER_CANCELLED -> "스캔취소"
            MlKitException.CODE_SCANNER_CAMERA_PERMISSION_NOT_GRANTED -> "카메라퍼미션 없음"
            MlKitException.CODE_SCANNER_APP_NAME_UNAVAILABLE -> "앱이없다"
            else -> "Failed to scan code : $e"
        }
    }
}