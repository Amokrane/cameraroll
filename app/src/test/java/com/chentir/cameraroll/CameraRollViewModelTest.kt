package com.chentir.cameraroll

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.chentir.cameraroll.data.local.PictureEntity
import com.chentir.cameraroll.utils.Lce
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CameraRollViewModelTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun testLoadPictures() {
        runBlocking {
            launch(Dispatchers.Main) {
                // Given
                val flow = flow<StoreResponse<List<PictureEntity>>> {
                        emit(StoreResponse.Data(MOCKED_PICTURES, ResponseOrigin.Fetcher))
                }

                val store = mock<Store<String, List<PictureEntity>>> {
                    on { stream(StoreRequest.cached(key = SEED, refresh = true)) } doReturn flow
                }

                // When
                val viewModel = CameraRollViewModel()
                viewModel.loadPicturesAction(store).join()

                // Then
                assertEquals(MOCKED_PICTURES, viewModel.liveData.value!!.data)
            }
        }
    }

    @Test
    fun testLoadPictures_WhenError() {
        runBlocking {
            launch(Dispatchers.Main) {
                // Given
                val flow = flow<StoreResponse<List<PictureEntity>>> {
                    emit(StoreResponse.Error(Throwable("error"), ResponseOrigin.Fetcher))
                }

                val store = mock<Store<String, List<PictureEntity>>> {
                    on { stream(StoreRequest.cached(key = SEED, refresh = true)) } doReturn flow
                }

                // When
                val viewModel = CameraRollViewModel()
                viewModel.loadPicturesAction(store).join()

                // Then
                assertNull(viewModel.liveData.value!!.data)
                assertEquals(Lce.Error("error"), viewModel.liveData.value)
            }
        }
    }
}
