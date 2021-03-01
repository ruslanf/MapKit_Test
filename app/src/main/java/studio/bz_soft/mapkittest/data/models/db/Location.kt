package studio.bz_soft.mapkittest.data.models.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import studio.bz_soft.mapkittest.root.extensions.generateUUID
import java.util.*

@Entity(tableName = "location")
data class Location(
    @PrimaryKey val uuid: UUID = generateUUID(),
    @ColumnInfo(name = "latitude") val longitude: Double,
    @ColumnInfo(name = "longitude") val latitude: Double,
    @ColumnInfo(name = "date_added") val dateAdded: Long,
    @ColumnInfo(name = "description") val description: String?
)