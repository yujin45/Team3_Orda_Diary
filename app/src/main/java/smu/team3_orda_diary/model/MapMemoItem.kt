package smu.team3_orda_diary.model

data class MapMemoItem(
    val id: Int,
    val title: String,
    val content: String,
    val latitude: Double,
    val longitude: Double
)
