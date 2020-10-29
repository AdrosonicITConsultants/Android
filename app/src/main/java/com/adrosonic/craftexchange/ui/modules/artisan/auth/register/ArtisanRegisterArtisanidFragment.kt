package com.adrosonic.craftexchange.ui.modules.artisan.auth.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentArtisanRegisterArtisanidBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.model.artisan.ArtisanidModel
import com.adrosonic.craftexchange.repository.data.registerResponse.RegisterResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback


class ArtisanRegisterArtisanidFragment : Fragment() {

    companion object {
        fun newInstance() = ArtisanRegisterArtisanidFragment()
        const val TAG = "ArtisanRegArtId"
    }

    private var mBinding: FragmentArtisanRegisterArtisanidBinding ?= null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artisan_register_artisanid, container, false)
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.buttonNext?.setOnClickListener{
            if(mBinding?.textBoxArtisanId?.nonEmpty() == true) {
//                showProgress()
                CraftExchangeRepository
                    .getRegisterService()
                    .verifyArtisanDetails("application/json",
                        ArtisanidModel(
                            "",
                            mBinding?.textBoxArtisanId?.text.toString()
                        )
                    )
                    .enqueue(object : Callback, retrofit2.Callback<RegisterResponse> {
                        override fun onResponse(
                            call: Call<RegisterResponse>,
                            response: Response<RegisterResponse>
                        ) {
                            if(response.body()?.valid == true){
                                Log.e(TAG, response.toString())
                                Prefs.putString(ConstantsDirectory.ARTISAN_ID,mBinding?.textBoxArtisanId?.text.toString())

                                if (savedInstanceState == null) {
                                    activity?.supportFragmentManager?.beginTransaction()
                                        ?.replace(R.id.register_container,
                                            ArtisanRegisterUsernameFragment.newInstance(),"Register Artisan Username")
                                        ?.addToBackStack(null)
                                        ?.commit()
                                }
                            } else{
                                Toast.makeText(requireActivity(),"${response.body()?.errorMessage}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            t.printStackTrace()
                            Toast.makeText(requireActivity(),"${t.printStackTrace()}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }else{ mBinding?.textBoxArtisanId?.nonEmpty{ mBinding?.textBoxArtisanId?.error = it } }
        }
    }
}
