package com.jacpalberto.devcomms.sponsors

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.jacpalberto.devcomms.data.DataState
import com.jacpalberto.devcomms.data.MainEventResponse
import com.jacpalberto.devcomms.data.Sponsor
import com.jacpalberto.devcomms.data.SponsorList

/**
 * Created by Alberto Carrillo on 9/15/18.
 */
//TODO: replace jdd-18 from string to BuildConfig variable
class SponsorRepository {
    private val db = FirebaseFirestore.getInstance()
    private val event = "jdd-18"
    private val eventRef = db.collection("events").document(event)
    private var sponsorList = mutableListOf<Sponsor>()
    private val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")

    fun fetchSponsors(onResult: (sponsors: SponsorList) -> Unit) {
        checkConnectivity(isConnected = { fetchFirestoreSponsors(onResult) },
                isNotConnected = { onResult(SponsorList(emptyList(), 401, DataState.ERROR)) })
    }

    private fun checkConnectivity(isConnected: () -> Unit, isNotConnected: () -> Unit) {
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) isConnected()
                else isNotConnected()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun fetchFirestoreSponsors(onResult: (sponsors: SponsorList) -> Unit) {
        sponsorList = mutableListOf()
        eventRef.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val eventResponse = it.result.toObject(MainEventResponse::class.java)
                val sponsors = eventResponse?.sponsors

                sponsors?.forEachIndexed { index, reference ->
                    reference.id?.get()?.addOnCompleteListener { sponsorResponse ->
                        if (sponsorResponse.isSuccessful) {
                            val sponsor = sponsorResponse.result.toObject(Sponsor::class.java)
                            if (sponsor != null) sponsorList.add(sponsor)
                            if (index == sponsors.size - 1) {
                                onResult(SponsorList(sponsorList, 0, DataState.SUCCESS))
                            }
                        }
                    }
                }
            } else {
                onResult(SponsorList(emptyList(), 400, DataState.ERROR))
            }
        }
    }
}