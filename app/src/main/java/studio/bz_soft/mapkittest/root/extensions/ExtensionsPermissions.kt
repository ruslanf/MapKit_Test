package studio.bz_soft.mapkittest.root.extensions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun permissionApi27() = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

@RequiresApi(Build.VERSION_CODES.O)
fun permissionApi28() = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

fun Context.checkSelfPermissionApi27(): Boolean =
    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED ||
    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED

@RequiresApi(Build.VERSION_CODES.O)
fun Context.checkSelfPermissionApi28(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED ||
    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED

fun getPermissionStatusApi27(activity: Activity): Boolean =
    checkRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION) ||
    checkRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

@RequiresApi(Build.VERSION_CODES.O)
fun getPermissionStatusApi28(activity: Activity): Boolean =
    checkRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION) ||
    checkRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

private fun checkRationale(activity: Activity, permission: String): Boolean =
    ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)