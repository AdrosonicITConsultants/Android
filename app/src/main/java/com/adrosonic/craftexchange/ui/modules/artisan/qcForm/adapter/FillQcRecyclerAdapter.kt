package com.adrosonic.craftexchange.ui.modules.artisan.qcForm.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.forEach
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.predicates.QcPredicates
import com.adrosonic.craftexchange.enums.ActionForm
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.repository.data.request.qc.QuestionAnswer
import com.adrosonic.craftexchange.repository.data.response.qc.QuestionListData
import com.adrosonic.craftexchange.utils.Utility
import com.wajahatkarim3.easyvalidation.core.view_ktx.contains

class FillQcRecyclerAdapter(
    var context: Context?,
    private var qcDetails1: ArrayList<QuestionListData>,
    var maxQCStageID: Long, var enqID: Long
) : RecyclerView.Adapter<FillQcRecyclerAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var quesText: TextView = view.findViewById(R.id.ques_text)
        var ansText: EditText = view.findViewById(R.id.ans_text)
        var addrText: EditText = view.findViewById(R.id.address_text)
        var chckBoxGrp: LinearLayout = view.findViewById(R.id.ans_chckBox)
        var radiogrp: RadioGroup = view.findViewById(R.id.ans_radio)
        var ansList: Spinner = view.findViewById(R.id.ans_list)
        var addressLayout: LinearLayout = view.findViewById(R.id.address_layout)
    }

    interface UpdateQuesAnsInterface{
        fun addQuesAns(listElement: QuestionAnswer)
//        fun removeQuesAns(listElement : QuestionAnswer)
    }
   private var qcDetails= qcDetails1
    var qcAdapterListener: UpdateQuesAnsInterface?=null
    var ansOptList = ArrayList<Triple<Long, String, String?>>()
    override fun getItemCount(): Int {
        return qcDetails?.size ?: 0
    }

    fun updateQcForm(newList: ArrayList<QuestionListData>?, newMaxStageID: Long) {
        if (newList != null) {
//            this.qcDetails=newList
            this.qcDetails.addAll(newList)
        }
        if (newMaxStageID != null) {
            this.maxQCStageID = newMaxStageID
        }
        this.notifyDataSetChanged()
    }

    fun refreshAdapter(){
        this.qcDetails?.clear()
        ansOptList?.clear()
        Log.e("QcList","refreshAdapter qcDetails: ${qcDetails.size}")
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_qc_form_fill,
            parent,
            false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var qcQues = qcDetails?.get(position)
        var maxQCStageID = maxQCStageID
        var quesNo = position?.plus(1)?.toLong()
        var enquiryID = enqID

        var qcObj = QcPredicates.getQcResponsesByEnq(enquiryID)

        var quesDataItr1 = Utility.getQcQuesData()?.data?.iterator()

        quesDataItr1?.forEach { it ->
            it.forEach {
                if (
                    it.stageId == maxQCStageID) {
                    if (it.questionNo == quesNo) {
                        holder.quesText.text = it.question
                        ansOptList.add(Triple(it.questionNo, it.answerType, it.optionValue))

                        when (it.answerType) {
                            "0" -> {
                                if(maxQCStageID == 7L && it.questionNo == 12L){
                                    holder.ansText?.visibility = View.GONE
                                    holder.radiogrp?.visibility = View.GONE
                                    holder.ansList.visibility = View.GONE
                                    holder.chckBoxGrp.visibility = View.GONE
                                    holder.addressLayout.visibility = View.VISIBLE
                                }else{
                                    holder.ansText?.visibility = View.VISIBLE
                                    holder.radiogrp?.visibility = View.GONE
                                    holder.ansList.visibility = View.GONE
                                    holder.chckBoxGrp.visibility = View.GONE
                                    holder.addressLayout.visibility = View.GONE
                                }
                            }
                            "1" -> {
                                holder.ansText?.visibility = View.GONE
                                holder.radiogrp?.visibility = View.GONE
                                holder.ansList.visibility = View.GONE
                                holder.chckBoxGrp.visibility = View.VISIBLE
                                holder.addressLayout.visibility = View.GONE

                                setCheckBox(holder, it.optionValue,"")
                            }
                            "2" -> {
                                holder.ansText?.visibility = View.GONE
                                holder.radiogrp?.visibility = View.VISIBLE
                                holder.ansList.visibility = View.GONE
                                holder.chckBoxGrp.visibility = View.GONE
                                holder.addressLayout.visibility = View.GONE

                                setRadioButtons(holder, it.optionValue,"")
                            }
                            "3" -> {
                                holder.ansText?.visibility = View.GONE
                                holder.radiogrp?.visibility = View.GONE
                                holder.ansList.visibility = View.VISIBLE
                                holder.chckBoxGrp.visibility = View.GONE
                                holder.addressLayout.visibility = View.GONE

                                setSpinnerList(holder, it.optionValue,"")
                            }
                            else -> {
                                holder.ansText?.visibility = View.GONE
                                holder.radiogrp?.visibility = View.GONE
                                holder.ansList.visibility = View.GONE
                                holder.chckBoxGrp.visibility = View.GONE
                                holder.addressLayout.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }

        //Todo : set saved answers
        if(qcObj!=null){
            var data = qcObj?.qcResponseString?.let { Utility.getArtisanQcResponse(it) }
            if(data?.isSend == ActionForm.SAVE.getId() && data?.stageId == maxQCStageID){//todo set data if save is 1 done here
                var itr1 = data?.artisanQcResponses?.iterator()
                if(itr1!=null){
                    while (itr1.hasNext()){
                        var itr2 = itr1.next().iterator()
                        if(itr2!=null) {
                            while (itr2.hasNext()) {
                                var qcData = itr2.next()
                                if(qcData?.stageId == maxQCStageID){
                                    if(qcData?.questionId == quesNo){
                                        var savedAns = qcData?.answer

                                        ansOptList.forEach {
                                            if(it.first == qcData?.questionId){
                                                when (it.second) {
                                                    "0" -> {
                                                        holder.ansText.setText(savedAns, TextView.BufferType.EDITABLE)
                                                        qcAdapterListener?.addQuesAns(QuestionAnswer(qcData?.questionId, savedAns))
                                                    }
                                                    "1" -> {
                                                        setCheckBox(holder,it.third,savedAns)
                                                    }
                                                    "2" -> {
                                                        setRadioButtons(holder,it.third,savedAns)
                                                    }
                                                    "3" -> {
                                                        setSpinnerList(holder,it.third,savedAns)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //Edit Text Listener
        holder.ansText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                Log.e("QCFA", "EditText : Position == $position , Text == $s")
                var quesNo = position.plus(1)?.toLong()
                qcAdapterListener?.addQuesAns(QuestionAnswer(quesNo, s.toString()))
                Log.e("QCFA", "EditText Pair : QuestionAnswer(quesNo,s.toString()")
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        //Edit Text Listener for Address of Buyer
        holder.addrText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                Log.e("QCFA", "EditText : Position == $position , Text == $s")
                var quesNo = position.plus(1)?.toLong()
                qcAdapterListener?.addQuesAns(QuestionAnswer(quesNo, s.toString()))
                Log.e("QCFA", "EditText Pair : QuestionAnswer(quesNo,s.toString()")
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })


    }

    fun setCheckBox(holder: MyViewHolder, optionValue: String?,compareValue : String) {
        if (optionValue != null) {
            if(compareValue!=null)holder.chckBoxGrp.removeAllViews()
            var ansList = ArrayList<String>()
            var listVal = optionValue.split(",").map { it -> it.trim() }
            listVal.forEach { it ->
                Log.e("Values", "$it : $compareValue")
                var cB = CheckBox(context)
                val params = RadioGroup.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 0, 20, 20)
                cB.layoutParams = params
                cB.setPadding(5, 5, 20, 5)
                cB.text = it
                cB.setBackgroundResource(R.drawable.rect_outline_grey)
                if(compareValue.contains(it)){
                    cB.isChecked=true
                    ansList.add(cB.text.toString())
                }
                //CheckBox Group Listener
                cB.setOnCheckedChangeListener { buttonView, isChecked ->
                    var quesNo = holder?.adapterPosition.plus(1)?.toLong()
                    if(isChecked){
                        ansList.add(cB.text.toString())
                        Log.e("Values","CheckBtn group: Position == ${holder.adapterPosition} , selectedAns == ${ansList.toString()}")
                        qcAdapterListener?.addQuesAns(QuestionAnswer(quesNo, ansList.toString()))
                    }else{
                        ansList.remove(cB.text.toString())
                        Log.e("Values","CheckBtn group: Position == ${holder.adapterPosition} , selectedAns == ${ansList.toString()}")
                        qcAdapterListener?.addQuesAns(QuestionAnswer(quesNo, ansList.toString()))
                    }
                }

                holder.chckBoxGrp.addView(cB)
            }
        }
    }

    fun setRadioButtons(holder: MyViewHolder, optionValue: String?,compareValue : String) {
        if (optionValue != null) {
            if(compareValue!=null)holder.radiogrp.removeAllViews()
            var listVal = optionValue.split(";").map { it -> it.trim() }
            listVal.forEach { it ->
                Log.i("Values", "$compareValue : $it")
                var rB = RadioButton(context)
                val params = RadioGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 0, 20, 20)
                rB.layoutParams = params
                rB.setPadding(5, 5, 20, 5)
                rB.text = it
                if(compareValue.equals(it,true)) {
                    rB.isChecked = true
                    var quesNo = holder.adapterPosition.plus(1).toLong()
                    qcAdapterListener?.addQuesAns(QuestionAnswer(quesNo, it))

                }
                rB.setBackgroundResource(R.drawable.rect_outline_grey)
                //Radio Group Listener
                holder.radiogrp.setOnCheckedChangeListener { group, checkedId ->
                    try {
                        val rb = (group.findViewById(checkedId) as RadioButton).text
                        group.forEach {
                            if(it.id!=checkedId) {
                                val rd = it as RadioButton
                                rd.isChecked = false
                            }
                        }
                        Log.e("QCFA", "RadioGroup : Position == ${holder.adapterPosition} , Text == $rb")
                        var quesNo = holder.adapterPosition.plus(1).toLong()
                        qcAdapterListener?.addQuesAns(QuestionAnswer(quesNo, rb.toString()))
                    } catch (e: Exception) {
                    }
                }
                holder.radiogrp.addView(rB)
            }
        }
    }

    fun setSpinnerList(holder: MyViewHolder, optionValue: String?, compareValue : String) {
        if (optionValue != null) {
            var listVal = optionValue.split(";").toTypedArray()
            var adapter = context?.let { ArrayAdapter(
                it,
                android.R.layout.simple_spinner_item,
                listVal
            ) }
            var quesNo = holder.adapterPosition.plus(1).toLong()

            adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            holder?.ansList?.adapter = adapter
            holder?.ansList?.setBackgroundResource(R.drawable.rect_outline_grey)
            if (compareValue != "") {
                val spinnerPosition = adapter!!.getPosition(compareValue)
                holder?.ansList?.setSelection(spinnerPosition)
                qcAdapterListener?.addQuesAns(QuestionAnswer(quesNo, compareValue))
            }

            //Spinner List Listener
            holder.ansList.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //do nothing
                }
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                var selectItem = parent?.getItemAtPosition(position).toString()
                Log.e(
                    "QCFA",
                    "SpinnerList : Position == ${holder.adapterPosition} , Text == $selectItem"
                )
                    qcAdapterListener?.addQuesAns(QuestionAnswer(quesNo, selectItem.toString()))
                }
            })
        }
    }
}