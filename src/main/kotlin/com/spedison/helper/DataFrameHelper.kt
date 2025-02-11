package com.spedison.helper

import com.spedison.model.ColumnDatabase
import krangl.DataFrame
import krangl.DataFrameRow
import krangl.fromResultSet
import java.sql.Connection
import java.sql.Date
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoField


object DataFrameHelper {

    val timeZoneConfig = ListTimeZone.getTimeZoneId()

    fun createDataFrameFromQuery(conn: Connection, sql: String, verbose: Boolean = false): DataFrame {
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery(sql)

        if (verbose)
            println("Query executed")

        val ret = DataFrame.fromResultSet(rs)

        if (verbose)
            println("Data is load to Dataframe")

        return ret
    }

    fun readInt(line: Int, columnsDatabase: ColumnDatabase, data: DataFrame): Int {
        val columName: String = columnsDatabase.nameDatabase
        val row: DataFrameRow = data.row(line)
        val ret: Any? = row.get(columName)
        if (ret is Long)
            return ret.toInt()
        if (ret is Int)
            return ret
        throw RuntimeException("Type Mismath")
    }

    fun readLong(line: Int, columnsDatabase: ColumnDatabase, data: DataFrame): Long {
        val columName: String = columnsDatabase.nameDatabase
        val row: DataFrameRow = data.row(line)
        val ret: Any? = row.get(columName)

        if (ret is Long)
            return ret

        if (ret is Int)
            return ret.toLong()

        throw RuntimeException("Type Mismath")
    }

    fun readDouble(line: Int, columnsDatabase: ColumnDatabase, data: DataFrame): Double {
        val columName: String = columnsDatabase.nameDatabase
        val row: DataFrameRow = data.row(line)
        val ret: Any? = row.get(columName)

        if (ret is Double)
            return ret
        if (ret is Float)
            return ret.toDouble()
        throw RuntimeException("Type Mismath")
    }

    fun readFloat(line: Int, columnsDatabase: ColumnDatabase, data: DataFrame): Float {
        val columName: String = columnsDatabase.nameDatabase
        val row: DataFrameRow = data.row(line)
        val ret: Any? = row.get(columName)

        if (ret is Float)
            return ret
        if (ret is Double)
            return ret.toFloat()

        throw RuntimeException("Type Mismath")
    }

    fun readString(line: Int, columnsDatabase: ColumnDatabase, data: DataFrame): String {
        val columName: String = columnsDatabase.nameDatabase
        val row: DataFrameRow = data.row(line)
        return row.get(columName) as String
    }

    fun isNull(line: Int, columnsDatabase: ColumnDatabase, data: DataFrame): Boolean {
        val columName: String = columnsDatabase.nameDatabase
        val row: DataFrameRow = data.row(line)
        return (row.get(columName) == null)
    }

    @SuppressWarnings
    fun readDate(line: Int, columnsDatabase: ColumnDatabase, data: DataFrame): java.util.Date {
        val columName: String = columnsDatabase.nameDatabase
        val row: DataFrameRow = data.row(line)
        val value = row.get(columName)

        // Second precision.
        if (value is LocalDateTime)
            return Date(
                value.atZone(this.timeZoneConfig.toZoneId()).toEpochSecond() * 1000L
            )

        if (value is LocalDate)
            return Date(
                value.get(ChronoField.YEAR)-1900,
                value.get(ChronoField.MONTH_OF_YEAR)-1,
                value.get(ChronoField.DAY_OF_MONTH))


        if (value is LocalTime) {
            val ret = LocalDateTime.of(LocalDate.now(), value)
            return Date(
                ret.atZone(this.timeZoneConfig.toZoneId()).toEpochSecond() * 1000L
            )
        }

        throw Exception("Column ${columnsDatabase.nameDatabase} is not defined as Date, check it.")
    }

}