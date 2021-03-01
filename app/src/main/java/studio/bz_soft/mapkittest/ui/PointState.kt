package studio.bz_soft.mapkittest.ui

sealed class PointState {
    object NeutralState: PointState()
    object AddPoint: PointState()
    object DeletePoint: PointState()
    object PointAdded: PointState()
    object PointDeleted: PointState()
}
