package com.sunayanpradhan.onlinevoting.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sunayanpradhan.onlinevoting.R
import com.sunayanpradhan.onlinevoting.Models.TeamInformation

class VoteAdapter(var list: ArrayList<TeamInformation>, var context: Context) : RecyclerView.Adapter<VoteAdapter.ViewHolder>(){

    var singleitem_selection_position=-1

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        val layoutCard:CardView=itemView.findViewById(R.id.layout_card)
        val teamLogo:ImageView=itemView.findViewById(R.id.team_logo)
        val teamName:TextView=itemView.findViewById(R.id.team_name)
        val teamSlogan:TextView=itemView.findViewById(R.id.team_slogan)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.layout_vote,parent,false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem=list[position]

        holder.layoutCard.setCardBackgroundColor(Color.parseColor(currentItem.teamColor))

        Glide.with(context).load(currentItem.teamLogo).into(holder.teamLogo)

        holder.teamName.text=currentItem.teamName

        holder.teamSlogan.text=currentItem.teamSlogan


        if (singleitem_selection_position==position){
            holder.itemView.setBackgroundColor(context.getColor(com.google.android.material.R.color.m3_dynamic_highlighted_text))
        }
        else{
            holder.itemView.setBackgroundColor(context.getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color))
        }

        holder.itemView.setOnClickListener {

            setSingleSelection(position)

        }



    }

    override fun getItemCount(): Int {

        return list.size

    }

    private fun setSingleSelection(adapterPosition:Int){

        if (adapterPosition==RecyclerView.NO_POSITION){
            return
        }

        else{
            notifyItemChanged(singleitem_selection_position)

            singleitem_selection_position=adapterPosition

            notifyItemChanged(singleitem_selection_position)

        }

    }



}