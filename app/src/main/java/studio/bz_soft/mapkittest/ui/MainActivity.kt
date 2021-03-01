package studio.bz_soft.mapkittest.ui

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import coil.loadAny
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationManager
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationTapListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.threeten.bp.Instant
import studio.bz_soft.mapkittest.R
import studio.bz_soft.mapkittest.databinding.ActivityMainBinding
import studio.bz_soft.mapkittest.databinding.SearchViewBarBinding
import studio.bz_soft.mapkittest.root.extensions.*
import java.util.*

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity(), /*LocationListener,*/ UserLocationObjectListener,
    MapObjectCollectionListener, /*MapObjectTapListener,*/ InputListener,
    /*UserLocationTapListener,*/ CameraListener {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val viewModel by inject<MapKitVM>()

    private var userLocationLayer: UserLocationLayer? = null
    private var locationManager: LocationManager? = null
    private var routeStartLocation = Point(0.0, 0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
        setContentView(binding.root)

        supportActionBar?.hide()

        super.onCreate(savedInstanceState)

        binding.apply {
            SearchViewBarBinding.bind(root).searchView.apply {
                queryHint = getString(R.string.map_address_search_hint)
            }

            with(backButtonIV) {
                loadAny(R.drawable.ic_arrow_left)
                setOnClickListener {  }
            }
            with(targetMeIV) {
                loadAny(R.drawable.ic_target_me)
                setOnClickListener { movePosition(routeStartLocation) }
            }
            with(mapView) {
                map.isRotateGesturesEnabled = true
                map.logo.setAlignment(Alignment(HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM))
                map.mapObjects.addListener(this@MainActivity)
                map.addInputListener(this@MainActivity)
            }

            movePosition(routeStartLocation)

            userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapView.mapWindow)
            userLocationLayer?.let {
                it.isVisible = true
                it.isHeadingEnabled = true
                it.isAutoZoomEnabled = true
                it.cameraPosition()?.let { position ->
                    routeStartLocation = position.target
                }
                it.setObjectListener(this@MainActivity)
            }

            with(viewModel) {
                progress.observe(this@MainActivity) { progressBar.showProgressBar(it) }
                errors.observe(this@MainActivity) { showToast("DataBase Error!!!") }
            }
            lifecycleScope.launch {
                launch { viewModel.watchPoint.collect { renderPlaceMark(it) } }
                launch { viewModel.watchLastLocation.collect { renderLastLocation(it) } }
                launch { viewModel.watchPointDescription.collect { renderDescription(it) } }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        checkPermissions()
    }

    override fun onStop() {
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    binding.root.context.showToast(getString(R.string.fragment_route_error_location))
            }
        }
    }

//    override fun onLocationUpdated(location: Location) {
//        Log.d("MapKit", "Latitude => ${location.position.latitude}")
//        Log.d("MapKit", "Longitude => ${location.position.longitude}")
//        viewModel.onSetPoint(location.position)
//        movePosition(location.position)
//    }
//
//    override fun onLocationStatusUpdated(locationStatus: LocationStatus) {
//        binding.root.showToast(
//            when (locationStatus) {
//                LocationStatus.AVAILABLE -> "Location is available"
//                LocationStatus.NOT_AVAILABLE -> "Location is not available"
//            }
//        )
//        onSetAnchor()
//    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        Log.d("MapKit", "onObjectAdded()...")
        onSetAnchor()

        binding.mapView.apply {
            movePosition(Point(width.toDouble(), height.toDouble()))
        }

        with(userLocationView) {
            pin.setIcon(ImageProvider.fromBitmap(this@MainActivity.getBitmapFromVectorDrawable(R.drawable.ic_baseline_navigation_24)))
//            arrow.setIcon(ImageProvider.fromResource(binding.root.context, R.drawable.ic_user_arrow))
            accuracyCircle.fillColor = Color.CYAN
        }
    }

    override fun onObjectRemoved(userLocationView: UserLocationView) {
        Log.d("MapKit", "onObjectRemoved()...")
    }

    override fun onObjectUpdated(userLocationView: UserLocationView, objectEvent: ObjectEvent) {
        Log.d("MapKit", "onObjectUpdated()...")
        Log.d("MapKit", "objectEvent is valid ==> ${objectEvent.isValid}")
        Log.d("MapKit", "userLocationView  ==> ${userLocationView.arrow}")
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        isFinished: Boolean
    ) {
        Log.d("MapKit", "onCameraPositionChanged()...")
        Log.d("MapKit", "isFinished ==> $isFinished")
        if (isFinished) onResetAnchor()
    }

//    override fun onUserLocationObjectTap(point: Point) {
//        Log.d("MapKit", "onUserLocationObjectTap()...")
//        Log.d("MapKit", "point tapped lat ==> ${point.latitude}, long ==> ${point.longitude}")
//    }

    override fun onMapObjectAdded(mapObject: MapObject) {
        Log.d("MapKit", "onMapObjectAdded()...")
        Log.d("MapKit", "mapObject user data => ${mapObject.isValid}")
    }

    override fun onMapObjectRemoved(mapObject: MapObject) {
        Log.d("MapKit", "onMapObjectRemoved()...")
    }

    override fun onMapObjectUpdated(mapObject: MapObject) {
        Log.d("MapKit", "onMapObjectUpdated()...")
    }

//    override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
//        Log.d("MapKit", "onMapObjectTap()...")
//        Log.d("MapKit", "mapObject user data => ${mapObject.userData}")
//        Log.d("MapKit", "mapObject point lat => ${point.latitude} long => ${point.longitude}")
//        return mapObject.isValid
//    }

    override fun onMapTap(map: Map, point: Point) {
        Log.d("MapKit", "onMapTap()...")
        Log.d("MapKit", "map point set at lat => ${point.latitude} long => ${point.longitude}")
        viewModel.onSetPoint(point)
        viewModel.onSetState(PointState.AddPoint)
        viewModel.onStateChange(PointState.AddPoint)

        viewModel.getPointDescription()
    }

    override fun onMapLongTap(p0: Map, p1: Point) {
        Log.d("MapKit", "onMapLongTap()...")
    }

    private fun renderPlaceMark(point: Point) {
        onSetAnchor()

        movePosition(point)

        binding.mapView.map.mapObjects.addPlacemark(point, ImageProvider.fromBitmap(this@MainActivity.getBitmapFromVectorDrawable(R.drawable.ic_baseline_add_location_24)))

        viewModel.getPointDescription()
//        with(userLocationView) {
//            pin.setIcon(
//                com.yandex.runtime.image.ImageProvider.fromBitmap(this@MainActivity.getBitmapFromVectorDrawable(
//                    studio.bz_soft.mapkittest.R.drawable.ic_baseline_navigation_24)))
////            arrow.setIcon(ImageProvider.fromResource(binding.root.context, R.drawable.ic_user_arrow))
//            accuracyCircle.fillColor = android.graphics.Color.CYAN
//        }
    }

    private fun renderLastLocation(location: studio.bz_soft.mapkittest.data.models.db.Location?) {
        location?.let {
            Log.d("MapKit", "MapKit last location is lat ==> ${it.latitude}, lon ==> ${it.longitude}, uuid ==> ${it.uuid}")
        }
    }

    private fun renderDescription(description: String) {
        Log.d("MapKit", "Point reverse geo coding ==>")
        Log.d("MapKit", "Description ==> $description")
    }

    private fun onCameraUserPosition() {
        userLocationLayer?.cameraPosition()?.let {
            movePosition(it.target)
        } ?: movePosition(TARGET_LOCATION)
    }

    private fun movePosition(target: Point) {
        binding.mapView.map.move(
            CameraPosition(target, 14f, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }

    private fun onSetAnchor() {
        Log.d("MapKit", "onSetAnchor()...")
        binding.apply {
            userLocationLayer?.setAnchor(
                PointF((mapView.width * 0.5).toFloat(), (mapView.height * 0.5).toFloat()),
                PointF((mapView.width * 0.5).toFloat(), (mapView.height * 0.83).toFloat())
            )
        }
    }

    private fun onResetAnchor() {
        Log.d("MapKit", "onResetAnchor()...")
        userLocationLayer?.resetAnchor()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun checkPermissions() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.P ->
                if (this.checkSelfPermissionApi27()) requestPermissions()
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ->
                if (this.checkSelfPermissionApi28()) requestPermissions()
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (getPermissionStatusApi28(this))
                requestPermissions(permissionApi28(), REQUEST_CODE_API_28)
            else
                requestPermissions(permissionApi28(), REQUEST_CODE_API_28)
        } else {
            if (getPermissionStatusApi27(this))
                requestPermissions(permissionApi27(), REQUEST_CODE_API_27)
            else
                requestPermissions(permissionApi27(), REQUEST_CODE_API_27)
        }
    }

    companion object {
        private const val MAPKIT_API_KEY = "c82d93da-c5d6-4373-bd38-5a7a7ba7f7c5"
        private const val PERMISSIONS_REQUEST_CODE_LOCATION = 1
        private const val REQUEST_CODE_API_27 = 27
        private const val REQUEST_CODE_API_28 = 28
        private val TARGET_LOCATION = Point(47.1717749, 38.749748)
    }
}