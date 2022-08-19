package com.example.gandline.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.target.Target
import com.example.gandline.R
import com.example.gandline.activity.LoginActivity
import com.example.gandline.http.AminoRequest
import com.example.gandline.utils.Serialization
import kotlin.math.roundToInt


@GlideModule
class AppGlideModule : AppGlideModule()

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        val view =  inflater.inflate(R.layout.fragment_profile, container, false)
        return setup(view)
    }


//    val profilePicture = view.findViewById<ImageView>(R.id.profile_picture)

    private fun setupIcon(view: View, link: String, destination: ImageView) {
        GlideApp.with(view.context)
            .load(link)
            .circleCrop()
            .error(R.drawable.bunny_icon)
            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            .into(destination)
    }

    private fun setup(view: View): View {

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

        AminoRequest.initRequest("GET",
            "/g/s/user-profile/${AminoRequest.uid}",
            requireActivity())
            .async()
            .send {
                val accountData = Serialization.extractAccountInfo(it)
                val accountNickname = view.findViewById<TextView>(R.id.account_nickname)
                val profilePicture = view.findViewById<ImageView>(R.id.profile_picture)
                accountNickname.text = accountData[0]
                setupIcon(view,
                    accountData[1]
                        .replace(" ", "")
                        .replace("\"", ""),
                    profilePicture)
                }

        AminoRequest.initRequest("GET",
        "/g/s/wallet",
        requireActivity())
            .async()
            .send {
                val accountMoney = view.findViewById<TextView>(R.id.coins_amount)
                accountMoney.text = Serialization.extractWalletInfo(it)
                    .toFloat()
                    .roundToInt()
                    .toString()
                accountMoney.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ac,
                    0)
            }

        return view
    }

}