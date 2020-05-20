package com.chentir.cameraroll

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chentir.cameraroll.data.local.PictureEntity
import com.chentir.cameraroll.utils.Lce
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class CameraRollViewModel : ViewModel() {
    val liveData = MutableLiveData<Lce<List<PictureEntity>>>()

    fun loadPicturesAction(store: Store<String, List<PictureEntity>>
    ) = viewModelScope.launch(Dispatchers.IO) {
        liveData.postValue(Lce.Loading)

        val flow = store.stream(StoreRequest.cached(key = SEED, refresh = true))

        flow.collect { response ->
            when (response) {
                is StoreResponse.Data -> {
                    liveData.postValue(Lce.Success(response.value))
                }
                is StoreResponse.Error -> {
                    liveData.postValue(Lce.Error(response.error))
                }
            }
        }
    }
}
