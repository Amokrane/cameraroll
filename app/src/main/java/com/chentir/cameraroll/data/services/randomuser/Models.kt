package com.chentir.cameraroll.data.services.randomuser

data class Results(val results: List<User>)
data class User(val id: Id, val picture: Picture)
data class Picture(val large: String, val medium: String, val thumbnail: String)
data class Id(val name: String, val value: String)
