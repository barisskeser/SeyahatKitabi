package com.example.seyahatkitabikotlin.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.seyahatkitabikotlin.Model.Place
import com.example.seyahatkitabikotlin.Presenter.MainActivityPresenter
import com.example.seyahatkitabikotlin.databinding.RecyclerItemBinding

class RecyclerAdapter (val placeObjs : List<Place>, val presenter : MainActivityPresenter) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        println()


        println("fdsf")
        println("fdsf")
        println("fdsf")

        val binding = RecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


    }

    override fun getItemCount(): Int {
        return placeObjs.size
    }

    class ViewHolder (val binding : View) :
        RecyclerView.ViewHolder(binding){

    }
}