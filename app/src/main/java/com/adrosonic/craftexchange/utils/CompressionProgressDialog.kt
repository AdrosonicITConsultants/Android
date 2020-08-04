package com.adrosonic.craftexchange.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import com.adrosonic.craftexchange.R

class CompressionProgressDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layoutInflater : LayoutInflater = LayoutInflater.from(activity as Context?)
        val view : View = layoutInflater.inflate(R.layout.dialog_compressionprogress,null,false)
        val builder : android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(activity)
        builder.setView(view)
        return builder.create()
    }
}