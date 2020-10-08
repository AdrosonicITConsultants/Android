package com.adrosonic.craftexchangemarketing.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.appcompat.widget.AppCompatSpinner
import java.util.*
import kotlin.collections.ArrayList

class MultiSelectionSpinner: AppCompatSpinner, DialogInterface.OnMultiChoiceClickListener {
    private var items:Array<String> ?  = null
    private var mSelection:BooleanArray ?  = null
    private var simpleAdapter: ArrayAdapter<String>

    val selectedStrings:List<String>
        get() {
            val selection = LinkedList<String>()
            for (i in items!!.indices)
            {
                if (mSelection?.get(i)!!)
                {
                    selection.add(items!![i])
                }
            }
            return selection
        }

    val selectedIndicies: ArrayList<Long>
        get() {
            val selection = ArrayList<Long>()
            for (i in items!!.indices)
            {
                if (mSelection?.get(i)!!)
                {
                    selection.add(i.inc().toLong())
                }
            }
            return selection
        }

    val selectedItemsAsString:String
        get() {
            val sb = StringBuilder()
            var foundOne = false
            for (i in items!!.indices)
            {
                if (mSelection?.get(i)!!)
                {
                    if (foundOne)
                    {
                        sb.append(", ")
                    }
                    foundOne = true
                    sb.append(items!![i])
                }
            }
            return sb.toString()
        }
    constructor(context: Context) : super(context) {
        simpleAdapter = ArrayAdapter<String>(context,
            android.R.layout.simple_spinner_item)
        super.setAdapter(simpleAdapter)
    }
    constructor(context:Context, attrs: AttributeSet) : super(context, attrs) {
        simpleAdapter = ArrayAdapter<String>(context,
            android.R.layout.simple_spinner_item)
        super.setAdapter(simpleAdapter)
    }
    override fun onClick(dialog:DialogInterface, which:Int, isChecked:Boolean) {
        if (mSelection != null && which < mSelection!!.size)
        {
            mSelection!![which] = isChecked
            simpleAdapter.clear()
            simpleAdapter.add(buildSelectedItemString())
        }
        else
        {
            throw IllegalArgumentException(
                "Argument 'which' is out of bounds.")
        }
    }
    override fun performClick():Boolean {
        val builder = AlertDialog.Builder(context)
        builder.setMultiChoiceItems(items, mSelection, this)
        builder.show()
        return true
    }

    override fun setAdapter(adapter: SpinnerAdapter) {
        throw RuntimeException(
            "setAdapter is not supported by MultiSelectSpinner.")
    }

    fun setItems(items:Array<String>) {
        this.items = items
        mSelection = BooleanArray(this.items!!.size)
        simpleAdapter.clear()
        simpleAdapter.add(this.items!![0])
        Arrays.fill(mSelection, false)
    }

    fun setItems(items:List<String>) {
        this.items = items.toTypedArray()

        mSelection = BooleanArray(this.items!!.size)
        simpleAdapter.clear()
        simpleAdapter.add(this.items!![0])
        Arrays.fill(mSelection, false)
    }
    fun setSelection(selection:Array<String>) {
        for (cell in selection)
        {
            for (j in items!!.indices)
            {
                if (items!![j] == cell)
                {
                    mSelection?.set(j, true)
                }
            }
        }
    }
    fun setSelection(selection:List<String>) {
        for (i in mSelection!!.indices)
        {
            mSelection!![i] = false
        }
        for (sel in selection)
        {
            for (j in items!!.indices)
            {
                if (items!![j] == sel)
                {
                    mSelection!![j] = true
                }
            }
        }
        simpleAdapter.clear()
        simpleAdapter.add(buildSelectedItemString())
    }
    override fun setSelection(index:Int) {
        for (i in mSelection!!.indices)
        {
            mSelection?.set(i, false)
        }
        if (index >= 0 && index < mSelection!!.size)
        {
            mSelection!![index] = true
        }
        else
        {
            throw IllegalArgumentException(("Index " + index
                    + " is out of bounds."))
        }
        simpleAdapter.clear()
        simpleAdapter.add(buildSelectedItemString())
    }
    fun setSelection(selectedIndicies:IntArray) {
        for (i in mSelection!!.indices)
        {
            mSelection!![i] = false
        }
        for (index in selectedIndicies)
        {
            if (index >= 0 && index < mSelection!!.size)
            {
                mSelection!![index] = true
            }
            else
            {
                throw IllegalArgumentException(("Index " + index
                        + " is out of bounds."))
            }
        }
        simpleAdapter.clear()
        simpleAdapter.add(buildSelectedItemString())
    }
    private fun buildSelectedItemString():String {
        val sb = StringBuilder()
        var foundOne = false
        for (i in items!!.indices)
        {
            if (mSelection?.get(i)!!)
            {
                if (foundOne)
                {
                    sb.append(", ")
                }
                foundOne = true
                sb.append(items!![i])
            }
        }
        return sb.toString()
    }
}