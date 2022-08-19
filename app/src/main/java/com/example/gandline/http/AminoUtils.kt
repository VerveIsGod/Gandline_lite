package com.example.gandline.http

import io.ktor.util.*
import android.util.Base64
import com.example.gandline.LibConstants
import com.example.gandline.decodeTheHex
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private val ndcPrefix = Base64.decode("Qg==", Base64.NO_WRAP)
private val ndcDeviceKey = Base64.decode("ArJYxjVZ2IBDIcXVBLrzIDWNNm8=", Base64.NO_WRAP)

object AminoUtils {

    fun getSignature(data: String): String {
        val mac = Mac.getInstance(LibConstants.HMAC_SHA_1)
        mac.init(SecretKeySpec(LibConstants.NDC_MSG_SIG_KEY.decodeTheHex(), LibConstants.HMAC_SHA_1))
        return Base64.encodeToString("42".decodeTheHex() + mac.doFinal(data.toByteArray()), Base64.NO_WRAP)
    }

    fun getDeviceId(): String {
        val token = ByteArray(15)
        Random().nextBytes(token)
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(SecretKeySpec(ndcDeviceKey, "HmacSHA1"))
        mac.update(ndcPrefix)
        mac.update(token)
        return hex(ndcPrefix) + hex(token) + hex(mac.doFinal())
    }

}