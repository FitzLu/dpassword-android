package com.dpass.android.mnemonic

import com.dpass.android.utils.Logger
import org.bitcoinj.wallet.DeterministicSeed
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*




class MnemonicGenerator {

    companion object {
        const val mTag = "MnemonicGenerator"
    }

    fun createMnemonicIndex(phraseCount: Int, wordList: MutableList<String>): ArrayList<Int>{
        val creationTime = System.currentTimeMillis()

        //正推
        val entropy             = generateEntropy()
        val entropyWithCheckSum = computeCheckSum(entropy)
        val indexsMaybeNull     = computeIndex(entropyWithCheckSum)

        if (indexsMaybeNull.size != phraseCount){
            throw RuntimeException("indexs count ${indexsMaybeNull.size} not equal phrase count $phraseCount")
        }

        val indexs              = arrayListOf<Int>()
        indexsMaybeNull.withIndex().forEach {
            if (it.value == null){
                throw RuntimeException("No.${it.index} value is null")
            }
            indexs.add(it.value!!)
        }

        var words  = ""
        indexs.forEach {
            words = "$words $it "
        }
        words = words.trim()

        //反推
        val passphrase = ""

        val seed = DeterministicSeed(words, null, passphrase, creationTime)
        seed.mnemonicCode

        return indexs
    }

    //生成一个长度为 128~256 位 (bits) 的随机序列(熵)
    private fun generateEntropy(): ByteArray{
        //先取128位随机散列
        var entropyBody = ByteArray(128 / 8)
        SecureRandom().nextBytes(entropyBody)

        //再取 0 ~ 128 位随机散列补位，使最后的散列值介于128位到256位, 且能被 32 整除
//        val nextInt = SecureRandom().nextInt(4)//0 1 2 3 4
//        if (nextInt > 0) {
//            val entropyTail = ByteArray(32 * nextInt / 8)//32 64 96 128
//            val ent = ByteArray(entropyBody.size + entropyTail.size)
//            entropyBody.withIndex().forEach {
//                ent[it.index] = it.value
//            }
//            entropyTail.withIndex().forEach {
//                ent[it.index + entropyBody.size] = it.value
//            }
//
//            entropyBody = ent
//        }

        val entropy = ByteArray(entropyBody.size)
        entropyBody.withIndex().forEach {
            entropy[it.index] = it.value
        }

        val size = entropy.size
        Logger.i(mTag, "generateEntropy() => bytes length $size, bits length ${size * 8}")

        return entropy
    }

    //取熵哈希后的前 n 位作为校验和 (n= 熵长度/32), 拼接到散列末尾
    //熵 + 校验和
    private fun computeCheckSum(entropy: ByteArray): ByteArray{
        val count = entropy.size * 8 / 32 / 4 //1
        val shaValue = sha256(entropy)

        val entropyWithCheckSum = Arrays.copyOf(entropy, entropy.size + count)

        for (i in 0 until count){
            entropyWithCheckSum[entropy.size + i] = shaValue[i]
        }

        Logger.i(mTag, "check sum bits length $count")
        Logger.i(mTag, "entropy with check sum => $entropyWithCheckSum bits length is ${entropyWithCheckSum.size * 8}")
        Logger.i(mTag, "entropy with check sum bit length => ${entropyWithCheckSum.size * 8}")

        return entropyWithCheckSum
    }

    private fun computeIndex(entropyWithCheckSum: ByteArray): kotlin.Array<Int?> {
        val indexCount = entropyWithCheckSum.size * 8 / 11
        val indexs = arrayOfNulls<Int>(indexCount)
        for (i in 0 until indexCount){
            indexs[i] = next11Bits(entropyWithCheckSum, i)
        }

        Logger.i(mTag, "word index count => $indexCount")
        Logger.i(mTag, "word index => $indexs")

        return indexs
    }

    private fun sha256(bytes: ByteArray): ByteArray {
        return sha256(bytes, 0, bytes.size)
    }

    private fun sha256(bytes: ByteArray, offset: Int, length: Int): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(bytes, offset, length)
        return digest.digest()
    }

    private fun next11Bits(bytes:ByteArray, offset:Int):Int {
        val skip = offset / 8
        val lowerBitsToRemove = 3 * 8 - 11 - offset % 8
        return ((((bytes[skip].toInt() and 0xff shl 16 or (
                bytes[skip + 1].toInt() and 0xff shl 8) or
                if (lowerBitsToRemove < 8)
                    bytes[skip + 2].toInt() and 0xff
                else
                    0)) shr lowerBitsToRemove) and (1 shl 11) - 1)
    }

    private fun parseHex(c: Char): Int {
        if (c in '0'..'9') return c - '0'
        if (c in 'a'..'f') return c - 'a' + 10
        if (c in 'A'..'F') return c - 'A' + 10
        throw RuntimeException("Invalid hex char '" + c + '\''.toString())
    }

    private fun entropyLengthPreChecks(ent: Int) {
        if (ent < 128)
            throw RuntimeException("Entropy too low, 128-256 bits allowed")
        if (ent > 256)
            throw RuntimeException("Entropy too high, 128-256 bits allowed")
        if (ent % 32 > 0)
            throw RuntimeException("Number of entropy bits must be divisible by 32")
    }
}