package com.adrosonic.craftexchange.ui.modules.Notification

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.Notifications
import com.adrosonic.craftexchange.database.predicates.NotificationPredicates
import com.adrosonic.craftexchange.database.predicates.WishlistPredicates
import com.adrosonic.craftexchange.syncManager.SyncCoordinator
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.NotificationViewModel
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_artisan_add_product_template.*
import kotlinx.android.synthetic.main.fragment_notifcation.*
import kotlinx.android.synthetic.main.fragment_notifcation.empty_view
import kotlinx.android.synthetic.main.fragment_notifcation.swipe_refresh_layout

/**
 * A fragment representing a list of Items.
 */
class NotifcationFragment : Fragment(),
    NotificationViewModel.NotificationInterface,
    NotificationAdapter.NotificationUpdatedListener {

    val mViewModel: NotificationViewModel by viewModels()
    private lateinit var notificationAdapter: NotificationAdapter
    var coordinator: SyncCoordinator? = null
    var dialog : Dialog?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notifcation, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.listener = this

        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mViewModel.getAllNotifications()
        }


        notificationList.layoutManager =LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        notificationAdapter = NotificationAdapter(requireContext(), mViewModel.getNotificationsMutableData().value)
        notificationList.adapter = notificationAdapter
        notificationAdapter.listener=this
        notificationList.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            if (notificationAdapter.openItems.size > 0) {
                notificationAdapter.closeAllExcept(null)
            }
        }
        mViewModel.getNotificationsMutableData().observe(viewLifecycleOwner, Observer<RealmResults<Notifications>> {
                notificationAdapter.updateNotificationlist(it)
            })
        setVisiblities()
        readAll.setOnClickListener {
            showDeleteDialog()
        }
        swipe_refresh_layout.isRefreshing = true
        swipe_refresh_layout.setOnRefreshListener {
            if (!Utility.checkIfInternetConnected(requireContext())) {
                Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
            } else {
                mViewModel.getAllNotifications()
            }
        }
        notification_elements.text ="${mViewModel.getNotificationsMutableData().value?.size} "+getString(R.string.new_notifications)

    }
    fun setVisiblities() {
        if (mViewModel.getNotificationsMutableData().value?.size!! > 0) {
            notificationList?.visibility = View.VISIBLE
            empty_view?.visibility = View.GONE
            readAll?.visibility=View.VISIBLE
            notification_elements.text =  "${mViewModel.getNotificationsMutableData().value?.size} "+getString(R.string.new_notifications)
        } else {
            notificationList?.visibility = View.GONE
            empty_view?.visibility = View.VISIBLE
            readAll?.visibility=View.GONE
            notification_elements?.text = getString(R.string.no_new_notifications)

        }
        badgeCountListener?.onBadgeCOuntUpdated()
    }
    fun showDeleteDialog() {
        var dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_removefrom_wishlist)
        dialog.show()
        val txt_dscrp = dialog.findViewById(R.id.txt_dscrp) as TextView
        val tvCancel = dialog.findViewById(R.id.txt_cancel) as TextView
        val tvDelete = dialog.findViewById(R.id.txt_back) as TextView
        tvDelete.setText("Mark all as read", TextView.BufferType.NORMAL)
        txt_dscrp.setText(getString(R.string.mark_all_noti_confirmation), TextView.BufferType.NORMAL)
        tvCancel.setOnClickListener {
            dialog.cancel()
        }
        tvDelete.setOnClickListener {
            //todo get all noti id
            setVisiblities()
            if(Utility.checkIfInternetConnected(requireContext())) {
                mViewModel.markAllNotificationsAsRead()
            }
            dialog.cancel()
        }
    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Notifications", "Onsucces")
                if(swipe_refresh_layout!=null)swipe_refresh_layout.isRefreshing = false
                mViewModel.getNotificationsMutableData()
                setVisiblities()
            }
            )
        } catch (e: Exception) {
            Log.e("Notifications", "Exception onSuccess " + e.message)
        }
    }
    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                swipe_refresh_layout.isRefreshing = false
                mViewModel.getNotificationsMutableData()
                Utility.displayMessage(getString(R.string.err_fetch_noti), requireContext())
                setVisiblities()
            }
            )
        } catch (e: Exception) {
        }
    }
    override fun onSelected(productId: Long, isRead: Long) {
        NotificationPredicates.updateNoificationForRead(productId)
        mViewModel.getNotificationsMutableData()
        setVisiblities()
        if(Utility.checkIfInternetConnected(requireContext())) {
            coordinator = SyncCoordinator(requireContext())
            coordinator?.performLocallyAvailableActions()
        }
    }
    companion object {
        var badgeCountListener: notifcationsInterface?=null
        interface notifcationsInterface{
            fun onBadgeCOuntUpdated()
        }
        @JvmStatic
        fun newInstance() =
            NotifcationFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}