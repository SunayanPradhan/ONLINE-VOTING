package com.sunayanpradhan.onlinevoting.Activities


import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunayanpradhan.onlinevoting.Models.VoterInformation
import com.sunayanpradhan.onlinevoting.R
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    lateinit var aadhaarNo: EditText
    lateinit var voterNo: EditText
    lateinit var phoneNo: EditText
    lateinit var verifyButton: Button
    lateinit var otpNo: EditText
    lateinit var getStarted: Button

    lateinit var auth: FirebaseAuth

    private var storedVerificationId = ""

    lateinit var resendToken:PhoneAuthProvider.ForceResendingToken

    private var isExist=false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        aadhaarNo = findViewById(R.id.aadhaar_no)
        voterNo = findViewById(R.id.voter_no)
        phoneNo = findViewById(R.id.phone_no)
        verifyButton = findViewById(R.id.verify_button)
        otpNo = findViewById(R.id.otp_no)
        getStarted = findViewById(R.id.get_started)

        auth = FirebaseAuth.getInstance()

        FirebaseApp.initializeApp(this)



        verifyButton.setOnClickListener {

            if (phoneNo.text.startsWith("+91")){
                if (phoneNo.length()==13) {
                    startPhoneNumberVerification(phoneNo.text.toString())




                }
                else{
                    Toast.makeText(this, "Enter phone number correctly", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                if (phoneNo.length()==10) {
                    startPhoneNumberVerification("+91${phoneNo.text}")



                }
                else{
                    Toast.makeText(this, "Enter phone number correctly", Toast.LENGTH_SHORT).show()
                }
            }


        }

        FirebaseDatabase.getInstance().reference.child("Voters").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for (dataSnapshot in snapshot.children) {

                    var user = dataSnapshot.getValue(VoterInformation::class.java)

                    user?.userId = dataSnapshot.key.toString()

                    if (aadhaarNo.text.toString()==user?.aadhaarId||
                        voterNo.text.toString()==user?.voterId){

                        isExist=true

                    }

                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })



        getStarted.setOnClickListener {

            if (aadhaarNo.text.toString().trim().isEmpty() ||
                voterNo.text.toString().trim().isEmpty() ||
                phoneNo.text.toString().trim().isEmpty() ||
                otpNo.text.toString().trim().isEmpty())
            {

                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()

            }

            else if (aadhaarNo.length()!=12){

                Toast.makeText(this, "Enter your Aadhaar no correctly", Toast.LENGTH_SHORT).show()

            }

            else {


                if (isExist==true){

                    Toast.makeText(this@MainActivity, "This account is already exists", Toast.LENGTH_SHORT).show()

                }

                else {
                    verifyPhoneNumberWithCode(storedVerificationId, otpNo.text.toString())


                }
            }



        }


    }

    private fun verifyPhoneNumberWithCode(verificationId: String, code: String) {

        val credential = PhoneAuthProvider.getCredential(verificationId, code)


        signInWithPhoneAuthCredential(credential)

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user

                    if (task.result?.additionalUserInfo?.isNewUser!!){

                        sendDataToDatabase()

                    }

                    else{


                        FirebaseDatabase.getInstance().reference.child("Voters").child(FirebaseAuth.getInstance().uid.toString()).addValueEventListener(object :ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {

                                val data:VoterInformation?=snapshot.getValue(VoterInformation::class.java)

                                data?.userId=snapshot.key.toString()

                                if (aadhaarNo.text.toString().trim()==data?.aadhaarId &&
                                    voterNo.text.toString().trim()==data.voterId &&
                                    phoneNo.text.toString().trim()==data.phoneNo){


                                    val intent=Intent(this@MainActivity,VoteCategoryActivity::class.java)

                                    startActivity(intent)


                                }

                                else{

                                    Toast.makeText(this@MainActivity, "Enter all data correctly", Toast.LENGTH_SHORT).show()

                                    FirebaseAuth.getInstance().signOut()


                                }


                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
                            }

                        })


                    }



                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }






    }

    private fun startPhoneNumberVerification(phoneNumber: String) {

        verifyButton.isEnabled=false

        verifyButton.setBackgroundColor(Color.GRAY)

        Toast.makeText(this, "OTP Send", Toast.LENGTH_SHORT).show()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)


        Handler().postDelayed({
            verifyButton.isEnabled=true

            verifyButton.setBackgroundColor(Color.BLACK)

            }, 60000)


    }


    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
        }
    }


    private fun sendDataToDatabase(){

        var firebaseUser: FirebaseUser? = auth.currentUser

        var user = VoterInformation(aadhaarNo.text.toString(),
            voterNo.text.toString(),
            phoneNo.text.toString(),
            firebaseUser?.uid.toString())

        if (firebaseUser != null) {
            FirebaseDatabase.getInstance().reference.child("Voters").child(firebaseUser.uid)
                .setValue(user).addOnSuccessListener {

                    val intent=Intent(this,VoteCategoryActivity::class.java)

                    startActivity(intent)

                }

        }


    }



    override fun onStart() {
        super.onStart()

        if (auth.currentUser!=null){

            val intent=Intent(this,VoteCategoryActivity::class.java)

            startActivity(intent)



        }

    }




}