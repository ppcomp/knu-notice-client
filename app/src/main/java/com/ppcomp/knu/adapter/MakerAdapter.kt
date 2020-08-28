package com.ppcomp.knu.activity


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ppcomp.knu.`object`.MakerData
import com.ppcomp.knu.R
import com.google.android.gms.ads.AdView

/**
 * The [RecyclerViewAdapter] class.
 *
 * The adapter provides access to the items in the [MenuItemViewHolder]
 * or the [AdViewHolder].
 */
internal class MakerAdapter
/**
 * For this example app, the recyclerViewItems list contains only
 * [MenuItem] and [AdView] types.
 */(
    // An Activity's Context.
    private val context: Context,
    // The list of banner ads and menu items.
    private val makerList: List<Any>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * The [MenuItemViewHolder] class.
     * Provides a reference to each view in the menu item view.
     */
    inner class MenuItemViewHolder internal constructor(view: View) :
        RecyclerView.ViewHolder(view) {
        val dateName : TextView
        val buttonMail : ImageButton
        val dataEmail : TextView
        val dataGit : TextView
        val dataAff : TextView

        init {
            dateName = view.findViewById(R.id.maker_name)
            buttonMail = view.findViewById(R.id.button_mail)
            dataEmail = view.findViewById(R.id.maker_email)
            dataGit = view.findViewById(R.id.maker_git)
            dataAff = view.findViewById(R.id.maker_aff)
        }
    }

    /**
     * The [AdViewHolder] class.
     */
    inner class AdViewHolder internal constructor(view: View?) :
        RecyclerView.ViewHolder(view!!)

    override fun getItemCount(): Int {
        return makerList.size
    }

    /**
     * Determines the view type for the given position.
     */
    override fun getItemViewType(position: Int): Int {
        return if (position % MakerActivity().ITEMS_PER_AD == 0) BANNER_AD_VIEW_TYPE else DATA_ITEM_VIEW_TYPE
    }

    /**
     * Creates a new view for a menu item view or a banner ad view
     * based on the viewType. This method is invoked by the layout manager.
     */
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            DATA_ITEM_VIEW_TYPE -> {
                val menuItemLayoutView: View =
                    LayoutInflater.from(viewGroup.context).inflate(
                        R.layout.activity_maker_item, viewGroup, false
                    )
                MenuItemViewHolder(
                    menuItemLayoutView
                )
            }
            BANNER_AD_VIEW_TYPE -> {
                val bannerLayoutView: View = LayoutInflater.from(
                    viewGroup.context
                ).inflate(
                    R.layout.admob,
                    viewGroup, false
                )
                AdViewHolder(
                    bannerLayoutView
                )
            }
            else -> {
                val bannerLayoutView: View = LayoutInflater.from(
                    viewGroup.context
                ).inflate(
                    R.layout.admob,
                    viewGroup, false
                )
                AdViewHolder(
                    bannerLayoutView
                )
            }
        }
    }

    /**
     * Replaces the content in the views that make up the menu item view and the
     * banner ad view. This method is invoked by the layout manager.
     */
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val viewType = getItemViewType(position)
        when (viewType) {
            DATA_ITEM_VIEW_TYPE -> {
                val MakerItemHolder =
                    holder as MenuItemViewHolder
                val makerItem: MakerData = makerList[position] as MakerData

                // Add the menu item details to the menu item view.
                MakerItemHolder.dateName.text = makerItem.makerName
                MakerItemHolder.dataEmail.text = makerItem.makerEmail
                MakerItemHolder.dataGit.text = makerItem.makerGit
                MakerItemHolder.dataAff.text = makerItem.makerAff
            }
            BANNER_AD_VIEW_TYPE -> {
                val bannerHolder =
                    holder as AdViewHolder
                val adView = makerList[position] as AdView
                val adCardView = bannerHolder.itemView as ViewGroup
                // The AdViewHolder recycled by the RecyclerView may be a different
                // instance than the one used previously for this position. Clear the
                // AdViewHolder of any subviews in case it has a different
                // AdView associated with it, and make sure the AdView for this position doesn't
                // already have a parent of a different recycled AdViewHolder.
                if (adCardView.childCount > 0) {
                    adCardView.removeAllViews()
                }
                if (adView.parent != null) {
                    (adView.parent as ViewGroup).removeView(adView)
                }

                // Add the banner ad to the ad view.
                adCardView.addView(adView)
            }
            else -> {
                val bannerHolder =
                    holder as AdViewHolder
                val adView = makerList[position] as AdView
                val adCardView = bannerHolder.itemView as ViewGroup
                if (adCardView.childCount > 0) {
                    adCardView.removeAllViews()
                }
                if (adView.parent != null) {
                    (adView.parent as ViewGroup).removeView(adView)
                }
                adCardView.addView(adView)
            }
        }
    }

    companion object {
        // A menu item view type.
        private const val DATA_ITEM_VIEW_TYPE = 0

        // The banner ad view type.
        private const val BANNER_AD_VIEW_TYPE = 1
    }

}