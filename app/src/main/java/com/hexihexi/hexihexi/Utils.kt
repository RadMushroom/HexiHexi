package com.hexihexi.hexihexi


class Utils {
    companion object {

        fun parseValue(value: String): Float{
            return value.replace("[a-zA-Z]".toRegex(), "").trim().toFloat()
        }

        fun parseTriple(value: String): List<Float> {
            return value.replace("[^0-9,;-]".toRegex(), "")
                    .replace(",", ".")
                    .split(";")
                    .map { it.toFloat() }
                    .toList()
        }
    }

}