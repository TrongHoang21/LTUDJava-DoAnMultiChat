package multiChat;

// Java implementation for multithreaded chat client
// Save file as Client.java

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{
    final static int ServerPort = 1234;
    private static String nameDest = "server";
    public static void main(String args[]) throws UnknownHostException, IOException
    {
        //GUI PART

        GUI.ClientPrivateRoom roomGUI = new GUI.ClientPrivateRoom(nameDest);

        //SOCKET PART
        Scanner scn = new Scanner(System.in);

        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        Socket s = new Socket(ip, ServerPort);

        // obtaining input and out streams
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        // readMessage thread
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {

                while (true) {
                    try {
                        // read the message sent to this client
                        String msg = dis.readUTF();
                        roomGUI.showMsgOnScreen(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        readMessage.start();

        roomGUI.getBtnSend().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //sendMessage.start();

                String msg = roomGUI.getBoxMsg();
                roomGUI.setBoxMsg("");

                if (!msg.equals("")) {
                    try {
                        // write on the output stream
                        String msgSent = msg + "#" + nameDest;
                        dos.writeUTF(msgSent);
                        msg = "Me: " + msg;
                        roomGUI.showMsgOnScreen(msg);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
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