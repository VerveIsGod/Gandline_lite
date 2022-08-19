package com.example.gandline.utils

import com.example.gandline.LibConstants
import com.example.gandline.objects.Account
import com.example.gandline.objects.Community
import com.example.gandline.objects.User
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.util.UUID

object Serialization {
    fun createAuthBody(
        email: String,
        password: String
    ): String {
        val jsonData = JsonObject()
        jsonData.addProperty("email", email)
        jsonData.addProperty("v", 2)
        jsonData.addProperty("secret", "0 $password")
        jsonData.addProperty("deviceID", LibConstants.DEFAULT_DEVICE_ID)
        jsonData.addProperty("clientType", 100)
        jsonData.addProperty("action", "normal")
        jsonData.addProperty("timestamp", System.currentTimeMillis())
        return jsonData.toString()
    }

    fun createAddInfluencerBody(fee: Int = 500): String {
        val jsonData = JsonObject()
        jsonData.addProperty("monthlyFee", fee)
        jsonData.addProperty("timestamp", System.currentTimeMillis())

        return jsonData.toString()
    }

    fun createBlogTransferBody(coins: Int): String {
        val jsonData = JsonObject()
        val tippingContext = JsonObject()

        jsonData.addProperty("coins", coins)
        tippingContext.addProperty("transactionId", UUID.randomUUID().toString())
        jsonData.add("tippingContext", tippingContext)
        jsonData.addProperty("timestamp", System.currentTimeMillis())

        return jsonData.toString()
    }

    fun createSubscribeBody(): String {
        val jsonData = JsonObject()
        val paymentContext = JsonObject()

        paymentContext.addProperty("transactionId", UUID.randomUUID().toString())
        paymentContext.addProperty("isAutoRenew", false)
        jsonData.add("paymentContext", paymentContext)
        jsonData.addProperty("timestamp", System.currentTimeMillis())

        return jsonData.toString()
    }

    fun extractAccount(response: String, email: String, password: String, authorizedDeviceId: String): Account {
        val responseJson = JsonParser.parseString(response).asJsonObject
        val accountJson = responseJson["account"].asJsonObject
        val phone = if (accountJson["phoneNumber"].isJsonNull) {
            null
        } else {
            accountJson["phoneNumber"].asString
        }
        return Account(
            email,
            accountJson["nickname"].asString,
            password,
            responseJson["sid"].asString,
            accountJson["uid"].asString,
            responseJson["auid"].asString,
            accountJson["aminoId"].asString,
            accountJson["aminoIdEditable"].asBoolean,
            accountJson["createdTime"].asString,
            phone,
            responseJson["secret"].asString,
            authorizedDeviceId
        )
    }
    fun extractCommunityList(response: String): MutableList<Community> {
        val json = JsonParser.parseString(response).asJsonObject
        val array = json["communityList"].asJsonArray
        val list = mutableListOf<Community>()
        array.forEach {
            val currentJson = it.asJsonObject
            if (currentJson["status"].asInt == 0) {
                list.add(
                    Community(
                        currentJson["name"].asString,
                        currentJson["ndcId"].asString,
                        if (currentJson["icon"].isJsonNull) {
                            "https://i.ytimg.com/vi/DTTHyOJi2gQ/maxresdefault.jpg"
                        } else {
                            currentJson["icon"].asString
                        }
                    )
                )
            }
        }
        return list
    }
    fun extractUserList(response: String, ndcId: String): MutableList<User> {
        val json = JsonParser.parseString(response).asJsonObject
        val array = json["userProfileList"].asJsonArray
        val list = mutableListOf<User>()
        array.forEach {
            val currentJson = it.asJsonObject
            list.add(
                User(
                    currentJson["nickname"].asString,
                    currentJson["uid"].asString,
                    ndcId
                )
            )
        }
        return list
    }
    fun extractAccountInfo(response: String): MutableList<String> {
        val json = JsonParser.parseString(response).asJsonObject
        val accountInfo = json["userProfile"].asJsonObject
        val list = mutableListOf<String>()

        list.add(accountInfo["nickname"].asString)
        list.add(accountInfo["icon"].asString)
        list.add("@android:color/white")
        return list
    }
    fun extractWalletInfo(response: String): String {
        val json = JsonParser.parseString(response).asJsonObject
        val walletInfo = json["wallet"].asJsonObject

        return walletInfo["totalCoins"].asString
    }
    fun extractLinkInfo(response: String): MutableList<String> {
        val json = JsonParser.parseString(response).asJsonObject
        val linkInfo = json["linkInfoV2"].asJsonObject
        val path = linkInfo["path"].asString
        val comId = path.split("/")[0].replace("x", "")
        val objectId = linkInfo["extensions"].asJsonObject["linkInfo"].asJsonObject["objectId"]
        val list = mutableListOf<String>()

        list.add(comId)
        list.add(objectId.asString)
        return list
    }
    fun extractInfluencerInfo(response: String): Int {
        val json = JsonParser.parseString(response).asJsonObject
        val accountInfo = json["userProfile"].asJsonObject
        return accountInfo["influencerInfo"].asJsonObject["monthlyFee"].asInt
    }
    fun extractCommunityInfo(response: String): MutableList<String> {
        val json = JsonParser.parseString(response).asJsonObject
        val community = json["community"].asJsonObject
        val influencers = community["influencerList"].asJsonArray
        val list = mutableListOf<String>()

        influencers.forEach { influencer ->
            list.add(influencer.asJsonObject["uid"].asString)
        }
        return list
    }
}

