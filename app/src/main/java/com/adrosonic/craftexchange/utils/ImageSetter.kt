package com.adrosonic.craftexchange.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.adrosonic.craftexchange.R
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
//import com.synnapps.carouselview.CarouselView
import info.abdolahi.CircularMusicProgressBar

object ImageSetter {
    /**
     * This method will set image into ImageView without using placeholder.
     * it will handel caching.
     * */
    fun setImage(context: Context, imagePath:String, imageView: ImageView) {
        try
        {
            Glide.with(context)
                .load(imagePath) // it can be a remote URL or a local absolute file path.
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .override(Target.SIZE_ORIGINAL))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                .priority(Priority.IMMEDIATE)
                .skipMemoryCache(false)
                .dontAnimate()
                .into(imageView)
        }
        catch (ex:Exception) {
            ex.printStackTrace()
        }
    }
    /**
     * This method will set image into ImageView with loading time placeholder
     * and also with error.
     * */
    fun setImage(context:Context, imagePath:String, imageView:ImageView,placeholder:Int, errImage:Int, fallbck : Int) {
        try
        {
            Glide.with(context)
                .load(imagePath)
                .signature(ObjectKey((System.currentTimeMillis()).div(24*60*60*1000).toString())) //periodically refreshes the image for new
                .apply(
                    RequestOptions()
                        .circleCrop()
                        .skipMemoryCache(false)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .override(Target.SIZE_ORIGINAL))
                .placeholder(placeholder)
                .priority(Priority.IMMEDIATE)
                .error(errImage)
                .fallback(fallbck)
                .dontAnimate()
//                .listener(object:RequestListener<Drawable> {
//
//                    override fun onResourceReady(
//                        resource: Drawable?,
//                        model: Any?,
//                        target: Target<Drawable>?,
//                        dataSource: DataSource?,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        mProgress?.visibility = View.GONE
//                        return false
//                    }
//
//                    override fun onLoadFailed(
//                        e: GlideException?,
//                        model: Any?,
//                        target: Target<Drawable>?,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        mProgress?.visibility = View.GONE
//                        Log.e("Image loading exception",e?.printStackTrace().toString())
//                        return false
//                    }
//                })
                .into(imageView)

        }
        catch (ex:Exception) {
            ex.printStackTrace()
        }
    }

    fun setImageWithProgress(context:Context, imagePath:String, imageView:ImageView,mProgress : ProgressBar,placeholder:Int, errImage:Int, fallbck : Int) {
        try
        {
            Glide.with(context)
                .load(imagePath)
                .signature(ObjectKey((System.currentTimeMillis()).div(24*60*60*1000).toString())) //periodically refreshes the image for new
                .apply(
                    RequestOptions()
                        .circleCrop()
                        .skipMemoryCache(false)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .override(Target.SIZE_ORIGINAL))
                .placeholder(placeholder)
                .priority(Priority.IMMEDIATE)
                .error(errImage)
                .fallback(fallbck)
                .dontAnimate()
                .listener(object:RequestListener<Drawable> {

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        mProgress?.visibility = View.GONE
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        mProgress?.visibility = View.GONE
                        Log.e("Glide","Image loading exception "+e?.printStackTrace().toString())
                        return false
                    }
                })
                .into(imageView)

        }
        catch (ex:Exception) {
            ex.printStackTrace()
        }
    }


    fun setImageUri(context: Context, imagePath: Uri, imageView: ImageView, placeholder:Int, errImage:Int, fallbck : Int) {
        try
        {
            Glide.with(context)
                .load(imagePath)
                .apply(
                    RequestOptions()
                        .circleCrop()
                        .skipMemoryCache(false)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .override(Target.SIZE_ORIGINAL))
                .placeholder(placeholder)
//                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                .priority(Priority.IMMEDIATE)
                .error(errImage)
                .fallback(fallbck)
                .dontAnimate()
                .into(imageView!!)

        }
        catch (ex:Exception) {
            ex.printStackTrace()
        }
    }

    fun setImageCircleProgress(context:Context, imagePath:String, imageView:CircularMusicProgressBar?, placeholder:Int, errImage:Int, fallbck : Int) {
        try
        {
            Glide.with(context)
                .load(imagePath)
                .apply(
                    RequestOptions()
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .override(Target.SIZE_ORIGINAL))
                .placeholder(placeholder)
//                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                .priority(Priority.IMMEDIATE)
                .error(errImage)
                .fallback(fallbck)
                .skipMemoryCache(false)
                .dontAnimate()
                .into(imageView!!)

        }
        catch (ex:Exception) {
            ex.printStackTrace()
        }
    }
    /**
     * Load image with caching, placeholder and result callback.
     * */
//    fun setImage(context:Context, imagePath:String, imageView:ImageView, placeholder:Int, listener: RequestListener<String, GlideDrawable>) {
//        try
//        {
//            Glide.with(context)
//                .load(imagePath)
//                .placeholder(placeholder)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .priority(Priority.IMMEDIATE)
//                .error(placeholder)
//                .skipMemoryCache(false)
//                .dontAnimate()
//                .listener(listener)
//                .into(imageView)
//        }
//        catch (ex:Exception) {
//            ex.printStackTrace()
//        }
//    }

    //Set image in carousel view
//    fun setImage(context: Context, imagePath:String, imageView: Carou) {
//        try
//        {
//            Glide.with(context)
//                .load(imagePath) // it can be a remote URL or a local absolute file path.
//                .apply(
//                    RequestOptions()
//                        .centerCrop()
//                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                        .override(Target.SIZE_ORIGINAL))
//                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
////                .priority(Priority.IMMEDIATE)
//                .skipMemoryCache(false)
//                .dontAnimate()
//                .into(imageView)
//        }
//        catch (ex:Exception) {
//            ex.printStackTrace()
//        }
//    }

    /**
     * Download image from remote URL and get callback of result.
     * */
    fun downloadImage(context:Context, imageSource:String, listener: SimpleTarget<Bitmap>) {
        try
        {
            Glide.with(context)
                .asBitmap()
                .load(imageSource)
                .into(listener)
        }
        catch (ex:Exception) {
            ex.printStackTrace()
        }
    }
}