import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Date

fun decode(data: ByteArray) {
    val buffer = ByteBuffer.wrap(data)
    buffer.order(ByteOrder.BIG_ENDIAN)

    // Skip data length
    buffer.position(8)

    // Read Codec ID
    val codecId = buffer.get()
    if (codecId != 0x08.toByte()) {
        println("Unsupported codec version")
        return
    }

    // Number of Data 1
    val numberOfData1 = buffer.get().toInt() and 0xFF

    for (i in 0..<numberOfData1) {
        // Parse each AVL data block
        parseAVLData(buffer)
    }

    // CRC (optional)
}

fun parseAVLData(buffer: ByteBuffer) {
    // Timestamp
    val timestamp = buffer.long
    val gpsTimestamp = Date(timestamp)

    // Priority
    val priority = buffer.get()

    // GPS Element
    val longitude = buffer.int * 0.0000001
    val latitude = buffer.int * 0.0000001
    val altitude = buffer.short.toDouble()
    val angle = buffer.short.toDouble()
    val satellites = buffer.get().toInt()
    val speed = buffer.short.toDouble()

    // IO Event
    val eventID = buffer.get().toInt()
    val currentPosition = buffer.position()

    buffer.position(currentPosition + 7)

    println("Timestamp: $gpsTimestamp")
    println("Latitude: $latitude, Longitude: $longitude")
    println("Altitude: $altitude, Angle: $angle")
    println("Satellites: $satellites, Speed: $speed")
    println("Event ID: $eventID")
}

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    val hexString =
        "000000000000004308020000016B40D57B480100000000000000000000000000000001010101000000000000016B40D5C198010000000000000000000000000000000101010101000000020000252C"
    val exampleData = hexString.hexToByteArray()
    decode(exampleData)
}