package com.adrosonic.craftexchange.ui.modules.buyer.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBuyerSuggestionBinding
import com.adrosonic.craftexchange.repository.data.response.search.SuggData
import com.adrosonic.craftexchange.repository.data.response.search.SuggestionResponse
import com.adrosonic.craftexchange.ui.modules.search.SuggestionAdapter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.SearchViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



class BuyerSuggestionFragment : Fragment(),
    SearchViewModel.FetchBuyerSuggestions {
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentBuyerSuggestionBinding?= null
    val mViewModel : SearchViewModel by viewModels()
    var adapter : SuggestionAdapter?= null
    var sugList = arrayListOf<SuggData>()

    var madeWithAntharan : Long ?= 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_suggestion, container, false)
        arguments?.let {
            madeWithAntharan = it.getLong(ARG_PARAM1)
        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.buySugListener = this

        var search = activity?.findViewById<SearchView>(R.id.search_artisan)
        search?.requestFocus()

        search?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query:String):Boolean {
                if(Utility.checkIfInternetConnected(requireContext())){
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.ss_container, BuyerSearchResultFragment.newInstance(query,5L)) //for GLOBAL(5) Search
                        ?.addToBackStack(null)
                        ?.commit()
                }else{
                    Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
                }
                return false
            }
            override fun onQueryTextChange(newText:String):Boolean {
            if(Utility.checkIfInternetConnected(requireContext())){
                if(newText.length >= 3) {
                    mViewModel.getBuyerSearchSuggestions(newText)
                    mBinding?.suggetionBg?.visibility = View.VISIBLE
                }else{
                    adapter?.notifyDataSetChanged()
                    mBinding?.suggetionBg?.visibility = View.GONE
                }
            }else{
                Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
            }
                return false
            }
        })

    }

    private fun setSuggestionListRecycler(mContext: Context, mSuggList: ArrayList<SuggData>?){
        if(mSuggList == null){
            mBinding?.emptySuggestionText?.visibility = View.VISIBLE
            mBinding?.suggetionBg?.visibility = View.GONE
        }else{
            mBinding?.emptySuggestionText?.visibility = View.GONE
            mBinding?.suggetionBg?.visibility = View.VISIBLE

            adapter = SuggestionAdapter(mContext, mSuggList)
            mBinding?.suggestionList?.adapter = adapter
            mBinding?.suggestionList?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Long) = BuyerSuggestionFragment().apply {
            arguments = Bundle().apply {
                putLong(ARG_PARAM1, param1)
            }
        }
    }

    override fun onSuccessSugg(sug: SuggestionResponse) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                sugList.clear()
                sug.data.forEach { sugList.add(it) }
                if(sugList.isEmpty()){
                    setSuggestionListRecycler(requireContext(), null)
                    adapter?.notifyDataSetChanged()
                }else{
                    setSuggestionListRecycler(requireContext(),sugList)
                    adapter?.notifyDataSetChanged()
                }
            }
            )
        } catch (e: Exception) {
            Log.e("Suggestions", "Exception onSuccess " + e.message)
        }
    }

    override fun onFailureSugg() {

        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Suggestions", "OnFailure")
            })
        } catch (e: Exception) {
            Log.e("Suggestions", "Exception onFailure " + e.message)
        }
    }
}