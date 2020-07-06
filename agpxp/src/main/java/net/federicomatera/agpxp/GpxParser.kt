/*
 * GPXParser.java
 * 
 * Copyright (c) 2012, AlternativeVision. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package net.federicomatera.agpxp

import net.federicomatera.agpxp.models.*
import net.federicomatera.agpxp.types.FixType
import org.w3c.dom.Node
import org.xml.sax.SAXException
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

class GpxParser {

    private val dateFormat by lazy { SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss", Locale.US) }

    @Throws(
        ParserConfigurationException::class,
        SAXException::class,
        IOException::class
    )
    fun parse(stream: InputStream): Gpx {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = builder.parse(stream)

        var version: String? = null
        var creator: String? = null
        val wayPoints = mutableListOf<WayPoint>()
        val tracks = mutableListOf<Track>()
        val routes = mutableListOf<Route>()

        val firstChild = doc.firstChild
        if (firstChild == null || GPX_NODE != firstChild.nodeName) {
            throw IOException("Invalid GPX file")
        }

        val attrs = firstChild.attributes
        for (idx in 0 until attrs.length) {
            val attr = attrs.item(idx)
            when (attr.nodeName) {
                VERSION_ATTR -> version = attr.nodeValue
                CREATOR_ATTR -> creator = attr.nodeValue
            }
        }

        val nodes = firstChild.childNodes
        for (idx in 0 until nodes.length) {
            val currentNode = nodes.item(idx)
            when (currentNode.nodeName) {
                WPT_NODE -> wayPoints.add(parseWayPoint(currentNode))
                TRK_NODE -> tracks.add(parseTrack(currentNode))
                RTE_NODE -> routes.add(parseRoute(currentNode))
            }
        }

        return Gpx(
            creator = creator,
            version = version,
            wayPoints = wayPoints,
            tracks = tracks,
            routes = routes
        )
    }

    private fun parseTrack(node: Node): Track {
        val trackPoints = mutableListOf<WayPoint>()
        var name: String? = null
        var comment: String? = null
        var description: String? = null
        var src: String? = null
        var number: Int? = null
        var type: String? = null

        val nodes = node.childNodes
        if (nodes != null) {
            for (idx in 0 until nodes.length) {
                val currentNode = nodes.item(idx)
                when (currentNode.nodeName) {
                    NAME_NODE -> name = currentNode.firstChild.nodeValue
                    CMT_NODE -> comment = currentNode.firstChild.nodeValue
                    DESC_NODE -> description = currentNode.firstChild.nodeValue
                    SRC_NODE -> src = currentNode.firstChild.nodeValue
                    NUMBER_NODE -> number = currentNode.firstChild.nodeValue.toInt()
                    TYPE_NODE -> type = currentNode.firstChild.nodeValue
                    TRKSEG_NODE -> trackPoints.addAll(parseTrackSeg(currentNode))
                }
            }
        }
        return Track(
            trackPoints = trackPoints,
            name = name,
            comment = comment,
            description = description,
            src = src,
            number = number,
            type = type
        )
    }

    private fun parseRoute(node: Node): Route {
        val routePoints = mutableListOf<WayPoint>()
        var name: String? = null
        var comment: String? = null
        var description: String? = null
        var src: String? = null
        var number: Int? = null
        var type: String? = null

        val nodes = node.childNodes
        if (nodes != null) {
            for (idx in 0 until nodes.length) {
                val currentNode = nodes.item(idx)
                when (currentNode.nodeName) {
                    NAME_NODE -> name = currentNode.firstChild.nodeValue
                    CMT_NODE -> comment = currentNode.firstChild.nodeValue
                    DESC_NODE -> description = currentNode.firstChild.nodeValue
                    SRC_NODE -> src = currentNode.firstChild.nodeValue
                    NUMBER_NODE -> number = currentNode.firstChild.nodeValue.toInt()
                    TYPE_NODE -> type = currentNode.firstChild.nodeValue
                    RTEPT_NODE -> routePoints.add(parseWayPoint(currentNode))
                }
            }
        }
        return Route(
            routePoints = routePoints,
            name = name,
            comment = comment,
            description = description,
            src = src,
            number = number,
            type = type
        )
    }

    private fun parseTrackSeg(node: Node): List<WayPoint> {
        val trackPoints = mutableListOf<WayPoint>()
        val nodes = node.childNodes
        if (nodes != null) {
            for (idx in 0 until nodes.length) {
                val currentNode = nodes.item(idx)
                if (TRKPT_NODE == currentNode.nodeName) {
                    trackPoints.add(parseWayPoint(currentNode))
                }
            }
        }
        return trackPoints
    }

    private fun parseWayPoint(node: Node): WayPoint {
        var elevation: Double? = null
        var time: Date? = null
        var speed: Float? = null
        var magneticDeclination: Double? = null
        var geoIdHeight: Double? = null
        var name: String? = null
        var comment: String? = null
        var description: String? = null
        var src: String? = null
        var sym: String? = null
        var type: String? = null
        var fix: FixType? = null
        var sat: Int? = null
        var hDop: Double? = null
        var vDop: Double? = null
        var pDop: Double? = null
        var ageOfGPSData: Double? = null
        var dGpsId: Int? = null

        val childNodes = node.childNodes
        if (childNodes != null) {
            for (idx in 0 until childNodes.length) {
                val currentNode = childNodes.item(idx)
                when (currentNode.nodeName) {
                    ELE_NODE -> elevation = currentNode.firstChild.nodeValue.toDouble()
                    TIME_NODE -> time = dateFormat.parse(currentNode.firstChild.nodeValue)
                    NAME_NODE -> name = currentNode.firstChild.nodeValue
                    SPEED_NODE -> speed = currentNode.firstChild.nodeValue.toFloat()
                    CMT_NODE -> comment = currentNode.firstChild.nodeValue
                    DESC_NODE -> description = currentNode.firstChild.nodeValue
                    SRC_NODE -> src = currentNode.firstChild.nodeValue
                    MAGVAR_NODE -> magneticDeclination = currentNode.firstChild.nodeValue.toDouble()
                    GEOIDHEIGHT_NODE -> geoIdHeight = currentNode.firstChild.nodeValue.toDouble()
                    SYM_NODE -> sym = currentNode.firstChild.nodeValue
                    FIX_NODE -> fix =
                        FixType.values().first { it.value == node.firstChild.nodeValue }
                    TYPE_NODE -> type = currentNode.firstChild.nodeValue
                    SAT_NODE -> sat = currentNode.firstChild.nodeValue.toInt()
                    HDOP_NODE -> hDop = currentNode.firstChild.nodeValue.toDouble()
                    VDOP_NODE -> vDop = currentNode.firstChild.nodeValue.toDouble()
                    PDOP_NODE -> pDop = currentNode.firstChild.nodeValue.toDouble()
                    AGEOFGPSDATA_NODE -> ageOfGPSData = currentNode.firstChild.nodeValue.toDouble()
                    DGPSID_NODE -> dGpsId = currentNode.firstChild.nodeValue.toInt()
                }
            }
        }

        return WayPoint(
            latitude = node.attributes.getNamedItem(LAT_ATTR).nodeValue.toDouble(),
            longitude = node.attributes.getNamedItem(LON_ATTR).nodeValue.toDouble(),
            elevation = elevation,
            time = time,
            speed = speed,
            magneticDeclination = magneticDeclination,
            geoIdHeight = geoIdHeight,
            name = name,
            comment = comment,
            description = description,
            src = src,
            sym = sym,
            type = type,
            fix = fix,
            sat = sat,
            hDop = hDop,
            vDop = vDop,
            pDop = pDop,
            ageOfGPSData = ageOfGPSData,
            dGpsId = dGpsId
        )
    }
}
