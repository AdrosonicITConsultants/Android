package com.adrosonic.craftexchange.ui.modules.buyer.viewProducts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.FragmentCategoryProductsBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.AllProductsResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.Product
import com.adrosonic.craftexchange.ui.modules.viewProducts.adapter.CategoryAdapter
import com.adrosonic.craftexchange.utils.Utility
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CategoryProductsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentCategoryProductsBinding ?= null
    private var mProduct = mutableListOf<Product>()
    private var categoryAdapter: CategoryAdapter?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_products, container, false)
       refreshProductCategory()
        initializeView()
//        mProduct.add(Product(1,"Saree",emptyList()))
//        mProduct.add(Product(2,"Fabrics",emptyList()))
//        mProduct.add(Product(3,"HA",emptyList()))
//        mProduct.add(Product(4,"Stole",emptyList()))
//        mProduct.add(Product(5,"Dupatta",emptyList()))
//        mProduct.add(Product(6,"FA",emptyList()))

        categoryAdapter = CategoryAdapter(requireContext(), mProduct)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView(){

        categoryAdapter?.setProducts(mProduct)
        mBinding?.catProdRecyclerList?.adapter = categoryAdapter
        mBinding?.catProdRecyclerList?.layoutManager = GridLayoutManager(requireContext(),2,RecyclerView.VERTICAL,false)
        categoryAdapter?.notifyDataSetChanged()
    }

    private fun refreshProductCategory(){
        if(Utility.checkIfInternetConnected(requireContext())) {
            CraftExchangeRepository
                .getProductService()
                .getAllProducts().enqueue(object : Callback, retrofit2.Callback<AllProductsResponse> {
                    override fun onFailure(call: Call<AllProductsResponse>, t: Throwable) {
                        t.printStackTrace()
                    }
                    override fun onResponse(
                        call: Call<AllProductsResponse>, response: Response<AllProductsResponse>) {
                        if (response.body()?.valid == true) {
                            ProductPredicates.insertAllCategoryProducts(response.body())
                        } else {
                            Toast.makeText(activity, "${response.body()?.errorMessage}", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }

    }

    private fun initializeView(){
        var prodList = ProductPredicates.getAllCategoryProducts()
        mProduct.clear()
        if (prodList != null) {
            for (prodsize in prodList){
                Log.i("Stat","$prodsize")
                var id = prodsize?.productid
                var productType = prodsize?.product
                var product =
                    Product(
                        id!!,
                        productType!!,
                        "",
                        emptyList()
                    )
                mProduct.add(product)
            }
        }
    }


    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = CategoryProductsFragment()
        const val TAG = "CategoryProducts"
    }
}
