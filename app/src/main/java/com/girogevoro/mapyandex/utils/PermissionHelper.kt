package com.girogevoro.mapyandex.utils

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale

/**
 * Invoke fun check()
 * Functions call callbacks listener onSuccessful or onUnsuccessful
 * If permissions were already obtained earlier when calling this function,
 * callbacks are no longer called
 */
interface PermissionHelper {
    fun setOnSuccessful(listener: () -> Unit)
    fun setOnUnsuccessful(listener: () -> Unit)
    fun check(isForce: Boolean = false)

    fun isGranted(): Boolean
}

class PermissionHelperImpl(
    private val activity: AppCompatActivity,
    private val permissions: Array<String>,
    private val texts: Texts,
) : PermissionHelper {
    private var onSuccessful: (() -> Unit)? = null
    private var onUnsuccessful: (() -> Unit)? = null

    private var permissionsForRequestList: MutableList<String> = mutableListOf()
    private var isPermissionsGranted = false
    private var isRationaleRequest = false
    private var isStarted = false

    private val requestPermissionLauncher =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions: Map<String, Boolean> ->
            if (permissions.all { it.value }) {
                sendSuccessful()
            } else if (!isRationaleRequest) {
                if (isShouldShowRequestPermissionRationale()) {
                    createAlertDialogRationale()
                } else {
                    createAlertDialogOpenAppSettings()
                }
            } else {
                createAlertDialogUnsuccessful()
            }
        }

    override fun setOnSuccessful(listener: () -> Unit) {
        onSuccessful = listener
    }

    override fun setOnUnsuccessful(listener: () -> Unit) {
        onUnsuccessful = listener
    }

    override fun check(isForce: Boolean) {
        if ((!isGranted() && !isStarted) || isForce) {
            isStarted = true
            runCheck()
        }
    }

    private fun runCheck() {
        if (isPermissionForRequest()) {
            isRationaleRequest = isShouldShowRequestPermissionRationale()
            if (isRationaleRequest) {
                createAlertDialogRationale()
            } else {
                request()
            }
        } else {
            sendSuccessful()
        }
    }

    override fun isGranted() = isPermissionsGranted

    private fun request() {
        requestPermissionLauncher.launch(permissionsForRequestList.toTypedArray())
    }

    private fun sendSuccessful() {
        isPermissionsGranted = true
        isStarted = false
        onSuccessful?.invoke()
    }

    private fun sendUnsuccessful() {
        isPermissionsGranted = false
        isStarted = false
        onUnsuccessful?.invoke()
    }


    private fun isPermissionForRequest(): Boolean {
        permissionsForRequestList.clear()
        permissions.forEach {
            if (activity.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED) {
                permissionsForRequestList.add(it)
            }
        }
        return permissionsForRequestList.isNotEmpty()
    }

    private fun isShouldShowRequestPermissionRationale(): Boolean {
        return permissionsForRequestList.any {
            shouldShowRequestPermissionRationale(activity, it)
        }
    }

    private fun openAppSetting() {
        activity.startActivity(Intent().apply {
            action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.parse("package:" + activity.packageName)
        })
    }

    private fun createAlertDialogRationale() {
        AlertDialog.Builder(activity)
            .setTitle(texts.dialogsTitle)
            .setMessage(texts.dialogRationaleMessage)
            .setPositiveButton(texts.resume) { _, _ ->
                request()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                createAlertDialogUnsuccessful()
                dialog.dismiss()
            }
            .show()
    }

    private fun createAlertDialogOpenAppSettings() {
        AlertDialog.Builder(activity)
            .setTitle(texts.dialogsTitle)
            .setMessage(texts.dialogOpenAppSettingsMessage)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                sendUnsuccessful()
                openAppSetting() // открываем настройки приложения
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                createAlertDialogUnsuccessful()
                dialog.dismiss()
            }
            .show()
    }

    private fun createAlertDialogUnsuccessful() {
        AlertDialog.Builder(activity)
            .setTitle(texts.dialogsTitle)
            .setMessage(texts.dialogUnsuccessfulMessage)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                sendUnsuccessful()
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    data class Texts(
        val dialogsTitle: String,
        val dialogRationaleMessage: String,
        val dialogOpenAppSettingsMessage: String,
        val dialogUnsuccessfulMessage: String,
        val resume: String = "resume",
    )
}