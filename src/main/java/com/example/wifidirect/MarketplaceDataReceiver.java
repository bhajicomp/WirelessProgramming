import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.IOException;

public class MarketplaceDataReceiver implements Runnable {
    private DatagramSocket socket;

    public MarketplaceDataReceiver(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (true) {
            try {
                socket.receive(packet);
                String data = new String(packet.getData(), 0, packet.getLength());

                // Parse the data and add the item to the marketplace
                // This will depend on how you're formatting the data
                // For example:
                // MarketplaceItem item = parseData(data);
                // marketplaceItemView.addItem(item);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Add a method to parse the data into a MarketplaceItem instance
    // This will depend on how you're formatting the data
    // For example:
    // private MarketplaceItem parseData(String data) {
    //     String[] parts = data.split(",");
    //     return new MarketplaceItem(parts[0], parts[1], Double.parseDouble(parts[2]));
    // }
}