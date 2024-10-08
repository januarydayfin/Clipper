package com.krayapp.buffercompanion.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "buffered")
data class StringEntity(
	@PrimaryKey var text: String,
	var position: Int? = 0
) {
	companion object {
		fun emptyEntity(): StringEntity {
			return StringEntity("", 0)
		}
	}

	override fun equals(other: Any?): Boolean {
		return text == (other as StringEntity).text
	}
}