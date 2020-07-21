package com.adrosonic.craftexchange.ui.modules.viewProducts.productlists

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBrandProdListBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.viewProducts.brand.BrandProductDetailResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BrandProdListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentBrandProdListBinding ?= null
    var artisanId : Int ?= 0


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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_brand_prod_list, container, false)
        artisanId = this.requireArguments().getString(ConstantsDirectory.ARTISAN_ID)?.toInt()
        getProducts()
        return mBinding?.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = BrandProdListFragment()
        const val TAG = "BrandProducts"
    }

    private fun getProducts(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getProductService()
            .getProductByArtisan(token,artisanId!!)
            .enqueue(object : Callback, retrofit2.Callback<BrandProductDetailResponse> {
                override fun onFailure(call: Call<BrandProductDetailResponse>, t: Throwable) {
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<BrandProductDetailResponse>, response: Response<BrandProductDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.e(TAG,"${response.body()}")
                    } else {
                        Toast.makeText(activity, "${response.body()}", Toast.LENGTH_SHORT).show()
                    }
                }
            })

    }
}
