package com.adrosonic.craftexchange.ui.modules.buyer.viewProducts

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.CategoryProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.ClusterList
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.FragmentCategoryProductsBinding
import com.adrosonic.craftexchange.databinding.FragmentRegionProductsBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.AllProductsResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.Product
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter.CategoryAdapter
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter.RegionAdapter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.CMSViewModel
import com.adrosonic.craftexchange.viewModels.CategoryViewModel
import com.adrosonic.craftexchange.viewModels.ClusterViewModel
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CategoryProductsFragment : Fragment(),
    CategoryViewModel.CategoryListInterface {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentCategoryProductsBinding ?= null
    private var categoryAdapter: CategoryAdapter?= null
    private var mCategoryList : RealmResults<CategoryProducts>?= null
    val mViewModel: CategoryViewModel by viewModels()
    val mCMSViewModel: CMSViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_products, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.catListener = this

        setRecyclerList()

        mBinding?.swipeCategory?.isEnabled = false

        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            if(UserConfig.shared.isAntranCoDesign)   mCMSViewModel.categoriescodesign()
            else mCMSViewModel.categoriesselfdesign()
        //  mCMSViewModel.getCategoriesData()
            mViewModel.getAllCategories()
        }

        mViewModel.getCategoryListMutableData()
            .observe(viewLifecycleOwner, Observer<RealmResults<CategoryProducts>>{
                mCategoryList = it
                categoryAdapter?.updateCategoryList(mCategoryList)
            })

        mBinding?.swipeCategory?.isRefreshing = true
    }

    private fun setRecyclerList(){
        mBinding?.catProdRecyclerList?.layoutManager = GridLayoutManager(requireContext(),2,RecyclerView.VERTICAL,false)
        categoryAdapter = CategoryAdapter(requireContext(),
            mViewModel.getCategoryListMutableData().value
        )
        mBinding?.catProdRecyclerList?.adapter = categoryAdapter
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = CategoryProductsFragment()
        const val TAG = "CategoryProducts"
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("categoryList", "OnFailure")
                mBinding?.swipeCategory?.isRefreshing = false
                mViewModel.getCategoryListMutableData()
                Utility.displayMessage(
                    getString(R.string.err_fetch_list),
                    requireContext()
                )
            }
            )
        } catch (e: Exception) {
            Log.e("categoryList", "Exception onFailure " + e.message)
        }
    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("categoryList", "OnFailure")
                mBinding?.swipeCategory?.isRefreshing = false
                mViewModel.getCategoryListMutableData()
            }
            )
        } catch (e: Exception) {
            Log.e("categoryList", "Exception onFailure " + e.message)
        }
    }
}
