package com.github.crafttogether.logger

data class Vector3(val x: Int, val y: Int, val z: Int) {
    val coords: String get() = "$x / $y / $z"
}
