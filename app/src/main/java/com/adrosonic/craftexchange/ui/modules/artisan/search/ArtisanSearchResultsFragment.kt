package com.adrosonic.craftexchange.ui.modules.artisan.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentArtisanSearchResultsBinding
import com.adrosonic.craftexchange.ui.modules.artisan.products.UploadedProductsASearchAdapter
import com.adrosonic.craftexchange.ui.modules.artisan.search.adapter.ArtisanSearchAdapter
import com.adrosonic.craftexchange.ui.modules.search.FilterCollectionAdapter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.ArtisanProductsViewModel
import com.adrosonic.craftexchange.viewModels.SearchViewModel


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ArtisanSearchResultsFragment : Fragment(),
    ArtisanProductsViewModel.productsFetchInterface,
    FilterCollectionAdapter.FilterSelectionListener{

    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentArtisanSearchResultsBinding?= null
    val mViewModel: SearchViewModel by viewModels()
    var adapter : UploadedProductsASearchAdapter?= null

    private lateinit var filterAdapter : FilterCollectionAdapter
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artisan_search_results, container, false)
        searchFilter = param1
        mBinding?.searchArtisanSwipe?.isEnabled = false // disable swipte to refresh action

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
                    ?.replace(R.id.ss_container, ArtisanSuggestionFragment.newInstance())
                    ?.addToBackStack(null)
                    ?.commit()
                return false
            }
        })

        searchFilter?.let { setupRecyclerView(it,-1L) }

        val slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
        val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)

        mBinding?.filterByCollection?.setOnClickListener {
            if (mBinding?.collectionFilterWindow?.visibility == View.GONE) {
                mBinding?.collectionFilterWindow?.visibility = View.VISIBLE
                mBinding?.collapseBtn?.visibility = View.VISIBLE
                mBinding?.filterRecycler?.visibility = View.VISIBLE
                mBinding?.collectionFilterWindow?.animation = slideDown
            } else {
                mBinding?.collectionFilterWindow?.visibility = View.GONE
                mBinding?.collapseBtn?.visibility = View.GONE
                mBinding?.filterRecycler?.visibility = View.GONE
                mBinding?.collectionFilterWindow?.animation = slideUp
            }
        }

        mBinding?.collapseBtn?.setOnClickListener {
            mBinding?.collectionFilterWindow?.visibility = View.GONE
            mBinding?.collapseBtn?.visibility = View.GONE
            mBinding?.filterRecycler?.visibility = View.GONE
            mBinding?.collectionFilterWindow?.animation = slideUp
        }
    }

    private fun setFilterRecycler(mFilterList : ArrayList<Pair<String,Long>>){
        mBinding?.filterRecycler?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        filterAdapter = FilterCollectionAdapter(requireContext(),mFilterList)
        mBinding?.filterRecycler?.adapter = filterAdapter
    }

    private fun setupRecyclerView(searchFilter : String, filter : Long){
        adapter = UploadedProductsASearchAdapter(requireContext(),searchFilter.let { mViewModel.getArtisanSearchData(it, filter).value })
        mBinding?.artisanSearchList?.adapter = adapter
        mBinding?.artisanSearchList?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding?.listSizeText?.text = "Found ${adapter?.itemCount} items"
        if(adapter?.itemCount == 0){
            mBinding?.emptyText?.visibility = View.VISIBLE
        }else{
            mBinding?.emptyText?.visibility = View.GONE
        }
        adapter?.notifyDataSetChanged()
    }



    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
            ArtisanSearchResultsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
//        fun newInstance() = ArtisanSearchResultsFragment()
    }

    override fun onSuccess() {
        TODO("Not yet implemented")
    }

    override fun onFailure() {
        TODO("Not yet implemented")
    }

//    override fun onFilterSelected(pairList: ArrayList<Triple<String, Boolean, Long>>) {
    override fun onFilterSelected(pairList: Pair<String,Long>) {
        filterSelected = pairList

        when(filterSelected?.second){
            1L -> {
                //Show all
                searchFilter?.let { setupRecyclerView(it,-1L) }
            }
            2L -> {
                //Antaran
                searchFilter?.let { setupRecyclerView(it,1L) }
            }
            3L -> {
                //Artisan
                searchFilter?.let { setupRecyclerView(it,0L) }
            }
        }


    }

}