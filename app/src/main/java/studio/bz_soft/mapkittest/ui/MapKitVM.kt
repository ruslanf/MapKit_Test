package studio.bz_soft.mapkittest.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import studio.bz_soft.mapkittest.data.models.db.Location
import studio.bz_soft.mapkittest.data.repository.DataBase
import studio.bz_soft.mapkittest.data.repository.Network
import studio.bz_soft.mapkittest.root.Left
import studio.bz_soft.mapkittest.root.Right
import studio.bz_soft.mapkittest.root.extensions.getCurrentDateInMills

@ExperimentalCoroutinesApi
class MapKitVM(
    private val dbRepository: DataBase,
    private val networkRepository: Network
) : ViewModel() {

    val progress = MutableLiveData<Int>()
    val errors = MutableLiveData<Exception>()

    val watchPoint: StateFlow<Point>
        get() = _point
    val watchLastLocation: StateFlow<Location?>
        get() = _lastLocation
    val watchPointState: StateFlow<PointState>
        get() = _pointState
    val watchPointDescription: StateFlow<String>
        get() = _pointDescription

    private val _point = MutableStateFlow(START_LOCATION)
    private val _lastLocation = MutableStateFlow<Location?>(null)
    private val _pointState = MutableStateFlow<PointState>(PointState.NeutralState)
    private val _pointDescription = MutableStateFlow<String>("")

    private var counter: Int = 0

    init {
        viewModelScope.launch {
            launch { dbRepository.watchLastLocation().collect { _lastLocation.value = it } }
        }
    }

    fun onSetPoint(point: Point) {
        _point.value = point
    }

    fun onSetState(state: PointState) {
        _pointState.value = state
    }

    fun onStateChange(state: PointState) {
        when (state) {
            PointState.AddPoint -> insertLocation(prepareLocation())
            PointState.DeletePoint -> TODO()
            PointState.PointAdded -> TODO()
            PointState.PointDeleted -> TODO()
            else -> Unit
        }
    }

    fun fetchLastLocation() {
        viewModelScope.launch {
            counter += 1
            progress.postValue(counter)

            when (val r = dbRepository.getLastLocation()) {
                is Left -> errors.postValue(r.value)
                is Right -> _lastLocation.value = r.value
            }

            counter -= 1
            progress.postValue(counter)
        }
    }

    fun getPointDescription() {
        Log.d("MapKit ==> ", "ViewModel getPointDescription()...")
        Log.d("MapKit ==> ", "ViewModel point lat => ${watchPoint.value.latitude}, lon => ${watchPoint.value.longitude}")
        viewModelScope.launch {
            counter += 1
            progress.postValue(counter)

            when (val r = networkRepository.getTest()
            ) {
                is Left -> errors.postValue(r.value)
                is Right -> _pointDescription.value = r.value
            }
//            when (val r = networkRepository.getPointDescription(
//                watchPoint.value.latitude, watchPoint.value.longitude)
//            ) {
//                is Left -> errors.postValue(r.value)
//                is Right -> _pointDescription.value = r.value
//            }

            counter -= 1
            progress.postValue(counter)
        }
    }

    private fun insertLocation(location: Location) {
        viewModelScope.launch {
            counter += 1
            progress.postValue(counter)

            when (val r = dbRepository.insertLocation(location)) {
                is Left -> errors.postValue(r.value)
                is Right -> _pointState.value = PointState.PointAdded
            }

            counter -= 1
            progress.postValue(counter)
        }
    }

    private fun prepareLocation(): Location =
        Location(
            latitude = watchPoint.value.latitude,
            longitude = watchPoint.value.longitude,
            dateAdded = getCurrentDateInMills(),
            description = null
        )

    companion object {
        private val START_LOCATION = Point(47.1717749, 38.749748)
    }
}