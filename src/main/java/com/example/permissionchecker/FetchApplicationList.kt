package com.example.permissionchecker

import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask

class FetchApplicationList(
    context: Context,
    private val listener: OnFetchApplicationListListener
): AsyncTask<Void, Void, MutableList<AppName>>() {
    private val packageManager: PackageManager = context.packageManager
    override fun doInBackground(vararg params: Void?): MutableList<AppName> {
        val appList = mutableListOf<AppName>()
        val packages = packageManager.getInstalledPackages(0)
        for(packageInfo in packages){
            try{
                val appPackageName = packageInfo.packageName
                val appInfo = packageManager.getApplicationInfo(appPackageName, 0)
                val appName = packageManager.getApplicationLabel(appInfo) as String
                val appIcon = packageManager.getApplicationIcon(appPackageName)
                if (packageManager.getLaunchIntentForPackage(appPackageName) != null){
                    appList.add(AppName(appPackageName, appName, appIcon))
                }
            } catch (e: PackageManager.NameNotFoundException) {
                // Handle exception if package information is not found
                e.printStackTrace()
            }

        }
        appList.sortBy { it.appName }
        return appList
    }

    override fun onPostExecute(result: MutableList<AppName>?) {
        super.onPostExecute(result)
        if (result != null) {
            listener.onApplicationList(result)
        }
    }
    interface OnFetchApplicationListListener {
        fun onApplicationList(applications: MutableList<AppName>)
    }
}