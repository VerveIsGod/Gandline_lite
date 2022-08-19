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
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.example.gandline.R
import com.example.gandline.activity.LoginActivity
import com.example.gandline.http.AminoRequest
import com.example.gandline.utils.Serialization
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.concurrent.thread


class BlogFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_blog, container, false)
        val coinsAmount = view.findViewById<TextInputEditText>(R.id.blogCoinsAmountEditText)
        val blogLink = view.findViewById<TextInputEditText>(R.id.blogLinkEditText)

        coinsAmount.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            0,
            0)
        blogLink.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            0,
            0)

        return setup(view)
    }

    @SuppressLint("SetTextI18n")
    private fun setup(view: View): View {
        val coinsAmount = view.findViewById<TextInputEditText>(R.id.blogCoinsAmountEditText)
        val blogLink = view.findViewById<TextInputEditText>(R.id.blogLinkEditText)
        val sendButton = view.findViewById<Button>(R.id.sendSubmitButtonBlog)

        val coinsLay = view.findViewById<TextInputLayout>(R.id.coins_lay_blog)
        val linkLay = view.findViewById<TextInputLayout>(R.id.link_lay_blog)

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

        blogLink.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                linkLay.error = null
                blogLink.setCompoundDrawablesWithIntrinsicBounds(
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
            ) {blogLink.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                0,
                0)}
        })

        sendButton.setOnClickListener {

            if (blogLink.text!!.isEmpty() and coinsAmount.text!!.isEmpty()) {
                coinsLay.error = "Please enter a coins amount"
                coinsLay.setErrorIconDrawable(R.drawable.no_text_icon)
                linkLay.error = "Please enter a link to a user"
                linkLay.setErrorIconDrawable(R.drawable.no_text_icon)
            } else if (coinsAmount.text!!.isEmpty()) {
                coinsLay.error = "Please enter a coins amount"
                coinsLay.setErrorIconDrawable(R.drawable.no_text_icon)
            } else if (blogLink.text!!.isEmpty()) {
                linkLay.error = "Please enter a link to a user"
                linkLay.setErrorIconDrawable(R.drawable.no_text_icon)
            } else {
                var total = 0
                AminoRequest.initRequest(
                    "GET",
                    "/g/s/link-resolution?q=${blogLink.text.toString()}",
                    requireActivity()
                )
                    .async().send {
                        val objectList = Serialization.extractLinkInfo(it)
//                        Toast.makeText(requireContext(),
//                            "${objectList[0]} || ${objectList[1]}",
//                            Toast.LENGTH_SHORT).show()
                        val comId = objectList[0]
                        val blogId = objectList[1]


                        var coins = coinsAmount.text.toString().toInt()
                        val staticCoins = coinsAmount.text.toString().toInt()
                        val counterText = view.findViewById<TextView>(R.id.sent_counter_text_blog)

                        counterText.text = "0/$staticCoins"

                        while (coins >= 500){
                            coins -= 500
                            thread {
                                AminoRequest.initRequest(
                                    "POST",
                                    "/x$comId/s/blog/$blogId/tipping",
                                    requireActivity()
                                )
                                    .async()
                                    .addBody(Serialization.createBlogTransferBody(500))
                                    .send {

                                    }
                            }
                            total += 500
                            counterText.text = "$total/$staticCoins"
                        }
                        if (coins < 500){
                            thread {
                                AminoRequest.initRequest(
                                    "POST",
                                    "/x$comId/s/blog/$blogId/tipping",
                                    requireActivity()
                                )
                                    .async()
                                    .addBody(Serialization.createBlogTransferBody(coins))
                                    .send {

                                    }
                            }
                            coins -= coins
                            total += coins
                            counterText.text = "$total/$staticCoins"
                        }
                    }
            }
        }

        return view
    }
}