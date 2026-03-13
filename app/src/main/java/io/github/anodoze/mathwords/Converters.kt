package io.github.anodoze.mathwords

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromOperation(op: Operation): String = op.name

    @TypeConverter
    fun toOperation(value: String): Operation = Operation.valueOf(value)
}