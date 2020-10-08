package com.adrosonic.craftexchangemarketing.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import java.io.*

interface CompressTaskResult {
    fun performFinalTask(result: ArrayList<String>)
}

class CompressImageTask(var cacheDir: String,  pairList: ArrayList<String>,
                        var compressTaskResult: CompressTaskResult) : AsyncTask<Unit, Unit, ArrayList<String>>() {
    val iterator = pairList.iterator()
    var compressAttachment=ArrayList<String>()
    override fun doInBackground(vararg params: Unit?): ArrayList<String> {
        try {
            compressAttachment.clear()
            var directory: File?
            var counter = 0
            var bitmap: Bitmap? = null
            while (iterator.hasNext()) {
                counter++
                val item = iterator.next()
                    val file = File(item)
                    var imageName: String
                    if (file.exists()) {
                        val o = BitmapFactory.Options().apply {
                            inJustDecodeBounds = true
                        }
                        BitmapFactory.decodeFile(file.absolutePath, o)
                        var fis: FileInputStream
                        try {
                            fis = FileInputStream(file)
                            BitmapFactory.decodeStream(fis, null, o)
                            fis.close()
                        } catch (e: IOException) {

                        }
                        val IMAGE_MAX_SIZE = 1024 * 1024
                        var scale: Int
                        scale = 1
                        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                            scale = Math.pow(2.0, Math.ceil(Math.log((IMAGE_MAX_SIZE / Math.max(o.outHeight, o.outWidth)).toDouble()) / Math.log(0.5))).toInt()
                        }
                        var o2 = BitmapFactory.Options()
                        o2.inSampleSize = scale
                        try {
                            fis = FileInputStream(file)
                            bitmap = BitmapFactory.decodeStream(fis, null, o2)
                            fis.close()
                        } catch (e: FileNotFoundException) {
                            Log.e("FileNotFoundException", e.message)
                        } catch (e: IOException) {
                            Log.e("Compress IOException", e.message)
                        }
                        directory = File(cacheDir, "SentImages/")
//                            Log.i("Path",directory.absolutePath)
                        if (!directory.exists()) {
                            directory.mkdir()
                        }
                        imageName = "${file.nameWithoutExtension}$counter.jpeg"
                        var out: FileOutputStream? = null
                        try {
                            val compressFile = File(directory.absolutePath, imageName)
                            compressFile.createNewFile()
                            out = FileOutputStream(compressFile)
                            bitmap?.compress(Bitmap.CompressFormat.JPEG, 45, out)
                        } catch (e: Exception) {
                            Log.e("Unable to Compress", "${file.nameWithoutExtension}$counter.jpeg")
                            Log.e("Unable to Compress", e.message)
                            return arrayListOf()
                        } finally {
                            try {
                                out?.flush()
                                out?.close()
                                compressAttachment.add(directory.absolutePath + "/" + imageName)
                                bitmap?.recycle()
                            } catch (e: Exception) {
                                Log.e("Compress IOException", e.message)
                                return arrayListOf()
                            }
                        }
                    }
                }
            return compressAttachment

        } catch (e: Exception) {
            Log.e("Memory Exception", e.message)
            return arrayListOf()
        }
    }

    override fun onPostExecute(result: ArrayList<String>) {
        super.onPostExecute(result)
        try {
            compressTaskResult.performFinalTask(result)
        } catch (e: Exception) {
            Log.e("", e.message)
        }
    }

}