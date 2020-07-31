package com.adrosonic.craftexchange.ui.modules.buyer.viewProducts

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.FragmentArtisanProductsBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.BrandDetails
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.BrandListResponse
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter.BrandAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ArtisanProductsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentArtisanProductsBinding ?= null
    private var mProduct = mutableListOf<BrandDetails>()
    private var brandAdapter: BrandAdapter?= null
    private var mSpinner = mutableListOf<String>()
    private var mClusterList = HashMap<String?,Long?>()
    private var filterBy : String ? = ""



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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artisan_products, container, false)
        refreshBrandDetails()
        initializeView()
        brandAdapter = BrandAdapter(requireContext(), mProduct)

        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        brandAdapter?.setProducts(mProduct)
        mBinding?.artProdRecyclerList?.adapter = brandAdapter
        mBinding?.artProdRecyclerList?.layoutManager = GridLayoutManager(requireContext(),2,
            RecyclerView.VERTICAL,false)
//        mBinding?.catProdRecyclerList?.layoutManager = AutoFitGridLayoutManager(requireContext(),500)
        brandAdapter?.notifyDataSetChanged()
    }

    private fun refreshBrandDetails(){
        if(Utility.checkIfInternetConnected(requireContext())) {

            var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
            CraftExchangeRepository
                .getProductService()
                .getFilteredArtisans(token).enqueue(object : Callback, retrofit2.Callback<BrandListResponse> {
                    override fun onFailure(call: Call<BrandListResponse>, t: Throwable) {
                        t.printStackTrace()
                    }
                    override fun onResponse(
                        call: Call<BrandListResponse>,
                        response: Response<BrandListResponse>
                    ) {
                        if (response.body()?.valid == true) {
                            ProductPredicates.insertBrands(response.body())
                        } else {
                            Toast.makeText(activity, "${response.body()?.errorMessage}", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }
    }

    private fun initializeView(){

        var clusterList = ProductPredicates.getAllClusters()
        mSpinner.clear()
        mSpinner.add("View by Cluster")
        if (clusterList != null) {
            for (size in clusterList){
                Log.i("Stat","$size")
                var cluster = size?.cluster
                var clusterId = size?.clusterid
                mSpinner.add(cluster!!)
                mClusterList.put(cluster,clusterId)
            }
        }
       filterSpinner(requireContext(),mSpinner,mBinding?.filterCluster)
    }
    fun filterSpinner(context : Context, array : List<String>, spinner : Spinner?) {
        var adapter= ArrayAdapter(context, android.R.layout.simple_spinner_item, array)
        var filterBy : String
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner?.adapter = adapter
        spinner?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                initialList()
                brandAdapter?.setProducts(mProduct)
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position > 0){
                    filterBy = parent?.getItemAtPosition(position).toString()
                    Log.e("spin","fil : $filterBy")
                    var productList = ProductPredicates.getFilteredBrands(mClusterList[filterBy])
                    var size = productList?.size
                    mProduct.clear()
                    if (productList != null) {
                        for (size in productList){
                            Log.i("Stat","$size")
                            Log.i("Stat","$size")
                            var artisanId = size?.artisanId
                            var clusterId = size?.clusterId
                            var firstname = size?.firstName
                            var  compname = size?.companyName
                            var profilepic = size?.profilePic
                            var  brandlogo = size?.logo
                            var brand = BrandDetails(profilepic,compname,firstname,brandlogo,clusterId,"",artisanId)
                            mProduct.add(brand)
                        }
                    }
                    brandAdapter?.setProducts(mProduct)
                }else{
                    initialList()
                }
            }
        })
    }

    fun initialList(){
        var brandList = ProductPredicates.getAllBrandDetails()
        mProduct.clear()
        if (brandList != null) {
            for (brandsize in brandList){
                Log.i("Stat","$brandsize")
                var artisanId = brandsize?.artisanId
                var clusterId = brandsize?.clusterId
                var firstname = brandsize?.firstName
                var  compname = brandsize?.companyName
                var profilepic = brandsize?.profilePic
                var  brandlogo = brandsize?.logo
                var brand = BrandDetails(profilepic,compname,firstname,brandlogo,clusterId,"",artisanId)
                mProduct.add(brand)
            }
            brandAdapter?.setProducts(mProduct)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ArtisanProductsFragment()
    }
}
