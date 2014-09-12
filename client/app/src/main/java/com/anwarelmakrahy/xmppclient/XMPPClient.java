package com.anwarelmakrahy.xmppclient;

import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class XMPPClient {

    private String server;
    private int port;

    private ConnectionConfiguration config;
    private XMPPConnection connection;

    public XMPPClient(String server, int port) {
        this.server = server;
        this.port = port;
    }

    public void init() throws XMPPException {

        Log.d("XMPP Client", "Initializing connection to server: " + server + " port: " + port);

        //config = new ConnectionConfiguration(server, port);
        //config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        //connection = new XMPPTCPConnection(config);

        connection = new XMPPTCPConnection(server);
        try {
            connection.connect();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("XMPP Client", "Connected: " + connection.isConnected());
    }

    public void authenticate(String username, String password) throws XMPPException {
        if (connection!=null && connection.isConnected()) {
            try {
                connection.login(username, password);
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setStatus(boolean available, String status) {

        Presence.Type type = available? Presence.Type.available: Presence.Type.unavailable;
        Presence presence = new Presence(type);

        presence.setStatus(status);
        try {
            connection.sendPacket(presence);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

    }

    public void destroy() {
        if (connection!=null && connection.isConnected()) {
            try {
                connection.disconnect();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    public void createEntry(String user, String name) throws Exception {
        Log.d("XMPP Client", "Creating entry for server: "+ "user with name: " + name);
        Roster roster = connection.getRoster();
        roster.createEntry(user, name, null);
    }

    public Map<String, RosterPacket.ItemStatus> getServers() throws Exception {
        Map<String, RosterPacket.ItemStatus> result = new HashMap<String, RosterPacket.ItemStatus>();
        Roster roster = connection.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();
        for (RosterEntry entry : entries) {
            result.put(entry.getUser(), entry.getStatus());
            Log.d("XMPP Client", "Online: " + entry.getName());
        }

        return result;
    }
}
