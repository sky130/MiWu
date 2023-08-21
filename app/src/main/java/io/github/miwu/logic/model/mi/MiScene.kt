package io.github.sky130.miwu.logic.model.mi

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(indices = [Index(value = ["sceneId"], unique = true)])
data class MiScene(
    @SerializedName("scene_id") val sceneId: String,
    @SerializedName("scene_name") val sceneName: String,
    val icon: String,
    var homeId: String = "",
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
