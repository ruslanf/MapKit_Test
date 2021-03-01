package studio.bz_soft.mapkittest.root.extensions

import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import java.util.*

fun generateUUID(): UUID = UUID.randomUUID()

fun getCurrentDate(): LocalDate = LocalDate.now(ZoneId.systemDefault())
fun getCurrentDateInMills(): Long = Instant.now().epochSecond