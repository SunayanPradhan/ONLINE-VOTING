package com.sunayanpradhan.onlinevoting.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.sunayanpradhan.onlinevoting.R

class VoteCategoryActivity : AppCompatActivity() {

    lateinit var electionsLayout:LinearLayout

    lateinit var scheduleLayout:LinearLayout

    lateinit var resultsLayout: LinearLayout

    lateinit var helplineLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote_category)

        electionsLayout=findViewById(R.id.elections_layout)

        electionsLayout.setOnClickListener {

            val intent=Intent(this,VoteListActivity::class.java)

            startActivity(intent)


        }

    }
}