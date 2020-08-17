package com.adrosonic.craftexchange.ui.modules.editPhoto

import android.R.attr.bitmap
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

            sbAdjustBrightness.setProgress(125)
            sbAdjustBrightness.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int,fromUser: Boolean) {
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
    fun enhanceImage(
        mBitmap: Bitmap,
        contrast: Float,
        brightness: Float
    ): Bitmap? {
        val cm = ColorMatrix(floatArrayOf(
                contrast,  0f, 0f,  0f,
                brightness,  0f, contrast, 0f,
                0f, brightness, 0f, 0f,
                contrast, 0f,  brightness,0f,
                0f,  0f, 1f, 0f)  )
        val mEnhancedBitmap = Bitmap.createBitmap(
            mBitmap.width, mBitmap.height, mBitmap
                .config
        )
        imgEditable.setImageBitmap(mEnhancedBitmap)
        updatedBitmap=mEnhancedBitmap
//        val canvas = Canvas(mEnhancedBitmap)
//        val paint = Paint()
//        paint.setColorFilter(ColorMatrixColorFilter(cm))
        return mEnhancedBitmap
    }
}