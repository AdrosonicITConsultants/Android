package com.adrosonic.craftexchange.ui.modules.artisan.productTemplate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.ui.modules.editPhoto.EditPhotoActivity
import com.adrosonic.craftexchange.ui.modules.editPhoto.editPhotoIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import kotlinx.android.synthetic.main.activity_edit_photo.*
import java.io.File

class ProdImageListAdapter(
    private val context: Context,
    private val isTemplate:Boolean,
    private val pairList: ArrayList<Triple<Boolean,Long, String>>
) : RecyclerView.Adapter<ProdImageListAdapter.MyViewHolder>() {

    interface ProdUpdateListener {
        fun onUpdate(pairList: ArrayList<Triple<Boolean,Long, String>>, deletedIds: ArrayList<Pair<Long,String>>)
    }

    var listener: ProdUpdateListener? = null
    private var deletedPaths = ArrayList<Pair<Long,String>>()

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var thumbnail: ImageView
        var removeProductImg: ImageView
        var editProductImg: ImageView

        init {
            thumbnail = view.findViewById(R.id.img_thumbnail)
            removeProductImg = view.findViewById(R.id.img_delete)
            editProductImg = view.findViewById(R.id.img_edit)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_add_product_phot, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item = pairList.get(position)
        var file = File(item.third)
        Log.e("ProdPath", "" + item.third)
        if(item.first){
            var url = if(isTemplate)Utility.getProductsImagesUrl(item.second,item.third) else Utility.getCustomProductImagesUrl(item.second,item.third)
            context?.let { ImageSetter.setImage(it,url,holder.thumbnail)
            }
        }else  (holder.thumbnail).setImageURI(Uri.fromFile(file))
//            ImageSetter.setImage(context, file.absolutePath, holder.thumbnail)

        holder.removeProductImg.setOnClickListener {
            var pos = position
            removeItem(pos)
        }
        if(!item.first) {
            holder.editProductImg.setOnClickListener {
//                context?.startActivity(context?.editPhotoIntent(file.absolutePath))
                val origin = it.context as Activity
                val intent = Intent(context!!, EditPhotoActivity::class.java)
                val bundle = Bundle()
                bundle.putString(ConstantsDirectory.EDIT_PATH, file.absolutePath)
                bundle.putInt(ConstantsDirectory.EDIT_IMAGE_POSITION,position)
                intent.putExtras(bundle)
                origin.startActivityForResult(intent,ConstantsDirectory.EDIT_IMAGE)
            }
        }
    }

    override fun getItemCount(): Int {
        return pairList.size
    }

    fun removeItem(position: Int) {
        deletedPaths.add(Pair(pairList.get(position).second,pairList.get(position).third))
        pairList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, pairList.size)
        listener?.onUpdate(pairList, deletedPaths)
    }

    fun viewImage(strfile: String) {
        val file = File(strfile)
        if (file.exists()) {
            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
            val myIntent = Intent(Intent.ACTION_EDIT)
            myIntent.putExtra(ShareCompat.EXTRA_CALLING_PACKAGE, context.getPackageName())
            val componentName = (context as Activity).getComponentName()
            myIntent.putExtra(ShareCompat.EXTRA_CALLING_ACTIVITY, componentName)
            myIntent.setDataAndType(uri, "image/*")
            myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(Intent.createChooser(myIntent, "Open with"))
        }
    }
}