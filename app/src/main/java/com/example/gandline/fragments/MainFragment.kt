package com.example.gandline.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.example.gandline.R
import com.example.gandline.activity.LoginActivity
import com.example.gandline.http.AminoRequest
import com.example.gandline.utils.Serialization
import com.example.gandline.utils.Serialization.extractCommunityInfo
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.concurrent.thread


class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val coinsAmount = view.findViewById<TextInputEditText>(R.id.coinsAmountEditText)
        val userLink = view.findViewById<TextInputEditText>(R.id.userLinkEditText)

        coinsAmount.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            0,
            0)
        userLink.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            0,
            0)

        return setup(view)
    }

    @SuppressLint("SetTextI18n")
    private fun setup(view: View): View {
        val coinsAmount = view.findViewById<TextInputEditText>(R.id.coinsAmountEditText)
        val userLink = view.findViewById<TextInputEditText>(R.id.userLinkEditText)
        val sendButton = view.findViewById<Button>(R.id.sendSubmitButton)

        val coinsLay = view.findViewById<TextInputLayout>(R.id.coins_lay)
        val linkLay = view.findViewById<TextInputLayout>(R.id.link_lay)

        val logoutButton = view.findViewById<ImageView>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            val alert = AlertDialog.Builder(requireActivity())
            alert.setTitle("Выход")
            alert.setMessage("Выйти из аккаунта?")
            alert.setPositiveButton("Да") { dialog, _ ->
                requireActivity().getSharedPreferences("account", Context.MODE_PRIVATE).edit {
                    clear()
                }
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                dialog.dismiss()
                requireActivity().finish()
            }
            alert.setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
            }
            alert.show()
        }

        coinsAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                coinsLay.error = null
                coinsAmount.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.with_text_icon,
                    0)
            }
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {coinsAmount.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                0,
                0)}

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {}
        })

        userLink.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                linkLay.error = null
                userLink.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.with_text_icon,
                    0)
            }
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {userLink.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                0,
                0)}
        })

        sendButton.setOnClickListener {

            if (userLink.text!!.isEmpty() and coinsAmount.text!!.isEmpty()) {
                coinsLay.error = "Please enter a coins amount"
                coinsLay.setErrorIconDrawable(R.drawable.no_text_icon)
                linkLay.error = "Please enter a link to a user"
                linkLay.setErrorIconDrawable(R.drawable.no_text_icon)
            } else if (coinsAmount.text!!.isEmpty()) {
                coinsLay.error = "Please enter a coins amount"
                coinsLay.setErrorIconDrawable(R.drawable.no_text_icon)
            } else if (userLink.text!!.isEmpty()) {
                linkLay.error = "Please enter a link to a user"
                linkLay.setErrorIconDrawable(R.drawable.no_text_icon)
            } else {
                var total = 0
                AminoRequest.initRequest(
                    "GET",
                    "/g/s/link-resolution?q=${userLink.text.toString()}",
                    requireActivity()
                )
                    .async()
                    .send {
                        val objectList = Serialization.extractLinkInfo(it)
//                        Toast.makeText(requireContext(),
//                            "${objectList[0]} || ${objectList[1]}",
//                            Toast.LENGTH_SHORT).show()
                        val comId = objectList[0]
                        val userId = objectList[1]


                        var coins = coinsAmount.text.toString().toInt()
                        val staticCoins = coinsAmount.text.toString().toInt()
                        val counterText = view.findViewById<TextView>(
                            R.id.sent_counter_text)

                        AminoRequest.initRequest(
                            "GET",
                            "/g/s-x$comId/community/info?withInfluencerList=1&withTopicList=true&influencerListOrderStrategy=fansCount",
                            requireActivity()
                        )
                            .async().send {
                            val influencers = extractCommunityInfo(it)
                            var removedInfluencer: String? = null
                            var removedInfluencerFee = 500
                            if ((influencers.size == 3) and (userId !in influencers)) {
                                removedInfluencer = influencers.random()
                                AminoRequest.initRequest(
                                    "DELETE",
                                    "/x$comId/s/influencer/$removedInfluencer",
                                    requireActivity()
                                ).async().send {}
                                AminoRequest.initRequest(
                                    "GET",
                                    "/x$comId/s/user-profile/$removedInfluencer",
                                    requireActivity()
                                )
                                    .async()
                                    .send {
                                        removedInfluencerFee = Serialization.extractInfluencerInfo(it)
                                    }
                            }
                            if (userId !in influencers) {
                                AminoRequest.initRequest(
                                    "POST",
                                    "/x$comId/s/influencer/$userId",
                                    requireActivity()
                                )
                                    .async()
                                    .addBody(Serialization.createAddInfluencerBody(fee=500))
                                    .send {}
                            }

                            AminoRequest.initRequest(
                                "GET",
                                "/x$comId/s/user-profile/$userId",
                                requireActivity()
                            )
                                .async()
                                .send {
                                    val fee = Serialization.extractInfluencerInfo(it)
                                    counterText.text = "0/${fee * (staticCoins / fee)}"
                                    Toast.makeText(requireContext(),
                                        "Started!",
                                        Toast.LENGTH_SHORT).show()
                                    for (i in 1..(coins / fee)) {
                                        thread {
                                            AminoRequest.initRequest(
                                                "POST",
                                                "/x$comId/s/influencer/$userId/subscribe",
                                                requireActivity())
                                                .addBody(
                                                    Serialization.createSubscribeBody()
                                                ).async().send {}
                                        }
                                        coins -= fee
                                        total += fee
                                        if (total > fee * (staticCoins / fee))
                                            total = fee * (staticCoins / fee)
                                        counterText.text =
                                            "${total}/${fee * (staticCoins / fee)}"
                                        if (coins < fee)
                                            break
                                    }
                                    Toast.makeText(requireContext(),
                                        "$total coins sent!!!",
                                        Toast.LENGTH_LONG).show()
                                    total = 0
                                    userLink.text = null
                                    coinsAmount.text = null
                                    coinsAmount.setCompoundDrawablesWithIntrinsicBounds(
                                        0,
                                        0,
                                        0,
                                        0)
                                    userLink.setCompoundDrawablesWithIntrinsicBounds(
                                        0,
                                        0,
                                        0,
                                        0)
                                }
                                if ((influencers.size == 3) and (userId !in influencers)) {
                                    AminoRequest.initRequest(
                                        "DELETE",
                                        "/x$comId/s/influencer/$userId",
                                        requireActivity()
                                    ).async().send {}
                                }
                                if (userId !in influencers) {
                                    AminoRequest.initRequest(
                                        "POST",
                                        "/x$comId/s/influencer/$removedInfluencer",
                                        requireActivity()
                                    )
                                        .async()
                                        .addBody(Serialization.createAddInfluencerBody(fee=removedInfluencerFee))
                                        .send {}
                                }
                        }
                }
            }
        }

        return view
    }
}