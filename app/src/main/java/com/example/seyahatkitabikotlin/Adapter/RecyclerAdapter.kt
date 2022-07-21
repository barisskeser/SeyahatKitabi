package com.example.seyahatkitabikotlin.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.seyahatkitabikotlin.Model.Place
import com.example.seyahatkitabikotlin.Presenter.MainActivityPresenter
import com.example.seyahatkitabikotlin.databinding.RecyclerItemBinding

class RecyclerAdapter (val placeObjs : List<Place>, val presenter : MainActivityPresenter) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)



        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.textView.text = placeObjs[position].name

        holder.binding.textView.setOnClickListener{
            presenter.onClickPlace(placeObjs[position])
        }

    }

    override fun getItemCount(): Int {
        return placeObjs.size
    }

    class ViewHolder (val binding : RecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root){

    }
}