package com.jacpalberto.devcomms.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jacpalberto.devcomms.R
import com.jacpalberto.devcomms.data.Sponsor
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_sponsor.view.*

/**
 * Created by Alberto Carrillo on 8/28/18.
 */
class SponsorsAdapter(private var sponsors: List<Sponsor>,
                      private val onSponsorLongClick: (Sponsor) -> Boolean,
                      private val onSponsorClick: (Sponsor) -> Unit) : RecyclerView.Adapter<SponsorsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_sponsor, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = sponsors.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sponsors[position], onSponsorClick, onSponsorLongClick)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(sponsor: Sponsor,
                 onSponsorClick: (Sponsor) -> Unit,
                 onSponsorLongClick: (Sponsor) -> Boolean) = with(itemView) {
            if (sponsor.logo_url != null)
                Picasso.get()
                        .load(if (sponsor.logo_url.isNotEmpty()) sponsor.logo_url else "placeholder")
                        .error(R.drawable.logo_community)
                        .resize(500, 500)
                        .centerInside()
                        .into(this.sponsorImg)

            sponsorImg.setOnClickListener { onSponsorClick(sponsor) }
            sponsorImg.setOnLongClickListener { onSponsorLongClick(sponsor) }
        }
    }
}