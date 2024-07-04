package com.example.permissionchecker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Permissions : AppCompatActivity(), FetchApplicationTask.OnApplicationFetchedListener,FetchApplicationList.OnFetchApplicationListListener {
    lateinit var spinner : Spinner
    lateinit var permissionsArray: ArrayList<String>
    lateinit var appPackageName: String
    lateinit var appName: String
    lateinit var appInfo: ApplicationInfo
    lateinit var appIcon: Drawable
    lateinit var adapter: AppAdapter
    lateinit var packages: List<PackageInfo>
    lateinit var installedApps: MutableList<ApplicationInfo>
    private val appList = mutableListOf<AppName>()
    private val cameraList = mutableListOf<AppName>()
    private val contactsList = mutableListOf<AppName>()
    private val locationList = mutableListOf<AppName>()
    private val microphoneList = mutableListOf<AppName>()
    private val storagemediaList = mutableListOf<AppName>()
    private val smsList = mutableListOf<AppName>()
    lateinit var rv_main_list:RecyclerView
    lateinit var tvCount:TextView

    @SuppressLint("QueryPermissionsNeeded", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)

        FetchApplicationList(this@Permissions,this@Permissions).execute()
        permissionsArray = arrayListOf(
            "All apps",
            "Camera",
            "Contacts",
            "Location",
            "Microphone",
            "Storage & Media",
            "SMS"
            //or any other permission you want to check for
        )

        spinner = findViewById(R.id.spinner)
        rv_main_list = findViewById(R.id.rv_main_list)
        tvCount = findViewById(R.id.tvCount)

        initSpinner()

        /*packages = packageManager.getInstalledPackages(0)


        for (packageInfo in packages) {
            appPackageName = packageInfo.packageName
            appInfo = packageManager.getApplicationInfo(appPackageName, 0)
            appName = packageManager.getApplicationLabel(appInfo) as String
            appIcon = packageManager.getApplicationIcon(appPackageName)

            if (packageManager.getLaunchIntentForPackage(appPackageName) != null) {
                appList.add(AppName(appPackageName, appName, appIcon))

                if (checkCameraPermission(appPackageName)) {
                    cameraList.add(AppName(appPackageName, appName, appIcon))
                }
                if (checkContactsPermission(appPackageName)) {
                    contactsList.add(AppName(appPackageName, appName, appIcon))
                }
                if (checkLocationPermission(appPackageName)) {
                    locationList.add(AppName(appPackageName, appName, appIcon))
                }
                if (checkMicrophonePermission(appPackageName)) {
                    microphoneList.add(AppName(appPackageName, appName, appIcon))
                }
                if (checkStoragePermission(appPackageName)) {
                    storagemediaList.add(AppName(appPackageName, appName, appIcon))
                }
                if (checkSmsPermission(appPackageName)) {
                    smsList.add(AppName(appPackageName, appName, appIcon))
                }

            }
        }*/

        /*appList.sortBy { it.appName }
        cameraList.sortBy { it.appName }
        contactsList.sortBy { it.appName }
        locationList.sortBy { it.appName }
        microphoneList.sortBy { it.appName }
        storagemediaList.sortBy { it.appName }
        smsList.sortBy { it.appName }*/
        rv_main_list.layoutManager = LinearLayoutManager(this)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                val text = parentView?.getItemAtPosition(position).toString()
                when (text) {
                    permissionsArray[0] -> {    //All apps
                        FetchApplicationList(this@Permissions,this@Permissions).execute()
                    }
                    permissionsArray[1] -> {    //Camera
                        FetchApplicationTask(this@Permissions,"Camera",this@Permissions).execute()
                    }
                    permissionsArray[2] -> {    //Contacts
                        FetchApplicationTask(this@Permissions,"Contacts",this@Permissions).execute()
                    }
                    permissionsArray[3] -> {    //Location
                        FetchApplicationTask(this@Permissions,"Location",this@Permissions).execute()
                    }
                    permissionsArray[4] -> {       //Microphone
                        FetchApplicationTask(this@Permissions,"Microphone",this@Permissions).execute()
                    }
                    permissionsArray[5] -> {       //Storage & Media
                        FetchApplicationTask(this@Permissions,"Storage",this@Permissions).execute()
                    }
                    permissionsArray[6] -> {    //Sms
                        FetchApplicationTask(this@Permissions,"SMS",this@Permissions).execute()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
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

    private fun initSpinner() {
        val arrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            permissionsArray
        )

        spinner.adapter = arrayAdapter
    }

    fun onItemClicked(position: Int) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)

        when (spinner.selectedItemPosition) {
            0 -> {
                intent.data = Uri.parse("package:${appList[position].packageName}")
            }
            1 -> {
                intent.data = Uri.parse("package:${cameraList[position].packageName}")
            }
            2 -> {
                intent.data = Uri.parse("package:${contactsList[position].packageName}")
            }
            3 -> {
                intent.data = Uri.parse("package:${locationList[position].packageName}")
            }
            4 -> {
                intent.data = Uri.parse("package:${microphoneList[position].packageName}")
            }
            5 -> {
                intent.data = Uri.parse("package:${storagemediaList[position].packageName}")
            }
            6 -> {
                intent.data = Uri.parse("package:${smsList[position].packageName}")
            }
        }
        startActivity(intent)
    }

    override fun onApplicationFetched(application: MutableList<AppName>) {
        adapter = AppAdapter(application, applicationContext)
        rv_main_list.adapter = adapter
        tvCount.text = "${application.size} apps found"
    }

    override fun onApplicationList(applications: MutableList<AppName>) {
        adapter = AppAdapter(applications, applicationContext)
        rv_main_list.adapter = adapter
        tvCount.text = "${applications.size} apps found"
    }


}