package br.santo.gymly.features.routines.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class Routine(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  val name: String,
  val dayOfWeek: String? = null
)

