package net.federicomatera.agpxp.models

import java.io.Serializable

data class Gpx(
    val version: String? = null,
    val creator: String? = null,
    val tracks: List<Track>? = null,
    val routes: List<Route>? = null,
    val wayPoints: List<WayPoint>? = null
): Serializable
