package com.example.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener, WifiP2pManager.PeerListListener {

    private WifiP2pManager manager;
    private Channel channel;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<WifiP2pDevice> peers = new ArrayList<>();
    private boolean isGroupOwner = false;
    private DatagramSocket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                connectToPeer(i);
            }
        });

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void discoverPeers(View view) {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Discovery started", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(MainActivity.this, "Discovery failed: " + reasonCode, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        List<String> deviceNames = new ArrayList<>();
        for (WifiP2pDevice device : peers) {
            deviceNames.add(device.deviceName);
        }
        adapter.clear();
        adapter.addAll(deviceNames);
        adapter.notifyDataSetChanged();
    }

    private void connectToPeer(int position) {
        WifiP2pDevice device = peers.get(position);
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Connection successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "Connection failed: " + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        isGroupOwner = info.isGroupOwner;
        if (isGroupOwner) {
            try {
                socket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }

    public void addItemToMarketplace(MarketplaceItem item) {
        if (isGroupOwner) {
            marketplaceItemView.addItem(item);
            for (WifiP2pDevice peer : peers) {
                sendItemData(peer.deviceAddress, item);
            }
        } else {
            sendItemData(groupOwnerAddress, item);
        }
    }

    private void sendItemData(String ipAddress, MarketplaceItem item) {
        try {
            byte[] data = item.toString().getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(ipAddress), socket.getLocalPort());
            socket.send(packet);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}