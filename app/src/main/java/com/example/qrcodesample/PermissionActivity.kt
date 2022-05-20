package com.example.qrcodesample

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

abstract class PermissionActivity : AppCompatActivity(){
    abstract fun permissionGranted(requestCode : Int)
    abstract fun permissionDenied(requestCode : Int)

    fun requirePermissions(permission:Array<String>, requestCode: Int){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permissionGranted(requestCode)
        }else{
            val isAllPermissionGranted = permission.all {
                checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
            }

            if(isAllPermissionGranted){
                permissionGranted(requestCode)
            }else{
                ActivityCompat.requestPermissions(this, permission, requestCode)
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED}){
            permissionGranted(requestCode)
        }else{
            permissionDenied(requestCode)
        }
    }
}