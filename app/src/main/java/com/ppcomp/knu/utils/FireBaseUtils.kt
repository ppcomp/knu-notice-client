package com.ppcomp.knu.utils

import com.google.firebase.database.*
import com.ppcomp.knu.fragment.NoticeFragment


class FireBaseUtils {

    fun loadData(myCallback: (result: String?) -> Unit) {
        try {
            // 내용
            var serverUrl = ""
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val myRef: DatabaseReference = database.getReference("url").child("server")
            //Read from the database
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    serverUrl = dataSnapshot.getValue() as String
                    myCallback.invoke(serverUrl)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                }
            })

        } catch (e: Throwable) {
        }
    }
}


//
//    fun loadData(onSuccess: (String) -> Unit, onError: (Throwable) -> Unit){
//        try {
//            // 내용
//            var urlValue: String = ""
//            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
//            val myRef: DatabaseReference = database.getReference("url").child("server")
//            //Read from the database
//            myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                urlValue = dataSnapshot.getValue() as String
//
//
//            }
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//            }
//        })
//            onSuccess(urlValue)
//        } catch(e: Throwable) {
//            onError(e)
//        }
//    }
//}


//    interface Callback{
//        fun success(data: String)
//        fun fail(errorMessage: String)
//    }
//
//    fun loadData(callback: Callback){
//        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
//        val myRef: DatabaseReference = database.getReference("url").child("server")
//
//        // Read from the database
//        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                callback.success(snapshot.getValue().toString())
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                callback.fail("asd Error")
//            }
//        })
//    }
//}

//
//    fun callUrl(): String {
//
//
//        var urlValue: String = ""
//        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
//        val myRef: DatabaseReference = database.getReference("url").child("server")
//        //Read from the database
//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                urlValue = dataSnapshot.getValue() as String
//
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//            }
//        })
//
//        return urlValue
//    }
//}







