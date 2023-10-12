import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.view.View
import android.widget.Button
import android.widget.EditText

class WiFiDirectBroadcastReceiver(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val activity: MainActivity
) : BroadcastReceiver() {

    // Form fields
    private lateinit var userNameField: EditText
    private lateinit var itemQuantityField: EditText
    private lateinit var itemNameField: EditText
    private lateinit var saveButton: Button

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Check if Wi-Fi Direct is enabled or disabled
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // Wi-Fi Direct is enabled
                    activity.setIsWifiP2pEnabled(true)
                } else {
                    // Wi-Fi Direct is disabled
                    activity.setIsWifiP2pEnabled(false)
                    activity.resetData()
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                // Request the current list of peers
                manager.requestPeers(channel, activity)
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                // Check if the device is connected or disconnected from a WiFi Direct Group
                val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                if (networkInfo.isConnected) {
                    // The device is connected to a WiFi Direct Group
                    activity.setConnected(true)
                } else {
                    // The device is disconnected from a WiFi Direct Group
                    activity.setConnected(false)
                    activity.resetData()
                }
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                // Update the local device's information
                val device = intent.getParcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                activity.updateLocalDevice(device)
            }
        }
    }

    // Rest of the code...
}