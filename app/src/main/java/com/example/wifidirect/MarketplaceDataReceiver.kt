import java.net.DatagramPacket
import java.net.DatagramSocket
import java.io.IOException

class MarketplaceDataReceiver(private val socket: DatagramSocket) : Runnable {

    override fun run() {
        val buffer = ByteArray(1024)
        val packet = DatagramPacket(buffer, buffer.size)

        while (true) {
            try {
                socket.receive(packet)
                val data = String(packet.data, 0, packet.length)

                // Parse the data and add the item to the marketplace
                // This will depend on how you're formatting the data
                // For example:
                // val item = parseData(data)
                // marketplaceItemView.addItem(item)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // Add a method to parse the data into a MarketplaceItem instance
    // This will depend on how you're formatting the data
    // For example:
    // private fun parseData(data: String): MarketplaceItem {
    //     val parts = data.split(",")
    //     return MarketplaceItem(parts[0], parts[1], parts[2].toDouble())
    // }
}