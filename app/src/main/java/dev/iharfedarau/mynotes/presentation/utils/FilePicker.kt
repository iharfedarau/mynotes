package dev.iharfedarau.mynotes.presentation.utils

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

//https://developer.android.com/training/data-storage/shared/documents-files#create-file
enum class FilePickerMode {
    CreateFile,
    OpenFile,
}

@Composable
fun FilePicker(
    show: Boolean,
    mode: FilePickerMode,
    onFileHandled: (FilePickerMode, Uri?) -> Unit
) {
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            onFileHandled(mode, result.data?.data)
        }

    val downloadDirPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
    val intent = Intent().apply {
        when (mode) {
            FilePickerMode.CreateFile -> {
                action = Intent.ACTION_CREATE_DOCUMENT
                putExtra(Intent.EXTRA_TITLE, "mynotes_backup.json")
            }

            FilePickerMode.OpenFile -> {
                action = Intent.ACTION_OPEN_DOCUMENT
            }
        }
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "application/json"

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker before your app creates the document.
        putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse(downloadDirPath))
    }
    LaunchedEffect(show) {
        if (show) {
            launcher.launch(intent)
        }
    }
}

fun isExternalStorageReadOnly(): Boolean {
    return Environment.MEDIA_MOUNTED_READ_ONLY == Environment.getExternalStorageState()
}

fun isExternalStorageAvailable(): Boolean {
    return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
}