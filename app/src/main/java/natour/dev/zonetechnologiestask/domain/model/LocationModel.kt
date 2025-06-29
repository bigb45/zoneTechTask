package natour.dev.zonetechnologiestask.domain.model

import java.sql.Timestamp

data class LocationModel(
    val id: String,
    val timestamp: String,
    val lon: String,
    val lat: String,
)
