package com.adrosonic.craftexchange.ui.modules.editPhoto

import android.R.attr.src
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_edit_photo.*
import java.io.File


fun Context.editPhotoIntent(location: String): Intent {
    val intent = Intent(this, EditPhotoActivity::class.java)
    intent.putExtra(ConstantsDirectory.EDIT_PATH, location)

    return intent.apply { }
}

class EditPhotoActivity : AppCompatActivity() {
    var photoPath=""
    var position=0
    lateinit var updatedUri:Uri
    lateinit var updatedBitmap: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_photo)

        if (intent.extras != null) {
            photoPath = intent.getStringExtra(ConstantsDirectory.EDIT_PATH)?:""
            position = intent.getIntExtra(ConstantsDirectory.EDIT_IMAGE_POSITION,0)
            Log.e("EditPhotoActivity", "template activity prodId :" + photoPath)
            var file = File(photoPath)
            updatedUri=Uri.fromFile(file)
            photoPath?.let {  (imgEditable).setImageURI(updatedUri)}

            saveImage.setOnClickListener {
                var bitmaps= (imgEditable.drawable as BitmapDrawable).bitmap
                Utility.overrideFileFromUri(this,bitmaps,file.name)
//                Utility.overrideFileFromUri(this,updatedUri,file.name)
                val resultIntent = Intent()
                resultIntent.putExtra(ConstantsDirectory.EDIT_IMAGE_POSITION, position)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
            imgCrop.setOnClickListener {
                CropImage.activity(Uri.fromFile(file)).setGuidelines(CropImageView.Guidelines.ON).start(this)
            }
            imgRotate.setOnClickListener {
                CropImage.activity(Uri.fromFile(file)).setGuidelines(CropImageView.Guidelines.ON).start(this)
            }
            imgBrightness.setOnClickListener {
                if(sbAdjustBrightness.visibility==View.VISIBLE)sbAdjustBrightness.visibility=View.GONE
                else sbAdjustBrightness.visibility=View.VISIBLE
            }

//            sbAdjustBrightness.setMax(4);
            sbAdjustBrightness.setProgress(125);
            sbAdjustBrightness.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int,fromUser: Boolean) {
//                    var res= changeBitmapContrastBrightness( (imgEditable.drawable as BitmapDrawable).bitmap, 1f, seekBar.progress.toFloat())
//                    imgEditable.setImageBitmap(res)
                    var res=setBrightness(progress)
                    imgEditable.setColorFilter(res)
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })

        }

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode,resultCode,data)
       if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                (imgEditable).setImageURI(result.uri)
                updatedUri=result.uri
                Log.e("EditPhotoActivity", "updatedUri :" + updatedUri.lastPathSegment)
//                Utility.displayMessage("Cropping successful, Sample: " + result.sampleSize,this)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.error, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun setBrightness(progress: Int): PorterDuffColorFilter? {
        return if (progress >= 100) {
            val value = (progress - 100) * 255 / 100
            PorterDuffColorFilter(Color.argb(value, 255, 255, 255), PorterDuff.Mode.SRC_OVER)
        } else {
            val value = (100 - progress) * 255 / 100
            PorterDuffColorFilter(Color.argb(value, 0, 0, 0), PorterDuff.Mode.SRC_ATOP)
        }
    }
    private fun adjustedContrast(src: Bitmap, value: Double): Bitmap? {
        // image size
        val width = src.width
        val height = src.height
        // create output bitmap

        // create a mutable empty bitmap
        val bmOut = Bitmap.createBitmap(width, height, src.config)

        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        val c = Canvas()
        c.setBitmap(bmOut)

        // draw bitmap to bmOut from src bitmap so we can modify it
        c.drawBitmap(src, 0f, 0f, Paint(Color.BLACK))


        // color information
        var A: Int
        var R: Int
        var G: Int
        var B: Int
        var pixel: Int
        // get contrast value
        val contrast = Math.pow((100 + value) / 100, 2.0)

        // scan through all pixels
        for (x in 0 until width) {
            for (y in 0 until height) {
                // get pixel color
                pixel = src.getPixel(x, y)
                A = Color.alpha(pixel)
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel)
                R = (((R / 255.0 - 0.5) * contrast + 0.5) * 255.0).toInt()
                if (R < 0) {
                    R = 0
                } else if (R > 255) {
                    R = 255
                }
                G = Color.green(pixel)
                G = (((G / 255.0 - 0.5) * contrast + 0.5) * 255.0).toInt()
                if (G < 0) {
                    G = 0
                } else if (G > 255) {
                    G = 255
                }
                B = Color.blue(pixel)
                B = (((B / 255.0 - 0.5) * contrast + 0.5) * 255.0).toInt()
                if (B < 0) {
                    B = 0
                } else if (B > 255) {
                    B = 255
                }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B))
            }
        }
        return bmOut
    }
    fun changeBitmapContrastBrightness(
        bmp: Bitmap,
        contrast: Float,
        brightness: Float
    ): Bitmap? {
        val cm = ColorMatrix(
            floatArrayOf(
                contrast,
                0f,
                0f,
                0f,
                brightness,
                0f,
                contrast,
                0f,
                0f,
                brightness,
                0f,
                0f,
                contrast,
                0f,
                brightness,
                0f,
                0f,
                0f,
                1f,
                0f
            )
        )

        var config: Bitmap.Config = bmp.getConfig()
        if (config == null) {
            config = Bitmap.Config.ARGB_8888
        }
//        val ret = Bitmap.createBitmap(src.getWidth(), src.getHeight(), config)

        val ret = Bitmap.createBitmap(bmp.width, bmp.height, config)
        val canvas = Canvas(ret)
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(bmp, 0f, 0f, paint)
        return ret
    }
}