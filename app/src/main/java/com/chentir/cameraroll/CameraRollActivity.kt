package com.chentir.cameraroll

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.chentir.cameraroll.data.local.PictureEntity
import com.chentir.cameraroll.databinding.ActivityCameraRollBinding
import com.chentir.cameraroll.imageloader.ImageLoader
import com.chentir.cameraroll.utils.Lce
import kotlinx.android.synthetic.main.activity_camera_roll.*
import kotlinx.android.synthetic.main.content_camera_roll.view.*

class CameraRollActivity : AppCompatActivity() {
    private lateinit var adapter: CameraRollAdapter
    private val viewModel: CameraRollViewModel by viewModels()

    private lateinit var binding: ActivityCameraRollBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraRollBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(toolbar)

        binding.root.picture_list.setHasFixedSize(true)

        viewModel.loadPicturesAction(
            store = DependencyProvider.provideStore(applicationContext, results = 500)
        )

        viewModel.liveData.observe(this,
            Observer<Lce<List<PictureEntity>>> { lce ->
                when (lce) {
                    is Lce.Loading -> {
                        progress.visibility = View.VISIBLE
                    }
                    is Lce.Success<List<PictureEntity>> -> {
                        adapter = CameraRollAdapter(lce.data, ImageLoader.init(applicationContext))
                        binding.root.picture_list.adapter = adapter
                        progress.visibility = View.GONE
                    }
                    is Lce.Error -> {
                        progress.visibility = View.GONE
                        Toast.makeText(this, "${lce.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }
}
