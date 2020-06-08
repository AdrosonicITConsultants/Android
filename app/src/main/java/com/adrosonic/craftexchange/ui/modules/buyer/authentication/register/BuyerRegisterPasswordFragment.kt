package com.adrosonic.craftexchange.ui.modules.buyer.authentication.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBuyerRegisterPasswordBinding
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty

private const val ARG_PARAM1 = "param1"

class BuyerRegisterPasswordFragment : Fragment() {

    companion object {
//        @JvmStatic
//        fun newInstance(param1: String) =
//            BuyerRegisterPasswordFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                }
//            }
        fun newInstance() = BuyerRegisterPasswordFragment()
        const val TAG = "BuyerRegisterpwd"
    }

    private var mBinding: FragmentBuyerRegisterPasswordBinding ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_register_password, container, false)
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.buttonNext?.setOnClickListener{
            if(
                mBinding?.textBoxPassword?.nonEmpty() == true &&
                mBinding?.textBoxRetypePwd?.nonEmpty() == true
            ){
                if(mBinding?.textBoxPassword?.text.toString() == mBinding?.textBoxRetypePwd?.text.toString()){

                    Prefs.putString(ConstantsDirectory.USER_PWD,mBinding?.textBoxRetypePwd?.text.toString())

                    if (savedInstanceState == null) {
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.register_container,
                                BuyerRegisterDetailsFragment.newInstance(),"Register Buyer Details")
                            ?.addToBackStack(null)
                            ?.commit()
                    }
                }else{
                    Toast.makeText(activity,"Enter Correct Password",Toast.LENGTH_SHORT).show()
                }
            }else{
                mBinding?.textBoxPassword?.nonEmpty{ mBinding?.textBoxPassword?.error = it }
                mBinding?.textBoxRetypePwd?.nonEmpty{ mBinding?.textBoxRetypePwd?.error = it }
            }
        }
    }

}
