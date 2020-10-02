package com.adrosonic.craftexchange.ui.modules.transaction

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.Transactions
import com.adrosonic.craftexchange.database.predicates.TransactionPredicates
import com.adrosonic.craftexchange.databinding.FragmentBuyerOnGoTransacBinding
import com.adrosonic.craftexchange.databinding.FragmentCompTransacBinding
import com.adrosonic.craftexchange.ui.modules.transaction.adapter.BuyerOnGoTranRecyclerAdapter
import com.adrosonic.craftexchange.ui.modules.transaction.adapter.CompTransRecyclerAdapter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.TransactionViewModel
import io.realm.RealmResults

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CompTransacFragment : Fragment(),
    TransactionViewModel.TransactionInterface {

    private var param1: String? = null
    private var param2: String? = null

    var mBinding : FragmentCompTransacBinding?= null

    val mTranVM : TransactionViewModel by viewModels()

    var mTranListAdapter : CompTransRecyclerAdapter?= null

    var mTranList : RealmResults<Transactions>?= null
    var mFilteredList : RealmResults<Transactions>?= null
    var mSearchedList : RealmResults<Transactions>?= null


    var filterList : Array<String> ?= null

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_comp_transac, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerList()
        mTranVM?.transactionListener = this

        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mTranVM.getCompletedTransactions("", 0L)
        }

        filterList = resources.getStringArray(R.array.filter_transac_items)

        mTranVM.getCompTranListMutableData()
            .observe(viewLifecycleOwner, Observer<RealmResults<Transactions>> {
                mTranList = it
                mTranListAdapter?.updateTransactionList(mTranList)
            })

        mBinding?.swipeCompletedTransactions?.isRefreshing = true
        mBinding?.swipeCompletedTransactions?.setOnRefreshListener {
            if (!Utility.checkIfInternetConnected(requireContext())) {
                Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
            } else {
                mTranVM.getCompletedTransactions("", 0L)
            }
        }

        mBinding?.btnFilterTransactions?.setOnClickListener {
            filterList?.let { it1 -> filterDialog(it1) }
        }

        mBinding?.searchCompletedTransac?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query:String):Boolean {
                mSearchedList = TransactionPredicates.getTransactionByEnquiryId(query.toLong())
                mTranListAdapter?.updateTransactionList(mSearchedList)
                return false
            }
            override fun onQueryTextChange(newText:String):Boolean {
                mSearchedList = TransactionPredicates.getTransactionByEnquiryId(newText.toLong())
                mTranListAdapter?.updateTransactionList(mSearchedList)
                return false
            }
        })
    }

    fun filterDialog(array: Array<String>){
        val mBuilder = AlertDialog.Builder(requireContext())
        mBuilder.setTitle(getString(R.string.filter_transac_by))
        mBuilder.setSingleChoiceItems(array, -1
        ) { dialogInterface, i ->
            mFilteredList = TransactionPredicates.getFilteredTransactions(array[i],true)
            mTranListAdapter?.updateTransactionList(mFilteredList)

            if(mFilteredList?.size == 0){
                mBinding?.emptyView?.visibility = View.VISIBLE
                mBinding?.completedTransacRecyclerList?.visibility = View.GONE
            }else{
                mBinding?.emptyView?.visibility = View.GONE
                mBinding?.completedTransacRecyclerList?.visibility = View.VISIBLE
            }

            dialogInterface.dismiss()
        }
        val mDialog = mBuilder.create()
        mDialog.show()
    }

    private fun setRecyclerList(){
        mBinding?.completedTransacRecyclerList?.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        mTranListAdapter = CompTransRecyclerAdapter(requireContext(), mTranVM.getCompTranListMutableData().value!!)
        mBinding?.completedTransacRecyclerList?.adapter = mTranListAdapter
//        mEnqListAdapter?.enqListener = this  //important to set adapter first and then call listener
    }

    fun setVisiblities() {
        if (mTranVM.getCompTranListMutableData().value?.size!! > 0) {
            mBinding?.completedTransacRecyclerList?.visibility = View.VISIBLE
            mBinding?.emptyView?.visibility = View.GONE
        } else {
            mBinding?.completedTransacRecyclerList?.visibility = View.GONE
            mBinding?.emptyView?.visibility = View.VISIBLE
        }
    }


    override fun onGetTransactionsSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("CompTransactionList", "onSuccess")
                mBinding?.swipeCompletedTransactions?.isRefreshing = false
                mTranVM.getCompTranListMutableData()
                setVisiblities()
            })
        } catch (e: Exception) {
            Log.e("CompTransactionList", "Exception onSuccess " + e.message)
        }
    }

    override fun onGetTransactionsFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("CompTransactionList", "OnFailure")
                mBinding?.swipeCompletedTransactions?.isRefreshing = false
                mTranVM.getCompTranListMutableData()
                Utility.displayMessage("Error while fetching Transactions", requireContext())
                setVisiblities()
            })
        } catch (e: Exception) {
            Log.e("CompTransactionList", "Exception onFailure " + e.message)
        }
    }


    companion object {
        fun newInstance() = CompTransacFragment()
    }
}