package com.example.permissionchecker

import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.widget.AdapterView

class FetchApplicationTask(
    context: Context,
    private val permissionToCheck: String,
    private val listener: OnApplicationFetchedListener
) : AsyncTask<Void, Void, MutableList<AppName>>() {
    private val packageManager: PackageManager = context.packageManager
    override fun doInBackground(vararg params: Void?): MutableList<AppName> {
        val appInfoWithPermission = mutableListOf<AppName>()
        val packages = packageManager.getInstalledPackages(0)
        for(packageInfo in packages){
            try {
                val appPackageName = packageInfo.packageName
                val appInfo = packageManager.getApplicationInfo(appPackageName, 0)
                val appName = packageManager.getApplicationLabel(appInfo) as String
                val appIcon = packageManager.getApplicationIcon(appPackageName)
                val permissions = packageManager.getPackageInfo(appPackageName,PackageManager.GET_PERMISSIONS).requestedPermissions
                if (permissionToCheck == "Camera"){
                    if (checkCameraPermission(appPackageName)) {
                        appInfoWithPermission.add(AppName(appPackageName,appName,appIcon))
                    }
                }else if (permissionToCheck == "Contacts"){
                    if (checkContactsPermission(appPackageName)) {
                        appInfoWithPermission.add(AppName(appPackageName,appName,appIcon))
                    }
                }else if (permissionToCheck == "Location"){
                    if (checkLocationPermission(appPackageName)) {
                        appInfoWithPermission.add(AppName(appPackageName,appName,appIcon))
                    }
                }else if (permissionToCheck == "Microphone"){
                    if (checkMicrophonePermission(appPackageName)) {
                        appInfoWithPermission.add(AppName(appPackageName,appName,appIcon))
                    }
                }else if (permissionToCheck == "Storage"){
                    if (checkStoragePermission(appPackageName)) {
                        appInfoWithPermission.add(AppName(appPackageName,appName,appIcon))
                    }
                }else if (permissionToCheck == "SMS"){
                    if (checkSmsPermission(appPackageName)) {
                        appInfoWithPermission.add(AppName(appPackageName,appName,appIcon))
                    }
                }

            } catch (e: PackageManager.NameNotFoundException) {
                // Handle exception if package information is not found
                e.printStackTrace()
            }

        }
        appInfoWithPermission.sortBy { it.appName }
        return appInfoWithPermission
    }

    override fun onPostExecute(result: MutableList<AppName>?) {
        super.onPostExecute(result)
        if (result != null) {
            listener.onApplicationFetched(result)
        }
    }

    interface OnApplicationFetchedListener {
        fun onApplicationFetched(applications: MutableList<AppName>)
    }

    private fun checkCameraPermission(appPackageName: String?): Boolean {
        return PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(
            android.Manifest.permission.CAMERA,
            appPackageName!!
        )
    }

    private fun checkContactsPermission(appPackageName: String?): Boolean {
        return (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(
            android.Manifest.permission.READ_CONTACTS,
            appPackageName!!
        )) or (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(
            android.Manifest.permission.WRITE_CONTACTS,
            appPackageName
        ))
    }

    private fun checkLocationPermission(appPackageName: String?): Boolean {
        return (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            appPackageName!!
        )) or (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            appPackageName
        ))
    }

    private fun checkMicrophonePermission(appPackageName: String?): Boolean {
        return PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(
            android.Manifest.permission.RECORD_AUDIO,
            appPackageName!!
        )
    }

    private fun checkStoragePermission(appPackageName: String?): Boolean {
        return (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            appPackageName!!
        )) or (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            appPackageName
        ))
    }

    private fun checkSmsPermission(appPackageName: String?): Boolean {
        return (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(
            android.Manifest.permission.READ_SMS,
            appPackageName!!
        )) or (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(
            android.Manifest.permission.SEND_SMS,
            appPackageName
        )) or (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(
            android.Manifest.permission.RECEIVE_SMS,
            appPackageName
        ))
    }
}