package com.example.wifidirect;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class DialogPopup {

    private Context context;
    private PopupWindow popupWindow;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText priceEditText;
    private Button addButton;

    public DialogPopup(Context context) {
        this.context = context;
        initView();
        initListeners();
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_popup, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleEditText = view.findViewById(R.id.title_edit_text);
        descriptionEditText = view.findViewById(R.id.description_edit_text);
        priceEditText = view.findViewById(R.id.price_edit_text);
        addButton = view.findViewById(R.id.add_button);
    }

    private void initListeners() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                double price = Double.parseDouble(priceEditText.getText().toString());

                MarketplaceItem item = new MarketplaceItem(title, description, price);
                addItemToMarketplace(item);

                popupWindow.dismiss();
            }
        });
    }

    public void show(View anchorView) {
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
    }

    public void addItemToMarketplace(MarketplaceItem item) {
        if (MainActivity.isGroupOwner) {
            // Add the item to the marketplace and relay the information to the peers
            MainActivity.marketplaceItemView.addItem(item);
            for (WifiP2pDevice peer : MainActivity.peers) {
                sendItemData(peer.deviceAddress, item);
            }
        } else {
            // Send the item information to the group owner
            sendItemData(MainActivity.groupOwnerAddress, item);
        }
    }

    private void sendItemData(String ipAddress, MarketplaceItem item) {
        try {
            byte[] data = item.toString().getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(ipAddress), MainActivity.socket.getLocalPort());
            MainActivity.socket.send(packet);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}