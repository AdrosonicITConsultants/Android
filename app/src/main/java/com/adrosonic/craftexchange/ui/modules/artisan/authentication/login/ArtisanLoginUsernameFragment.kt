package com.adrosonic.craftexchange.ui.modules.artisan.authentication.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentArtisanLoginUsernameBinding
import com.adrosonic.craftexchange.ui.modules.authentication.register.RegisterActivity

private const val ARG_PARAM1 = "param1"

class ArtisanLoginUsernameFragment : Fragment() {
    private var param1: String? = null

//    companion object {
//        @JvmStatic
//        fun newInstance(param1: String) =
//            ArtisanLoginUsernameFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                }
//            }
//    }

    companion object {
        fun newInstance() = ArtisanLoginUsernameFragment()
    }

    private var mBinding: FragmentArtisanLoginUsernameBinding ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artisan_login_username, container, false)
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val clickSpan = SpannableString("New User? Click here to register.")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                startActivity(Intent(activity, RegisterActivity::class.java))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        clickSpan.setSpan(clickableSpan, 10, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        mBinding?.textViewClickHere?.text = clickSpan
        mBinding?.textViewClickHere?.movementMethod = LinkMovementMethod.getInstance()
        mBinding?.textViewClickHere?.highlightColor = Color.TRANSPARENT

        mBinding?.buttonNext?.setOnClickListener{
            if (savedInstanceState == null) {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.login_container,
                        ArtisanLoginPasswordFragment.newInstance(),"Login Artisan Password")
                    ?.commit()
            }
        }
    }

}
