package natour.dev.zonetechnologiestask.domain.model

data class Device(
    val name: String,
    val uniqueId: String,
    val category: String = "Android Phone"

)