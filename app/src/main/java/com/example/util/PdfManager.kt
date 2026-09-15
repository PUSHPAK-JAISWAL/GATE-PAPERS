package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit

object PdfManager {
    private const val TAG = "PdfManager"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    fun getLocalCachedFile(context: Context, fileName: String): File {
        val pdfDir = File(context.cacheDir, "downloaded_pdfs")
        if (!pdfDir.exists()) {
            pdfDir.mkdirs()
        }
        return File(pdfDir, fileName)
    }

    fun isPdfCached(context: Context, fileName: String): Boolean {
        val file = getLocalCachedFile(context, fileName)
        return file.exists() && file.length() > 0
    }

    /**
     * Downloads the PDF from raw GitHub URL or uses existing cached copy.
     */
    suspend fun downloadOrGetCachedPdf(
        context: Context,
        rawUrl: String,
        fileName: String,
        onProgress: (Float) -> Unit = {}
    ): File? = withContext(Dispatchers.IO) {
        val targetFile = getLocalCachedFile(context, fileName)

        // If already cached and valid size (> 100 bytes), return it immediately
        if (targetFile.exists() && targetFile.length() > 500) {
            onProgress(1f)
            return@withContext targetFile
        }

        try {
            val request = Request.Builder()
                .url(rawUrl)
                .header("User-Agent", "GatePapersApp")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.e(TAG, "Download failed with HTTP ${response.code} for $rawUrl")
                return@withContext null
            }

            val body = response.body ?: return@withContext null
            val contentLength = body.contentLength()

            val inputStream: InputStream = body.byteStream()
            val tempFile = File(context.cacheDir, "${fileName}.tmp")
            val outputStream = FileOutputStream(tempFile)

            val buffer = ByteArray(8192)
            var totalBytesRead = 0L
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
                if (contentLength > 0) {
                    val progress = (totalBytesRead.toFloat() / contentLength).coerceIn(0f, 1f)
                    onProgress(progress)
                }
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            // Rename temp to target
            if (tempFile.renameTo(targetFile)) {
                onProgress(1f)
                return@withContext targetFile
            } else {
                tempFile.copyTo(targetFile, overwrite = true)
                tempFile.delete()
                onProgress(1f)
                return@withContext targetFile
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception downloading PDF $fileName", e)
            return@withContext null
        }
    }

    /**
     * Opens the PDF in external viewer (Drive, Adobe, Chrome) using FileProvider.
     */
    fun openWithExternalApp(context: Context, file: File): Boolean {
        return try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(Intent.createChooser(intent, "Open PDF with...").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error opening PDF externally", e)
            Toast.makeText(context, "No PDF viewer app found on device", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * Prints the authentic PDF document directly via Android PrintManager.
     */
    fun printPdfDocument(context: Context, file: File, jobTitle: String = "GATE Question Paper") {
        try {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            if (printManager == null) {
                Toast.makeText(context, "Printing service unavailable", Toast.LENGTH_SHORT).show()
                return
            }

            val printAttributes = PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build()

            val adapter = object : PrintDocumentAdapter() {
                override fun onLayout(
                    oldAttributes: PrintAttributes?,
                    newAttributes: PrintAttributes?,
                    cancellationSignal: CancellationSignal?,
                    callback: LayoutResultCallback?,
                    extras: Bundle?
                ) {
                    if (cancellationSignal?.isCanceled == true) {
                        callback?.onLayoutCancelled()
                        return
                    }
                    val info = PrintDocumentInfo.Builder(file.name)
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .build()
                    callback?.onLayoutFinished(info, true)
                }

                override fun onWrite(
                    pages: Array<out PageRange>?,
                    destination: ParcelFileDescriptor?,
                    cancellationSignal: CancellationSignal?,
                    callback: WriteResultCallback?
                ) {
                    var input: FileInputStream? = null
                    var output: FileOutputStream? = null
                    try {
                        input = FileInputStream(file)
                        output = FileOutputStream(destination?.fileDescriptor)
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } >= 0) {
                            if (cancellationSignal?.isCanceled == true) {
                                callback?.onWriteCancelled()
                                return
                            }
                            output.write(buffer, 0, bytesRead)
                        }
                        callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                    } catch (e: Exception) {
                        callback?.onWriteFailed(e.message)
                    } finally {
                        input?.close()
                        output?.close()
                    }
                }
            }

            printManager.print(jobTitle, adapter, printAttributes)
        } catch (e: Exception) {
            Log.e(TAG, "Error initiating PDF print", e)
            Toast.makeText(context, "Error initiating print: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Saves the downloaded PDF to the device's public Downloads directory.
     */
    suspend fun savePdfToPublicDownloads(context: Context, file: File, displayName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/GATE_Papers")
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return@withContext false
                resolver.openOutputStream(uri)?.use { out ->
                    FileInputStream(file).use { input ->
                        input.copyTo(out)
                    }
                }
                return@withContext true
            } else {
                val targetDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val targetFile = File(targetDir, displayName)
                file.copyTo(targetFile, overwrite = true)
                return@withContext true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving PDF to public downloads", e)
            return@withContext false
        }
    }

    /**
     * Renders a single page of a PDF file to a Bitmap.
     */
    fun renderPageToBitmap(pdfFile: File, pageIndex: Int, targetWidth: Int = 1080): Bitmap? {
        return try {
            val pfd = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(pfd)
            if (pageIndex < 0 || pageIndex >= renderer.pageCount) {
                renderer.close()
                pfd.close()
                return null
            }
            val page = renderer.openPage(pageIndex)
            val ratio = page.height.toFloat() / page.width.toFloat()
            val height = (targetWidth * ratio).toInt()
            val bitmap = Bitmap.createBitmap(targetWidth, height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            renderer.close()
            pfd.close()
            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Error rendering page $pageIndex of ${pdfFile.name}", e)
            null
        }
    }

    /**
     * Returns total page count of a PDF file.
     */
    fun getPageCount(pdfFile: File): Int {
        return try {
            val pfd = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(pfd)
            val count = renderer.pageCount
            renderer.close()
            pfd.close()
            count
        } catch (e: Exception) {
            Log.e(TAG, "Error reading page count", e)
            0
        }
    }
}
