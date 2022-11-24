package com.aksantara.simk3.view.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksantara.simk3.databinding.ItemOnboardingBinding
import com.aksantara.simk3.models.Onboarding

class OnboardingAdapter(private val onBoardingData: List<Onboarding>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        return OnboardingViewHolder(
            ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return onBoardingData.size
    }

    override fun onBindViewHolder(
        holder: OnboardingAdapter.OnboardingViewHolder,
        position: Int
    ) {
        holder.bind(onBoardingData[position])
    }

    inner class OnboardingViewHolder(private val binding: ItemOnboardingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(onBoardingData: Onboarding) {
            with(binding) {
                tvTitle.text = onBoardingData.title
                tvDesc.text = onBoardingData.description
                imgOnboarding.setBackgroundResource(onBoardingData.imgHeader)
            }
        }
    }
}