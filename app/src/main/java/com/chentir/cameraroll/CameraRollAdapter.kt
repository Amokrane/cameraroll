package com.chentir.cameraroll

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.chentir.cameraroll.data.local.PictureEntity
import com.chentir.cameraroll.imageloader.ImageLoader
import kotlinx.android.synthetic.main.picture_item.view.*
import timber.log.Timber

class CameraRollAdapter(
    private val pictures: List<PictureEntity>,
    private val imageLoader: ImageLoader
) :
    RecyclerView.Adapter<CameraRollAdapter.CameraRollViewHolder>() {

    class CameraRollViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.picture_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CameraRollViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.picture_item, parent, false)

        return CameraRollViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return pictures.size
    }

    override fun onViewRecycled(holder: CameraRollViewHolder) {
        super.onViewRecycled(holder)
        Timber.d("CameraRollAdapter#onViewRecycled: ${holder.imageView}")
        imageLoader.cancel(holder.imageView)
    }

    override fun onBindViewHolder(holder: CameraRollViewHolder, position: Int) {
        val pictureImageView = holder.imageView
        imageLoader.cancel(holder.imageView)
        imageLoader.load(url = pictures[position].large, imageView = pictureImageView)
    }
}