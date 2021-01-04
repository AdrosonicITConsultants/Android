package com.adrosonic.craftexchange.ui.modules.buyer.landing

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBuyerHomeBinding
import com.adrosonic.craftexchange.ui.modules.artisan.productTemplate.addProductIntent
import com.adrosonic.craftexchange.ui.modules.buyer.ownDesign.ownDesignIntent
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.ViewAntaranProductsFragment
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.ViewArtisanProductsFragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.CMSViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.pixplicity.easyprefs.library.Prefs
import org.json.JSONArray
import org.json.JSONObject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BuyerHomeFragment : Fragment(),
CMSViewModel.CMSDataInterface{

    companion object {
        fun newInstance() = BuyerHomeFragment()
        const val TAG = "BuyerHomeFrag"
    }

    private var mBinding: FragmentBuyerHomeBinding? = null
    val mCMSVM : CMSViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_home, container, false)
        var firstname = Prefs.getString(ConstantsDirectory.FIRST_NAME, "User")
        mBinding?.textUser?.text = firstname
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCMSVM.cmsListener = this
        if(Utility.checkIfInternetConnected(requireContext())){
            mCMSVM?.getPagesData(27)
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),requireContext())
            mBinding?.antaranImage?.setImageResource(R.drawable.antaran_image)
            mBinding?.artisanImage?.setImageResource(R.drawable.artisan_catalogue_image)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.artisanCatalogue?.setOnClickListener {
            if (savedInstanceState == null) {
                Prefs.putLong(ConstantsDirectory.IS_MADE_WITH_ANTHARAN,0)
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(
                        R.id.buyer_home_container,
                        ViewArtisanProductsFragment.newInstance()
                    )
                    ?.addToBackStack(null)
                    ?.commit()
            }
        }

        mBinding?.antaranCatalogue?.setOnClickListener {
            if (savedInstanceState == null) {
                Prefs.putLong(ConstantsDirectory.IS_MADE_WITH_ANTHARAN,1)
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(
                        R.id.buyer_home_container,
                        ViewAntaranProductsFragment.newInstance()
                    )
                    ?.addToBackStack(null)
                    ?.commit()
            }
        }
        mBinding?.buttonCustomDesign?.setOnClickListener {
            startActivity(context?.ownDesignIntent())
        }

    }

    override fun onCMSFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("CMS", "OnFailure")
            }
            )
        } catch (e: Exception) {
            Log.e("CMS", "Exception onFailure " + e.message)
        }
    }

    override fun onCMSSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("CMS", "onSuccess")
                if(UserConfig.shared.pageCMS != null){
//                    val json=JSONObject(UserConfig.shared.pageCMS)
//                    val dataJson = JSONArray(UserConfig.shared.pageCMS)
//                    Log.i("CMS", "DataJson : $dataJson")
//                    for (i in 0 until dataJson.length()) {
                        val dataObj = JSONObject(UserConfig.shared.pageCMS)
                        Log.i("CMS", "DataObj : $dataObj")
                        var pageId = dataObj?.getString("id")?.toLong()
                        if (pageId == 64L) {
                            var acfObj = dataObj?.getJSONObject("acf")
                            mBinding?.browseText?.text = ""//acfObj?.getString("card_para2")

                            var url1 = acfObj?.getString("card_background_1")
                            var url2 = acfObj?.getString("card_background_2")

                            Glide.with(this).load(url2).signature(ObjectKey((System.currentTimeMillis()).div(7*24*60*60*1000).toString())).into(object: SimpleTarget<Drawable>() {
                                override fun onResourceReady(
                                    resource: Drawable,
                                    transition: Transition<in Drawable>?
                                ) {
                                    mBinding?.cmsBackImg1?.background = resource
                                }
                            })

                            Glide.with(this).load(url1).signature(ObjectKey((System.currentTimeMillis()).div(7*24*60*60*1000).toString())).into(object: SimpleTarget<Drawable>() {
                                override fun onResourceReady(
                                    resource: Drawable,
                                    transition: Transition<in Drawable>?
                                ) {
                                    mBinding?.cmsImgLayout?.background = resource
                                }
                            })

                            mBinding?.titleText?.text =""// acfObj?.getString("card_para")

                            var desc = acfObj?.getString("card_para2")
                            @RequiresApi(Build.VERSION_CODES.N)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                mBinding?.descriptionText?.text = Html.fromHtml(desc, Html.FROM_HTML_MODE_COMPACT)
                            } else {
                                mBinding?.descriptionText?.text = Html.fromHtml(desc)
                            }

                            var artUrl = acfObj?.getString("artisan_background_extended")
                            var antUrl = acfObj?.getString("antaran_background_extended")

                            mBinding?.artisanImage?.let {
                                artUrl?.let { it1 ->
                                    ImageSetter?.setCMSImage(requireContext(), it1,
                                        it
                                    )
                                }
                            }

                            mBinding?.antaranImage?.let {
                                antUrl?.let { it1 ->
                                    ImageSetter?.setCMSImage(requireContext(), it1,
                                        it
                                    )
                                }
                            }

                        }
                    }
//                }
            }
            )
        } catch (e: Exception) {
            Log.e("CMS", "Exception onFailure " + e.message)
        }
    }


}
