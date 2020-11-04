package com.adrosonic.craftexchange.ui.modules.artisan.qcForm.adapter

import java.util.HashMap
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.repository.data.request.qc.QuestionAnswer

class QcExpandableListAdapter(context:Context, maxStageID : Long, enqID : Long, expandableListTitle:ArrayList<String>, expandableListDetail:ArrayList<QuestionAnswer>):BaseExpandableListAdapter() {
    private var context:Context = context
    private var expandableListTitle:ArrayList<String> = expandableListTitle
    private var expandableListDetail:ArrayList<QuestionAnswer> = expandableListDetail
    private var maxStageID: Long = maxStageID
    private var enqID: Long = enqID

    override fun getGroupView(listPosition:Int, isExpanded:Boolean, convertView:View, parent:ViewGroup):View {
        var convertView = convertView
        val listTitle = getGroup(listPosition) as String
        if (convertView == null)
        {
            convertView = LayoutInflater.from(parent.context).inflate(R.layout.item_qc_form_view_header, parent, false)
        }
        val listTitleTextView = convertView.findViewById(R.id.qc_stage) as TextView
        listTitleTextView.text = "listTitle"
        return convertView
    }

    override fun getChildView(listPosition:Int, expandedListPosition:Int, isLastChild:Boolean, convertView:View, parent:ViewGroup):View {
        var convertView = convertView
        if (convertView == null)
        {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.item_qc_form_view_listitem, null)
        }
        var expQuestion = convertView.findViewById(R.id.exp_ques_text) as TextView
        var expAnswer = convertView.findViewById(R.id.exp_ans_text) as TextView

        return convertView
    }

    override fun getChild(listPosition:Int, expandedListPosition:Int): QuestionAnswer? {
        return this.expandableListDetail?.get(expandedListPosition)
    }
    override fun getChildId(listPosition:Int, expandedListPosition:Int):Long {
        return expandedListPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return this.expandableListTitle.size
    }

    override fun getChildrenCount(listPosition:Int): Int {
        return this.expandableListDetail?.size!!
    }
    override fun getGroup(listPosition:Int):Any {
        return this.expandableListTitle[listPosition]
    }
    override fun getGroupId(listPosition:Int):Long {
        return listPosition.toLong()
    }
    override fun hasStableIds():Boolean {
        return false
    }
    override fun isChildSelectable(listPosition:Int, expandedListPosition:Int):Boolean {
        return true
    }
}