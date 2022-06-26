package com.sunayanpradhan.onlinevoting.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunayanpradhan.onlinevoting.R
import com.sunayanpradhan.onlinevoting.Models.TeamInformation
import com.sunayanpradhan.onlinevoting.Adapters.VoteAdapter
import com.sunayanpradhan.onlinevoting.Models.VoteInformation

class VoteActivity : AppCompatActivity() {

    lateinit var voteLogo:ImageView
    lateinit var voteTitle:TextView
    lateinit var voteDuration:TextView
    lateinit var teamRecyclerView: RecyclerView

    lateinit var list:ArrayList<TeamInformation>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote)

        voteLogo=findViewById(R.id.vote_logo)
        voteTitle= findViewById(R.id.vote_title)
        voteDuration=findViewById(R.id.vote_duration)
        teamRecyclerView=findViewById(R.id.team_recyclerView)

        list= ArrayList()

        val adapter= VoteAdapter(list,this)

        val layoutManager=LinearLayoutManager(this)

        teamRecyclerView.layoutManager=layoutManager


        FirebaseDatabase.getInstance().reference.child("Votes").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for (datasnapShot in snapshot.children){

                    val data: VoteInformation?=datasnapShot.getValue(VoteInformation::class.java)

                    data?.voteId=datasnapShot.key.toString()

                    Glide.with(this@VoteActivity).load(data?.voteLogo).into(voteLogo)

                    voteTitle.text= data?.voteTitle

                    voteDuration.text= data?.voteStartTime.toString()+"-"+data?.voteEndTime.toString()



                }


            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(this@VoteActivity, error.message, Toast.LENGTH_SHORT).show()

            }
        })


        FirebaseDatabase.getInstance().reference.child("VoteTeams").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for (datasnapShot in snapshot.children){

                    val data: TeamInformation?=datasnapShot.getValue(TeamInformation::class.java)

                    data?.teamId=datasnapShot.key.toString()

                    list.add(data!!)
                }


                adapter.notifyDataSetChanged()

                teamRecyclerView.adapter=adapter


            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(this@VoteActivity, error.message, Toast.LENGTH_SHORT).show()

            }
        })



    }
}