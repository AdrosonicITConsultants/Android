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
import com.adrosonic.craftexchange.ui.modules.buyer.profile.BuyerProfileActivity.Companion.regAddr
import com.adrosonic.craftexchange.ui.modules.buyer.profile.editProfile.BuyerEditProfileActivity.Companion.craftUser
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength

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
        mBinding?.regAddr?.text = regAddr?.line1 ?: " - "
        mBinding?.country?.text = regAddr?.country ?: " - "
        mBinding?.email?.text = BuyerProfileActivity.craftUser?.email ?: " - "
        var mobile = "${craftUser?.mobile}  (primary)"
        mBinding?.mobile?.text = mobile

        mBinding?.designation?.setText(craftUser?.designation ?: " ")
        mBinding?.altMobile?.setText(craftUser?.alternateMobile ?: " ")

        Prefs.putString(ConstantsDirectory.DESIGNATION,craftUser?.designation ?: " ")
        Prefs.putString(ConstantsDirectory.ALT_MOBILE,craftUser?.alternateMobile ?: " ")

        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.designation?.addTextChangedListener {
            Prefs.putString(ConstantsDirectory.DESIGNATION, mBinding?.designation?.text.toString())
        }

        mBinding?.altMobile?.addTextChangedListener {
            if(mBinding?.altMobile?.text?.isNotEmpty()!!) {
                if(mBinding?.altMobile?.minLength(10) == false){
                    mBinding?.altMobile?.error = activity?.getString(R.string.mobile_no_invalid_text)
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,false)
                }else{
                    Prefs.putString(ConstantsDirectory.ALT_MOBILE, mBinding?.altMobile?.text.toString())
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)
                }
            }else{
                Prefs.putString(ConstantsDirectory.ALT_MOBILE, mBinding?.altMobile?.text.toString())
                Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)
            }
        }
    }

    companion object {
        fun newInstance() = GeneralEditFragment()
        const val TAG = "GenEditFrag"
    }
}
