package multiChat;

// Java implementation for multithreaded chat client
// Save file as Client.java

import GUI.ClientOnlineList;
import GUI.ClientPrivateRoom;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;



public class Client
{
    //DATA PROPERTIES
    private static String nameDest = "server";
    private String nameSrc;
    private static String userName = "NoNam";
    private ArrayList<String> currentOnlineList;
    private ArrayList<PartnerInfo> listPartner;
    //NON DATA PROPs
    static DataInputStream dis;
    static DataOutputStream dos;
    final static int ServerPort = 1234;
    GUI.ClientOnlineList mainGUI;
    GUI.ClientPrivateRoom roomGUI;
    Thread readMessage;

    public Client(String nameSrc) throws UnknownHostException, IOException{
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

        //GUI PART
        mainGUI = new ClientOnlineList(this);
        roomGUI = new ClientPrivateRoom(nameDest, nameSrc, this);


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
                                handleOnlineList(content);
                                break;
                            case "reqInfo":
                                handleInfoRequest(content);
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

        mainGUI.getBtnCreatePrivateRoom().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nameDest;
                if (mainGUI.getListOnlineList().getSelectedIndex() != -1) {
                    nameDest = "" + mainGUI.getListOnlineList().getSelectedValue();
                    try {
                        createPrivateRoom(nameDest, nameSrc);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }

                }
            }
        });
    }

    //TOOL FUNCTIONS
    public static void Sender(String msgSent)
    {
        try {
            dos.writeUTF(msgSent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendOnlineListRequest()
    {
        // write on the output stream
        String msgSent = "reqList#";
        Sender(msgSent);
    }

    public void sendInfoRequest(String registName) {
        // write on the output stream
        String msgSent = "reqInfo#" + registName;
        Sender(msgSent);
    }

    public void handleOnlineList(String content)
    {
        StringTokenizer st = new StringTokenizer(content, "_");
        currentOnlineList.clear();

        while(st.hasMoreTokens())
        {
            currentOnlineList.add(st.nextToken());
        }

        mainGUI.updateOnlineListOnScreen(currentOnlineList);
    }

    public void handleInfoRequest(String content)
    {
        StringTokenizer st = new StringTokenizer(content, "_");

        while(st.hasMoreTokens())
        {
            nameSrc = st.nextToken();
            userName = st.nextToken();
        }

        mainGUI.updateInfoOnScreen(nameSrc, userName);
    }

    public void createPrivateRoom(String nameDest, String nameSrc) throws InterruptedException {
        roomGUI.setNewNameDest(nameDest);
        roomGUI.setNewNameSrc(nameSrc);
        roomGUI.setVisible(true);
        mainGUI.setVisible(false);
        roomGUI.setBoxChatLog("");

        //List chat log for different partners
        //If exists
        for (int i = 0; i < listPartner.size(); i++)
        {
            if(nameDest.equals(listPartner.get(i).getPartnerName())){
                roomGUI.preloadChatLog(this);
                return;
            }
        }

        //If not, add new one
        PartnerInfo p = new PartnerInfo(nameDest, "noName");
        p.setListChatLog(new ArrayList<String>());
        listPartner.add(p);
        roomGUI.preloadChatLog(this);
    }

    public void saveChatLog(String nameDest, ArrayList<String> src){
        for (int i = 0; i < listPartner.size(); i++)
        {
            if(nameDest.equals(listPartner.get(i).getPartnerName())){
                listPartner.get(i).setListChatLog(src);
                return;
            }
        }
    }

    public ArrayList<String> getCurrentOnlineList() {
        return currentOnlineList;
    }

    public ArrayList<PartnerInfo> getListPartner() {
        return listPartner;
    }

    public ClientOnlineList getMainGUI() {
        return mainGUI;
    }

    public static void main(String args[]) throws IOException {
        new Client("client 0");
    }
}

// sendMessage thread
//        Thread sendMessage = new Thread(new Runnable()
//        {
//            @Override
//            public void run() {
//                    // read the message to deliver.
//                    String msg = roomGUI.getBoxMsg();
//                    roomGUI.setBoxMsg("");
//
//                    if (!msg.equals("")) {
//                        try {
//                            // write on the output stream
//                            dos.writeUTF(msg);
//                            roomGUI.showMsgOnScreen(msg);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//            }
//        });