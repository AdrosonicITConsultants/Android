package com.adrosonic.craftexchange.ui.modules.buyer.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentArtisanSearchResultsBinding
import com.adrosonic.craftexchange.databinding.FragmentBuyerSearchResultBinding
import com.adrosonic.craftexchange.ui.modules.artisan.search.ArtisanSuggestionFragment
import com.adrosonic.craftexchange.ui.modules.artisan.search.adapter.ArtisanSearchAdapter
import com.adrosonic.craftexchange.ui.modules.search.FilterCollectionAdapter
import com.adrosonic.craftexchange.viewModels.SearchViewModel


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class BuyerSearchResultFragment : Fragment(),
    FilterCollectionAdapter.FilterSelectionListener {

    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentBuyerSearchResultBinding?= null

    private lateinit var filterAdapter : FilterCollectionAdapter
    val mViewModel: SearchViewModel by viewModels()
    var adapter : ArtisanSearchAdapter?= null
    var filterList = ArrayList<Pair<String,Long>>()
    var filterSelected : Pair<String,Long> ?= null
    private var searchFilter : String ?= ""

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_search_result, container, false)
        searchFilter = param1

        filterList.clear()
        filterList.add(Pair(requireActivity().getString(R.string.show_both),1))
        filterList.add(Pair(requireActivity().getString(R.string.antaran_co_design_collection),2))
        filterList.add(Pair(requireActivity().getString(R.string.artisan_self_design_collection),3))

        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFilterRecycler(filterList)
        filterAdapter.fListener = this

        var search = activity?.findViewById<SearchView>(R.id.search_artisan)
        search?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query:String):Boolean {
                return false
            }
            override fun onQueryTextChange(newText:String):Boolean {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.ss_container, BuyerSuggestionFragment.newInstance())
                    ?.addToBackStack(null)
                    ?.commit()
                return false
            }
        })
    }

    private fun setFilterRecycler(mFilterList : ArrayList<Pair<String,Long>>){
        mBinding?.filterRecycler?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        filterAdapter = FilterCollectionAdapter(requireContext(),mFilterList)
        mBinding?.filterRecycler?.adapter = filterAdapter
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
            BuyerSearchResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onFilterSelected(pairList: Pair<String, Long>) {
        TODO("Not yet implemented")
    }
}