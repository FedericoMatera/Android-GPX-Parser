package net.federicomatera.agpxp.types

enum class FixType constructor(val value: String) {
    NONE("none"),
    TWO_D("2d"),
    THREE_D("3d"),
    D_GPS("dgps"),
    PPS("pps")
}
