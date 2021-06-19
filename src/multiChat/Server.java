package multiChat;

// Java implementation of  Server side
// It contains two classes : Server and ClientHandler
// Save file as Server.java

import GUI.ServerInterface;

import java.io.*;
import java.util.*;
import java.net.*;

// Server class
public class Server
{
    // Vector to store active clients
    static Vector<ClientHandler> ar = new Vector<>();

    // counter for clients
    static int i = 0;

    ServerInterface mainGUI;

    public Server() throws IOException {
        // server is listening on port 1234
        ServerSocket ss = new ServerSocket(1234);

        mainGUI = new ServerInterface(this);

        // running infinite loop for getting client request
        Socket s;
        System.out.println("Waiting for a Client");

        while (true)
        {
            // Accept the incoming request

            s = ss.accept();

            // obtain input and output streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            System.out.println("Creating a new handler for this client...");

            // Create a new handler object for handling this request.
            ClientHandler mtch = new ClientHandler(s,"client " + i, dis, dos);

            // Create a new Thread with this object.
            Thread t = new Thread(mtch);

            System.out.println("Adding this client to active client list");

            // add this client to active clients list
            ar.add(mtch);
            System.out.println(mtch.name + " joined");

            System.out.println("Active users list:");
            for (ClientHandler mc : Server.ar)
            {
                System.out.println(mc.name);
            }

            // start the thread.
            t.start();

            // increment i for new client.
            // i is used for naming only, and can be replaced
            // by any naming scheme
            i++;

        }




    }

    public static void main(String[] args) throws IOException
    {
        Server server1 = new Server();
    }

    public ServerInterface getMainGUI() {
        return mainGUI;
    }

    public ArrayList<String> getCurrentOnlineList() {
        ArrayList<String> res = new ArrayList<>();
        for (int i = 0; i < getAr().size(); i++) {
            res.add(getAr().get(i).getName());
        }
        return res;
    }

    public static Vector<ClientHandler> getAr() {
        return ar;
    }
}

// ClientHandler class
class ClientHandler implements Runnable
{
    public final String name;
    public String userName;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;

    // constructor
    public ClientHandler(Socket s, String name,
                         DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.userName = "";
        this.s = s;
        this.isloggedin=true;
    }

    @Override
    public void run() {

        String received;
        while (true)
        {
            try
            {
                // receive the string
                received = dis.readUTF();

                System.out.println(received);

                if(received.equals("logout")){
                    this.isloggedin=false;
                    this.s.close();
                    break;
                }

                // break the string into message and recipient part
                StringTokenizer st = new StringTokenizer(received, "#");
                String command = st.nextToken();

                if(command.equals("msg")) {
                    String recipient = st.nextToken();
                    String MsgToSend = st.nextToken();

                    // search for the recipient in the connected devices list.
                    // ar is the vector storing client of active users
                    for (ClientHandler mc : Server.ar) {
                        // if the recipient is found, write on its
                        // output stream
                        if (mc.name.equals(recipient) && mc.isloggedin == true) {
                            mc.dos.writeUTF("msg#" + this.name + ": " + MsgToSend);
                            break;
                        }
                    }
                } else if(command.equals("reqList")) {  //request Online list
                    String MsgToSend = handleListRequest();
                    for (ClientHandler mc : Server.ar) {
                        // if the recipient is found, write on its
                        // output stream
                        if (mc.isloggedin == true) {
                            mc.dos.writeUTF("reqList#" + MsgToSend);
                        }
                    }
                } else if(command.equals("reqInfo")) {  //request Online list
                    this.userName = st.nextToken();
                    dos.writeUTF("reqInfo#" + this.name + "_" + this.userName);

                }
            } catch (IOException e) {

                e.printStackTrace();
            }

        }

        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public String handleListRequest()
    {
        String res = "";
        for (ClientHandler mc : Server.ar) {
            res += mc.name + "_";
        }

        return res;
    }

    public String getName() {
        return name;
    }
}

//        // getting localhost ip
//       InetAddress ip = InetAddress.getByName("localhost");
//
//        // establish the connection
//            Socket s = new Socket(ip, 1234);

//        Scanner scn = new Scanner(System.in);
//
//        // obtaining input and out streams
//        DataInputStream dis1 = new DataInputStream(s.getInputStream());
//        DataOutputStream dos1 = new DataOutputStream(s.getOutputStream());
//
//        // sendMessage thread
//        Thread sendMessage = new Thread(new Runnable()
//        {
//            @Override
//            public void run() {
//                while (true) {
//
//                    // read the message to deliver.
//                    String msg = scn.nextLine();
//
//                    try {
//                        // write on the output stream
//                        dos1.writeUTF(msg);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//
//        // readMessage thread
//        Thread readMessage = new Thread(new Runnable()
//        {
//            @Override
//            public void run() {
//
//                while (true) {
//                    try {
//                        String received = dis1.readUTF();
//
//                        StringTokenizer st = new StringTokenizer(received, "#");
//                        String command = st.nextToken();
//                        String content = st.nextToken();
//
//                        switch (command){
//                            case "reqList":
//                                //do things
//                                break;
//                            case "msg":
//                                //roomGUI.showMsgOnScreen(content);
//                                System.out.println(content);
//                                break;
//                        }
//
//                    } catch (IOException e) {
//
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//
//        sendMessage.start();
//        readMessage.start();

//Make server like a client 0 to send and receive msg from clients
//        s = ss.accept();
//        System.out.println("Admin joined");
//        DataInputStream dis2 = new DataInputStream(s.getInputStream());
//        DataOutputStream dos2 = new DataOutputStream(s.getOutputStream());
//        ClientHandler servInstance = new ClientHandler(s,"server", dis2, dos2);
//        ar.add(servInstance);
//        Thread t1 = new Thread(servInstance);
//        t1.start();
//new Client("server");