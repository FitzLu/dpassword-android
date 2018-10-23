package com.dpass.android.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

object SupportActivityUtil {

    private const val mTag = "SupportActivityUtil"

    /**
     * The 'fragment' is added to the container view with id 'frameId'. The operation is
     * performed by the 'fragmentManager'.
     *
     * */
    fun addFragmentToActivity(fragmentManager: FragmentManager,
                              fragment: Fragment,
                              frameId: Int): Unit {

        if (!checkNotNull(fragmentManager) || !checkNotNull(fragment)) {
            return
        }
        val transaction = fragmentManager.beginTransaction()
        transaction.add(frameId, fragment)
        transaction.commitAllowingStateLoss()
    }

    fun showFragmentInActivity(fragmentManager: FragmentManager,
                               fragment: Fragment): Unit {

        if (!checkNotNull(fragmentManager) || !checkNotNull(fragment)) {
            return
        }
        val transaction = fragmentManager.beginTransaction()
        transaction.show(fragment)
        transaction.commitAllowingStateLoss()
    }

    fun switchFragmentInActivity(fragmentManager: FragmentManager,
                                 from: Fragment, to: Fragment,
                                 frameId: Int){
        if (!checkNotNull(fragmentManager) || !checkNotNull(from) || !checkNotNull(to)) {
            return
        }
        val transaction = fragmentManager.beginTransaction()
        if (from.isAdded){
            transaction.hide(from)
        }
        if (!to.isAdded){
            transaction.add(frameId, to)
        }
        transaction.show(to)
        transaction.commitAllowingStateLoss()
    }

    fun jumpActivity(current: Activity, target: Class<out Activity>){
        if (!checkNotNull(current) || !checkNotNull(target)){
            return
        }
        val intent = Intent(current, target)
        jumpByIntent(current, intent)
    }

    fun jumpByIntent(context: Context, intent: Intent) {
        if (!checkNotNull(context) || !checkNotNull(intent)) {
            return
        }
        try {
            context.startActivity(intent)
        }catch (e: Exception){
            Logger.e(mTag, e.toString())
        }
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference an object reference
     * @return true if reference is non-null otherwise false
     * @throws NullPointerException only throw in debug mode
     */
    private fun <T> checkNotNull(reference: T?): Boolean {
//        if (reference == null && BuildConfig.OPEN_LOG) {
//            throw NullPointerException()
//        }
        return reference != null
    }

}