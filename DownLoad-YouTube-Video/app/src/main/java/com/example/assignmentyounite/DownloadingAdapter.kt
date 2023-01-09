package com.example.assignmentyounite

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DownloadingAdapter(private var list: MutableList<DownLoadModel>): RecyclerView.Adapter<DownloadingAdapter.DownLoadingHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownLoadingHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.download_view, parent, false)
        return DownLoadingHolder(view)
    }

    override fun onBindViewHolder(holder: DownLoadingHolder, position: Int) {
        val model = list[position]

        holder.link.text = model.link
        holder.progress.text = "${model.progress} %"
    }

    override fun getItemCount() = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(lis: MutableList<DownLoadModel>){
        list = lis
        notifyDataSetChanged()
    }

    class DownLoadingHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val link: TextView = itemView.findViewById(R.id.link)
        val progress: TextView = itemView.findViewById(R.id.progress)
    }
}