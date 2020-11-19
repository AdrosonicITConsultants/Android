package com.adrosonic.craftexchangemarketing.ui.modules.teamManagement

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.FragmentTeamListBinding
import com.adrosonic.craftexchangemarketing.repository.data.request.team.AdminsRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.team.AdminRoleData
import com.adrosonic.craftexchangemarketing.repository.data.response.team.AdminsData
import com.adrosonic.craftexchangemarketing.ui.modules.teamManagement.adapter.TeamRecyclerAdapter
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.TeamViewModel
import com.pixplicity.easyprefs.library.Prefs


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TeamListFragment : Fragment(),
TeamViewModel.AdminDetailsInterface{
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentTeamListBinding ?= null

    var mAdapter : TeamRecyclerAdapter ?= null

    var refRoleId : Int ?= -1  // -1 for All roles
    var pageNo : Int ?= 1
    var searchStr : String ?= ""
    private var mSpinner = mutableListOf<String>()
    var teamList = ArrayList<AdminsData>()


    var adReq = AdminsRequest()
    val mTeamVM : TeamViewModel by viewModels()

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_team_list, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTeamVM?.adminListener = this

        setRecyclerAdapter()
        mBinding?.swipeTeamList?.isEnabled = false

        //set spinner list
        var adRole = Utility.getAdminRoleDetails()
        mSpinner?.clear()
        mSpinner.add("Select Role Category")
        mSpinner.add("All")
        adRole?.forEach {
            mSpinner?.add(it.desc)
        }
        setRoleSpinner(mSpinner)

//        if(Utility.checkIfInternetConnected(requireContext())){
//            mBinding?.swipeTeamList?.isRefreshing = true
//            adReq.pageNo = pageNo!!
//            adReq.refRoleId = refRoleId!!
//            adReq.searchStr = searchStr.toString()
//            mTeamVM?.getTeamList(adReq)
//        }else{
//            Utility.displayMessage(getString(R.string.no_internet_connection),requireContext())
//        }

        mBinding?.teamRecyclerList?.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState:Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    if(Utility.checkIfInternetConnected(requireContext())){
                        mBinding?.swipeTeamList?.isRefreshing = true
                        adReq.pageNo = pageNo!!
                        adReq.refRoleId = refRoleId!!
                        if(refRoleId == -1){
                            adReq.searchStr = null
                        }else{
                            adReq.searchStr = searchStr.toString()
                        }
                        mTeamVM?.getTeamList(adReq)
                    }else{
                        Utility.displayMessage(getString(R.string.no_internet_connection),requireContext())
                    }
                }
            }
        })

        mBinding?.searchTeammate?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(expr: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                var searchExpression = p0?.toString() ?: ""
                if(Utility.checkIfInternetConnected(requireContext())){
                    mBinding?.swipeTeamList?.isRefreshing = true
                    teamList?.clear()

                    pageNo = 1
                    refRoleId = -1
                    searchStr = searchExpression

                    adReq.pageNo = pageNo
                    adReq.refRoleId = refRoleId
                    adReq.searchStr = searchStr
                    mTeamVM?.getTeamList(adReq)
                }else{
                    Utility.displayMessage(getString(R.string.no_internet_connection),requireContext())
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    fun setRoleSpinner(adRoleList : List<String>){
        var adapter= ArrayAdapter(requireContext(), R.layout.item_role_select_spinner, adRoleList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding?.spRole?.adapter = adapter
        mBinding?.spRole?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //do nothing
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position > 0){
                    if(position == 1){
                        if(Utility.checkIfInternetConnected(requireContext())){
                            mBinding?.swipeTeamList?.isRefreshing = true
                            teamList?.clear()

                            pageNo = 1
                            refRoleId = -1
                            searchStr = null

                            adReq.pageNo = pageNo
                            adReq.refRoleId = refRoleId
                            adReq.searchStr = searchStr
                            mTeamVM?.getTeamList(adReq)
                        }else{
                            Utility.displayMessage(getString(R.string.no_internet_connection),requireContext())
                        }
                    }else{
                        if(Utility.checkIfInternetConnected(requireContext())){
                            mBinding?.swipeTeamList?.isRefreshing = true
                            teamList?.clear()

                            pageNo = 1
                            refRoleId = position.minus(1)
                            searchStr = null

                            adReq.pageNo = pageNo
                            adReq.refRoleId = refRoleId
                            adReq.searchStr = searchStr
                            mTeamVM?.getTeamList(adReq)
                        }else{
                            Utility.displayMessage(getString(R.string.no_internet_connection),requireContext())
                        }
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if(Utility.checkIfInternetConnected(requireContext())){
            mBinding?.swipeTeamList?.isRefreshing = true
            mBinding?.searchTeammate?.text?.clear()
            teamList?.clear()
            pageNo = 1
            refRoleId = -1
            searchStr = ""

            adReq.pageNo = pageNo
            adReq.refRoleId = refRoleId
            adReq.searchStr = searchStr
            mTeamVM?.getTeamList(adReq)
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),requireContext())
        }
    }

    fun setRecyclerAdapter(){
        teamList?.clear()
        mBinding?.teamRecyclerList?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mAdapter = TeamRecyclerAdapter(requireContext(),teamList )
        mBinding?.teamRecyclerList?.adapter = mAdapter
    }

    companion object {
        fun newInstance() = TeamListFragment()
    }

    override fun onSuccessAdminDetails() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                pageNo = pageNo?.plus(1)
                mBinding?.swipeTeamList?.isRefreshing = false
                var adminDataList = Utility.getAdminTeam()
                adminDataList?.forEach {
                    teamList.add(it)
                }
                mBinding?.txtTotalCount?.text = "Total Count : ${teamList.size}"
                mAdapter?.updateList(teamList)
            })
        } catch (e: Exception) {
            Log.e("Team List", "Exception onSuccess " + e.message)
        }
    }

    override fun onFailureAdminDetails() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                mBinding?.swipeTeamList?.isRefreshing = false
                Utility.displayMessage("Error fetching list",requireContext())
            })
        } catch (e: Exception) {
            Log.e("Team List", "Exception onFailure " + e.message)
        }
    }
}