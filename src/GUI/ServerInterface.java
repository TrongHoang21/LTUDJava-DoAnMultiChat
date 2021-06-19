package GUI;


import multiChat.Client;
import multiChat.PartnerInfo;
import multiChat.Server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ServerInterface extends JFrame{
    private JList listOnlineList;
    private JLabel labelList;
    private JButton btnSendListToAll;
    private JLabel labelThisServer;
    private JButton btnDisable;
    private JButton btnEnable;
    private JButton btnCreatePrivateRoom;
    private JPanel panel1;
    GUI.ClientPrivateRoom roomGUI;
    private static String nameDest = "server";
    private String nameSrc;
    ArrayList<String> currentOnlineList;
    private ArrayList<PartnerInfo> listPartner;
    private ArrayList<String> listChatLog;

    //NON DATA
    Thread readMessage;
    final static int ServerPort = 1234;
    static DataInputStream dis;
    static DataOutputStream dos;

    public ServerInterface(Server server) throws IOException {
        //DATA PART
        currentOnlineList = new ArrayList<>();
        listPartner = new ArrayList<>();
        this.nameSrc = nameSrc;

        //SOCKET INIT PART

        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        Socket s = new Socket(ip, ServerPort);

        // obtaining input and out streams
        dis = new DataInputStream(s.getInputStream());
        dos = new DataOutputStream(s.getOutputStream());
        sendOnlineListRequest();

        //GUI PART
        btnEnable.setEnabled(false);
        add(panel1);
        setSize(500,500);
        setVisible(true);
        updateOnlineListOnScreen(server);
        roomGUI = new ClientPrivateRoom(nameDest, nameSrc, server);


        //THREADING PART
        // readMessage thread
        readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {

                while (true) {
                    try {
                        // read the message sent to this client
                        String received = dis.readUTF();

                        StringTokenizer st = new StringTokenizer(received, "#");
                        String command = st.nextToken();
                        String content = st.nextToken();

                        switch (command){
                            case "reqList":
                                updateOnlineListOnScreen(server);
                                break;
                            case "msg":
                                roomGUI.solveMessage(received);
                                break;
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        readMessage.start();



        btnCreatePrivateRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nameDest;
                if (getListOnlineList().getSelectedIndex() != -1) {
                    nameDest = "" + getListOnlineList().getSelectedValue();
                    try {
                        createPrivateRoom(nameDest, nameSrc);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }

                }
            }
        });

        btnSendListToAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendOnlineListRequest();
            }
        });

        btnDisable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnEnable.setEnabled(true);
                btnDisable.setEnabled(false);
                try {
                    server.getServerListener().wait();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }

            }
        });

        btnEnable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnEnable.setEnabled(false);
                btnDisable.setEnabled(true);
                server.getServerListener().notify();

//                server.switchOnKillingMainThread(false);
//                server.getServerListener().start();
            }
        });
    }

    public void updateOnlineListOnScreen(Server s) {

            DefaultListModel<String> l1 = new DefaultListModel<>();
            ArrayList<String> l2 = s.getCurrentOnlineList();
            for (int i = 0; i < l2.size(); i++) {
                l1.addElement(l2.get(i));
            }

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    listOnlineList.setModel(l1);
                }
            });

    }

    public static void sendOnlineListRequest()
    {
        // write on the output stream
        String msgSent = "reqList#";
        try {
            dos.writeUTF(msgSent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createPrivateRoom(String nameDest, String nameSrc) throws InterruptedException {
        roomGUI.setNewNameDest(nameDest);
        roomGUI.setNewNameSrc(nameSrc);
        roomGUI.setVisible(true);
        setVisible(false);
        roomGUI.setBoxChatLog("");

        //List chat log for different partners
        //If exists
        for (int i = 0; i < listPartner.size(); i++)
        {
            if(nameDest.equals(listPartner.get(i).getPartnerName())){
                roomGUI.preloadChatLogServer(this);
                return;
            }
        }

        //If not, add new one
        PartnerInfo p = new PartnerInfo(nameDest, "noName");
        p.setListChatLog(new ArrayList<String>());
        listPartner.add(p);
        roomGUI.preloadChatLogServer(this);
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public ArrayList<PartnerInfo> getListPartner() {
        return listPartner;
    }

    public JList getListOnlineList() {
        return listOnlineList;
    }
}

//    // sendMessage thread
//    Thread sendMessage = new Thread(new Runnable()
//    {
//        @Override
//        public void run() {
//            while (true) {
//
//                // read the message to deliver.
//                String msg = scn.nextLine();
//
//                try {
//                    // write on the output stream
//                    dos1.writeUTF(msg);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    });
