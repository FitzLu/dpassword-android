package com.dpass.android.activities.entry

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import com.dpass.android.R
import com.dpass.android.base.BaseAppActivity
import com.dpass.android.utils.Logger
import kotlinx.android.synthetic.main.activity_generate_entry.*
import java.security.SecureRandom



class GenerateEntryActivity: BaseAppActivity() {

    override var mTag: String = javaClass.simpleName
    override var layoutResId: Int = R.layout.activity_generate_entry
    override var hasToolbar: Boolean = false

    private var currentPasswordLength = 12

    override fun setUpViews(savedInstanceState: Bundle?) {
        findViewById<View>(R.id.ivX)?.setOnClickListener {
            finish()
        }
        ivRefresh?.setOnClickListener {
            tvPassword?.text = generatePassword(currentPasswordLength)
        }
        tvCopy?.setOnClickListener {
            val text =  tvPassword?.text
            if (!text.isNullOrEmpty()) {
                try {
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("", text)
                    clipboard.primaryClip = clip
                    showToast(getString(R.string.copy_success))
                }catch (e: Exception){
                    Logger.e(e.toString())
                    showToast(getString(R.string.copy_failed))
                }
            }
        }
        lengthSeek?.max = 100
        lengthSeek?.progress = currentPasswordLength
        tvLength?.text = currentPasswordLength.toString()
        tvPassword?.text = generatePassword(currentPasswordLength)
        lengthSeek?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentPasswordLength = progress
                tvLength?.text = currentPasswordLength.toString()
                tvPassword?.text = generatePassword(currentPasswordLength)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
    }

    override fun work(savedInstanceState: Bundle?) {

    }

    private var words = "abcdefghijklmnopqrstuvwxyz1234567890"

    private fun generatePassword(passwordLength: Int): String{
        if (passwordLength < 1){
            return  ""
        }
        val wordsList = arrayListOf<String>()
        words.toCharArray().mapTo(wordsList, {it.toString()})
        wordsList.shuffle()
        val wordsCount = wordsList.size
        val secureRandom = SecureRandom()
        val resultCharList = arrayListOf<String>()
        for (i in 0 until passwordLength){
            var pick        = wordsList[secureRandom.nextInt(wordsCount - 1)]
            val isUppercase = secureRandom.nextInt(100) % 2 == 0
            if (isUppercase) pick = pick.toUpperCase()
            resultCharList.add(pick)
        }
        resultCharList.shuffle()
        var password = ""
        resultCharList.forEach {
            password = "$password$it"
        }
        return password
    }
}