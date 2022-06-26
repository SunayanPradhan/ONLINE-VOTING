package com.sunayanpradhan.onlinevoting.Models

data class VoterInformation(var voterId:String,
                            var aadhaarId:String,
                            var phoneNo:String,
                            var userId:String
)
{
    constructor():this(
        "",
        "",
        "",
        ""
    )
}
