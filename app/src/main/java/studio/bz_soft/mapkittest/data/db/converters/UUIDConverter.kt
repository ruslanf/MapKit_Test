package studio.bz_soft.mapkittest.data.db.converters

import androidx.room.TypeConverter
import java.util.*

class UUIDConverter {
    @TypeConverter
    fun fromString(value: String?): UUID? = UUID.fromString(value)

    @TypeConverter
    fun fromUUID(uuid: UUID?): String = uuid.toString()
}