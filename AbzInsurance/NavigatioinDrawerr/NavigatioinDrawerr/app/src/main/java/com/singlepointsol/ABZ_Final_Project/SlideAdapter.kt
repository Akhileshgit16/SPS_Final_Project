package com.singlepointsol.ABZ_Final_Project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.singlepointsol.navigatioindrawerr.R

class SlideAdapter(private val slideList: List<Int>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_WELCOME = 0
        const val TYPE_BUY_INSURANCE = 1
        const val TYPE_MANAGE_POLICIES = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_WELCOME -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.slide_welcome, parent, false)
                WelcomeViewHolder(view)
            }
            TYPE_BUY_INSURANCE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.slide_buyinsurance, parent, false)
                BuyInsuranceViewHolder(view)
            }
            TYPE_MANAGE_POLICIES -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.slide_manage_policies, parent, false)
                ManagePoliciesViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val context = holder.itemView.context // Get the context

        when (holder) {
            is WelcomeViewHolder -> {
                holder.titleText.text = context.getString(R.string.welcome_title)
                holder.subTitleText.text = context.getString(R.string.welcome_subtitle)
                holder.instructionText.text = context.getString(R.string.welcome_instruction)
                holder.logoImage.setImageResource(R.drawable.carinsurancelogo)
            }
            is BuyInsuranceViewHolder -> {
                holder.titleBuyInsurance.text = context.getString(R.string.buy_insurance_title)
                holder.subTitleBuyInsurance.text = context.getString(R.string.buy_insurance_subtitle)
                holder.instructionBuyInsurance.text = context.getString(R.string.buy_insurance_instruction)
                holder.buyInsuranceImage.setImageResource(R.drawable.carbuyinsurance)
            }
            is ManagePoliciesViewHolder -> {
                holder.titleManagePolicies.text = context.getString(R.string.manage_policies_title)
                holder.subTitleManagePolicies.text = context.getString(R.string.manage_policies_subtitle)
                holder.instructionManagePolicies.text = context.getString(R.string.manage_policies_instruction)
                holder.managePoliciesImage.setImageResource(R.drawable.carpolicyclaim)
            }
        }
    }

    override fun getItemCount(): Int {
        return slideList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_WELCOME
            1 -> TYPE_BUY_INSURANCE
            2 -> TYPE_MANAGE_POLICIES
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

    // ViewHolder for Welcome Slide
    class WelcomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val subTitleText: TextView = itemView.findViewById(R.id.subTitleText)
        val instructionText: TextView = itemView.findViewById(R.id.instructionText)
        val logoImage: ImageView = itemView.findViewById(R.id.logoImage)
    }

    // ViewHolder for Buy Insurance Slide
    class BuyInsuranceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleBuyInsurance: TextView = itemView.findViewById(R.id.titleBuyInsurance)
        val subTitleBuyInsurance: TextView = itemView.findViewById(R.id.subTitleBuyInsurance)
        val instructionBuyInsurance: TextView = itemView.findViewById(R.id.instructionBuyInsurance)
        val buyInsuranceImage: ImageView = itemView.findViewById(R.id.buyInsuranceImage)
    }

    // ViewHolder for Manage Policies Slide
    class ManagePoliciesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleManagePolicies: TextView = itemView.findViewById(R.id.titleManagePolicies)
        val subTitleManagePolicies: TextView = itemView.findViewById(R.id.subTitleManagePolicies)
        val instructionManagePolicies: TextView = itemView.findViewById(R.id.instructionManagePolicies)
        val managePoliciesImage: ImageView = itemView.findViewById(R.id.managePoliciesImage)
    }
}
