package net.federicomatera.agpxp

import net.federicomatera.agpxp.models.*
import net.federicomatera.agpxp.types.FixType
import org.w3c.dom.Node
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class GpxWriter {

    private val dateFormat by lazy { SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss", Locale.US) }

    @Throws(
        ParserConfigurationException::class,
        TransformerException::class
    )
    fun write(gpx: Gpx, out: OutputStream?) {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = builder.newDocument()
        val gpxNode: Node = doc.createElement(GPX_NODE).apply {
            addBasicGPXInfoToNode(gpx)

            gpx.wayPoints?.forEach {
                addWayPointToGPXNode(it)
            }
            gpx.tracks?.forEach {
                addTrackToGPXNode(it)
            }
            gpx.routes?.forEach {
                addRouteToGPXNode(it)
            }
        }

        doc.appendChild(gpxNode)

        val transformer = TransformerFactory.newInstance().newTransformer()
        val source = DOMSource(doc)
        val result = StreamResult(out)
        transformer.transform(source, result)
    }

    private fun Node.addWayPointToGPXNode(wayPoint: WayPoint) {
        addGenericWayPointToGPXNode(WPT_NODE, wayPoint)
    }

    private fun Node.addGenericWayPointToGPXNode(tagName: String, wayPoint: WayPoint) {
        val child: Node = ownerDocument.createElement(tagName).apply {
            addAttribute(LAT_ATTR, wayPoint.latitude)
            addAttribute(LON_ATTR, wayPoint.longitude)
            appendChild(ELE_NODE, wayPoint.elevation)
            appendChild(TIME_NODE, wayPoint.time)
            appendChild(SPEED_NODE, wayPoint.speed)
            appendChild(MAGVAR_NODE, wayPoint.magneticDeclination)
            appendChild(GEOIDHEIGHT_NODE, wayPoint.geoIdHeight)
            appendChild(NAME_NODE, wayPoint.name)
            appendChild(CMT_NODE, wayPoint.comment)
            appendChild(SRC_NODE, wayPoint.src)
            appendChild(SYM_NODE, wayPoint.sym)
            appendChild(TYPE_NODE, wayPoint.type)
            appendChild(FIX_NODE, wayPoint.fix)
            appendChild(SAT_NODE, wayPoint.sat)
            appendChild(HDOP_NODE, wayPoint.hDop)
            appendChild(VDOP_NODE, wayPoint.vDop)
            appendChild(PDOP_NODE, wayPoint.pDop)
            appendChild(AGEOFGPSDATA_NODE, wayPoint.ageOfGPSData)
            appendChild(DGPSID_NODE, wayPoint.dGpsId)
        }

        appendChild(child)
    }

    private fun Node.addTrackToGPXNode(track: Track) {
        val child: Node = ownerDocument.createElement(TRK_NODE).apply {
            appendChild(NAME_NODE, track.name)
            appendChild(CMT_NODE, track.comment)
            appendChild(SRC_NODE, track.src)
            appendChild(NUMBER_NODE, track.number)
            appendChild(TYPE_NODE, track.type)
        }

        val trkSegNode: Node = ownerDocument.createElement(TRKSEG_NODE)
        track.trackPoints.forEach {
            trkSegNode.addGenericWayPointToGPXNode(TRKPT_NODE, it)
        }

        child.appendChild(trkSegNode)
        appendChild(child)
    }

    private fun Node.addRouteToGPXNode(route: Route) {
        val child: Node = ownerDocument.createElement(TRK_NODE).apply {
            appendChild(NAME_NODE, route.name)
            appendChild(CMT_NODE, route.comment)
            appendChild(DESC_NODE, route.description)
            appendChild(SRC_NODE, route.src)
            appendChild(NUMBER_NODE, route.number)
            appendChild(TYPE_NODE, route.type)
        }

        route.routePoints.forEach {
            addGenericWayPointToGPXNode(RTEPT_NODE, it)
        }
        appendChild(child)
    }

    private fun Node.addBasicGPXInfoToNode(gpx: Gpx) {
        addAttribute(VERSION_ATTR, gpx.version)
        addAttribute(CREATOR_ATTR, gpx.creator)
    }

    private fun Node.addAttribute(name: String, value: String?) {
        value
            ?.let { ownerDocument.createAttribute(name).apply { nodeValue = it } }
            ?.also { attributes.setNamedItem(it) }
    }

    private fun Node.addAttribute(name: String, value: Double?) {
        value
            ?.let { ownerDocument.createAttribute(name).apply { nodeValue = it.toString() } }
            ?.also { attributes.setNamedItem(it) }
    }

    private fun Node.appendChild(name: String, value: String?) {
        value
            ?.let {
                ownerDocument.createElement(name)
                    .apply { appendChild(ownerDocument.createTextNode(it)) }
            }
            ?.also { appendChild(it) }
    }

    private fun Node.appendChild(name: String, value: Int?) {
        value
            ?.let {
                ownerDocument.createElement(name)
                    .apply { appendChild(ownerDocument.createTextNode(it.toString())) }
            }
            ?.also { appendChild(it) }
    }

    private fun Node.appendChild(name: String, value: Float?) {
        value
            ?.let {
                ownerDocument.createElement(name)
                    .apply { appendChild(ownerDocument.createTextNode(it.toString())) }
            }
            ?.also { appendChild(it) }
    }

    private fun Node.appendChild(name: String, value: Double?) {
        value
            ?.let {
                ownerDocument.createElement(name)
                    .apply { appendChild(ownerDocument.createTextNode(it.toString())) }
            }
            ?.also { appendChild(it) }
    }

    private fun Node.appendChild(name: String, value: Date?) {
        value
            ?.let {
                ownerDocument.createElement(name)
                    .apply { appendChild(ownerDocument.createTextNode(dateFormat.format(it))) }
            }
            ?.also { appendChild(it) }
    }

    private fun Node.appendChild(name: String, value: FixType?) {
        value
            ?.let {
                ownerDocument.createElement(name)
                    .apply { appendChild(ownerDocument.createTextNode(it.value)) }
            }
            ?.also { appendChild(it) }
    }
}