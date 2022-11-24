package com.aksantara.simk3.view.main.remainder

import androidx.recyclerview.widget.RecyclerView
import com.aksantara.simk3.databinding.ItemRvRemainderBinding
import com.aksantara.simk3.models.Reminder
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class RemainderViewHolder(private val binding: ItemRvRemainderBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Reminder) {
        with(binding) {

            tvTitle.text = data.reminderTitle
            tvDesc.text = data.reminderDesc
            tvDate.text = data.reminderDate

            with(itemView) {
                setOnClickListener {

                }
            }

        }
    }
}