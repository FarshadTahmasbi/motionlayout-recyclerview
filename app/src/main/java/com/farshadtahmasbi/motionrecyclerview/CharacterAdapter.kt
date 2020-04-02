package com.farshadtahmasbi.motionrecyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_image.view.*

class CharacterAdapter(
    private val items: List<Data>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<CharacterAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_image,
                    parent,
                    false
                )
            , itemClickListener
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    class Holder(itemView: View, private val itemClickListener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        private lateinit var data : Data
        init {
            itemView.img.setOnClickListener {
                itemClickListener.onItemClick(
                    itemView,
                    layoutPosition,
                    data
                )
            }
        }

        fun bind(data : Data){
            this.data = data
            itemView.img.load(data.imageRes)
        }
    }

    data class Data(
        @DrawableRes val imageRes: Int,
        @DrawableRes val headerRes: Int,
        @StringRes val title: Int,
        @StringRes val desc: Int
    )

    interface OnItemClickListener {
        fun onItemClick(
            view: View,
            position: Int,
            data: Data
        )
    }
}