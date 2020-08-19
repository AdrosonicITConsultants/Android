package com.adrosonic.craftexchange.ui.modules.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentSuggestionBinding
import com.adrosonic.craftexchange.repository.data.response.search.SuggData
import com.adrosonic.craftexchange.repository.data.response.search.SuggestionResponse
import com.adrosonic.craftexchange.ui.modules.artisan.search.ArtisanSearchResultsFragment
import com.adrosonic.craftexchange.ui.modules.buyer.auth.login.BuyerLoginPasswordFragment
import com.adrosonic.craftexchange.viewModels.SearchViewModel


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SuggestionFragment : Fragment(),
SearchViewModel.FetchSuggestions{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentSuggestionBinding?= null
    val mViewModel : SearchViewModel by viewModels()
    var adapter : SuggestionAdapter ?= null
    var sugList = arrayListOf<SuggData>()
//    var suggList = ArrayList<Pair<String,String>>()
//    var suggestion = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_suggestion, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel?.suggListener = this


        var search = activity?.findViewById<SearchView>(R.id.search_artisan)
        search?.requestFocus()

        search?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query:String):Boolean {
//                mViewModel?.getArtisanSearchSuggestions(query)
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.ss_container, ArtisanSearchResultsFragment.newInstance(query))
                    ?.addToBackStack(null)
                    ?.commit()

                return false
            }
            override fun onQueryTextChange(newText:String):Boolean {
                if(newText.length >= 3) {
                    mViewModel?.getArtisanSearchSuggestions(newText)
                    mBinding?.suggetionBg?.visibility = View.VISIBLE

                }else{
                    sugList?.clear()
                    adapter?.notifyDataSetChanged()
                    mBinding?.suggetionBg?.visibility = View.GONE
                }
                return false
            }
        })


    }

    private fun setSuggestionListRecycler(
        mContext: Context,
        mSuggList: ArrayList<SuggData>?
    ){

        if(mSuggList == null){
            mBinding?.emptySuggestionText?.visibility = View.VISIBLE
            mBinding?.suggetionBg?.visibility = View.GONE
        }else{
            mBinding?.emptySuggestionText?.visibility = View.GONE
            mBinding?.suggetionBg?.visibility = View.VISIBLE

            adapter = SuggestionAdapter(mContext, mSuggList)
            mBinding?.suggestionList?.adapter = adapter
            mBinding?.suggestionList?.layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false)
        }

    }


    companion object {

        @JvmStatic
        fun newInstance() = SuggestionFragment()
    }

    override fun onSuccessSugg(sug: SuggestionResponse) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                sugList?.clear()
                sug.data?.forEach {
                    sugList?.add(it)
                }
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
            }
            )
        } catch (e: Exception) {
            Log.e("Suggestions", "Exception onFailure " + e.message)
        }
    }
}