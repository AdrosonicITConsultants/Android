package com.adrosonic.craftexchange.ui.modules.artisan.productTemplate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.utils.ImageSetter
import java.io.File

class ProdImageListAdapter(
    private val context: Context,
    private val pairList: ArrayList<String>
) : RecyclerView.Adapter<ProdImageListAdapter.MyViewHolder>() {

    interface ProdUpdateListener {
        fun onUpdate(pairList: ArrayList<String>, deletedIds: ArrayList<String>)
    }

    var listener: ProdImageListAdapter.ProdUpdateListener? = null
    private var deletedPaths = ArrayList<String>()

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
        var file = File(item)
        Log.e("ProdPath", "" + item)
        ImageSetter.setImage(context, file.absolutePath, holder.thumbnail)

        holder.thumbnail.setOnClickListener {
            viewImage(item)
        }
        holder.removeProductImg.setOnClickListener {
            var pos = position
            removeItem(pos)
        }
        holder.editProductImg.setOnClickListener {
        }

    }

    override fun getItemCount(): Int {
        return pairList.size
    }

    fun removeItem(position: Int) {
        pairList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, pairList.size)
        listener?.onUpdate(pairList, deletedPaths)
    }

    fun viewImage(strfile: String) {
        val file = File(strfile)
        if (file.exists()) {
            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
            val myIntent = Intent(Intent.ACTION_VIEW)
            myIntent.putExtra(ShareCompat.EXTRA_CALLING_PACKAGE, context.getPackageName())
            val componentName = (context as Activity).getComponentName()
            myIntent.putExtra(ShareCompat.EXTRA_CALLING_ACTIVITY, componentName)
            myIntent.setDataAndType(uri, "image/*")
            myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(Intent.createChooser(myIntent, "Open with"))
        }
    }
}