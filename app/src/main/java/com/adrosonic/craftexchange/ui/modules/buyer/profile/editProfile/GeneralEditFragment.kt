package com.adrosonic.craftexchange.ui.modules.buyer.profile.editProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentGeneralEditBinding
import com.adrosonic.craftexchange.ui.modules.buyer.profile.BuyerProfileActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GeneralEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GeneralEditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentGeneralEditBinding?= null
    private var altmobile:String?=""


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
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_general_edit, container, false)
        mBinding?.regAddr?.text = BuyerProfileActivity.regAddr?.line1 ?: " - "
        mBinding?.country?.text = BuyerProfileActivity.regAddr?.country ?: " - "
//        if(craftUser?.email != "") {
        mBinding?.email?.text = BuyerProfileActivity.craftUser?.email ?: " - "
//        }
        var mobile = "${BuyerProfileActivity.craftUser?.mobile}  (primary)"
        if(BuyerProfileActivity.craftUser?.alternateMobile != ""){
            altmobile = "${BuyerProfileActivity.craftUser?.alternateMobile}"
        }

        mBinding?.mobile?.text = mobile
        mBinding?.altMobile?.setText(altmobile ?: "")
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding?.designation?.addTextChangedListener {
            Prefs.putString(ConstantsDirectory.DESIGNATION, mBinding?.designation?.text.toString())
        }
        mBinding?.altMobile?.addTextChangedListener {
            Prefs.putString(ConstantsDirectory.ALT_MOBILE, mBinding?.altMobile?.text.toString())
        }
    }

    companion object {
        fun newInstance() = GeneralEditFragment()
        const val TAG = "GenEditFrag"
    }
}
