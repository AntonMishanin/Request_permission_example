package com.paint.requestpermissionexample

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import android.util.Log
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 0
        val TAG = MainActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissionGranted = checkPermission()
        if (permissionGranted) {
            Log.d(TAG, "PERMISSION ALREADY GRANTED")
        } else {
            Log.d(TAG, "PERMISSION NOT YET GRANTED")
            requestPermission()
        }
    }

    private fun checkPermission(): Boolean =
            if (SDK_INT >= Build.VERSION_CODES.R) {
                Environment.isExternalStorageManager()
            } else {
                val result = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
                val result1 = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
                result == PERMISSION_GRANTED && result1 == PERMISSION_GRANTED
            }


    private fun requestPermission() =
            if (SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    val intent = Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                    startActivityForResult(intent, PERMISSION_REQUEST_CODE)
                } catch (e: Exception) {
                    val intent = Intent()
                    intent.action = ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    startActivityForResult(intent, PERMISSION_REQUEST_CODE)
                }
            } else {
                //below android 11
                val permission = arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permission, PERMISSION_REQUEST_CODE)
            }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != PERMISSION_REQUEST_CODE) return

        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                Log.d(TAG, "PERMISSION ANDROID 11 GRANTED")
            } else {
                Log.d(TAG, "PERMISSION ANDROID 11 DENIED")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode != PERMISSION_REQUEST_CODE) return

        for (i in grantResults.indices) {
            if (grantResults[i] != PERMISSION_GRANTED) {
                Log.d(TAG, "PERMISSION DENIED")
                return
            }
        }
        Log.d(TAG, "PERMISSION GRANTED")
    }
}
