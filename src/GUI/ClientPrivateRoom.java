package GUI;

import com.vdurmont.emoji.EmojiParser;
import multiChat.Client;
import multiChat.Server;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static java.awt.event.KeyEvent.VK_ENTER;

public class ClientPrivateRoom extends JFrame {
    private JTextArea boxChatLog;
    private JButton btnBack;
    private JButton btnSend;
    private JTextArea boxMsg;
    private JLabel labelDestination;
    private JPanel panel1;
    private JLabel labelSource;
    private JCheckBox cbEnterToSend;
    private ArrayList<String> listChatLog;
    private String nameDest;
    private String nameSource;
    ActionListener AL;

    public ClientPrivateRoom(String nameDst, String nameSrc, Client c)
    {
        this.nameDest = nameDst;
        listChatLog = new ArrayList<String>();
        labelDestination.setText("To: " + nameDest);
        labelSource.setText("From: " + nameSrc);
        boxChatLog.setText("");
        add(panel1);
        setSize(500,500);

        this.getBtnSend().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //sendMessage.start();

                String msg = getBoxMsg();
                setBoxMsg("");

                if (!msg.equals("")) {
                    String msgSent = "msg#" + nameDest + "#" + msg;
                    Client.Sender(msgSent);

                    msg = "Me: " + msg;
                    showMsgOnScreen(msg);

                    //Save data
                    listChatLog.add(msg);
                }
            }
        });

        this.getBtnBack().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                c.getMainGUI().setVisible(true);
                boxMsg.setText("");
                boxChatLog.setText("");

                //save data
                //c.saveChatLog(nameDest, listChatLog);
            }
        });
    }

    public ClientPrivateRoom(String nameDst, String nameSrc, Server server)
    {
        this.nameDest = nameDst;
        listChatLog = new ArrayList<String>();
        labelDestination.setText("To: " + nameDest);
        labelSource.setText("From: " + nameSrc);
        boxChatLog.setText("");
        boxMsg.addKeyListener(new MKeyListener(this));
        add(panel1);
        setSize(500,500);



        AL = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //sendMessage.start();

                String msg = getBoxMsg();
                setBoxMsg("");

                if (!msg.equals("")) {
                    String msgSent = "msg#" + nameDest + "#" + msg;
                    try {
                        server.getMainGUI().getDos().writeUTF(msgSent);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

                    msg = "Me: " + msg;
                    showMsgOnScreen(msg);

                    //Save data
                    listChatLog.add(msg);
                }
            }
        };



        this.getBtnSend().addActionListener(AL);

        this.getBtnBack().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                server.getMainGUI().setVisible(true);
                boxMsg.setText("");
                boxChatLog.setText("");

                //save data
                //c.saveChatLog(nameDest, listChatLog);
            }
        });
    }

    public void preloadChatLog(Client c){
        //Preload chat log
        for(int i = 0; i< c.getListPartner().size(); i++) {
            if(nameDest.equals(c.getListPartner().get(i).getPartnerName())) {
                listChatLog = c.getListPartner().get(i).getListChatLog();
            }
        }

        String data = "";
        for(String msg : listChatLog){
            data += msg;
            data += "\n";
        }

        String finalData = data;

        boxChatLog.setText("");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                showMsgOnScreen(finalData);
            }
        });
    }

    public void preloadChatLogServer(ServerInterface s) {
        //Preload chat log
        for (int i = 0; i < s.getListPartner().size(); i++) {
            if (nameDest.equals(s.getListPartner().get(i).getPartnerName())) {
                listChatLog = s.getListPartner().get(i).getListChatLog();
            }
        }
    }

    public void solveMessage(String received)
    {
        StringTokenizer st = new StringTokenizer(received, "#");
        String command = st.nextToken();
        String content = st.nextToken();

        switch (command){
            case "msg":
                showMsgOnScreen(content);

                //Save data
                listChatLog.add(content);
                break;
        }

    }

    public void setNewNameDest(String nameDst)
    {
        labelDestination.setText(nameDst);
        this.nameDest = nameDst;
    }
    public void setNewNameSrc(String nameSrc)
    {
        labelSource.setText(nameSrc);
        this.nameSource = nameSrc;
    }

    public String getBoxMsg() {
        return boxMsg.getText();
    }

    public void setBoxMsg(String t) {
        this.boxMsg.setText(t);
    }

    public void setBoxChatLog(String t) {
        this.boxChatLog.setText(t);
    }

    public JButton getBtnSend() {
        return btnSend;
    }

    public JButton getBtnBack() {
        return btnBack;
    }

    public ArrayList<String> getListChatLog() {
        return listChatLog;
    }

    public void setListChatLog(ArrayList<String> listChatLog) {
        this.listChatLog = listChatLog;
    }

    public void showMsgOnScreen(String msg){
        SwingUtilities.invokeLater(new Runnable() { //invokeAndWait() is meant to be called from the non-GUI thread
            @Override
            public void run() {
                boxChatLog.append("\n");
                String result = EmojiParser.parseToUnicode(msg);
                boxChatLog.append(result);

                //Sample for emoji https://github.com/Coding/emoji-java
                //String str = "An :grinning:awesome :smiley:string &#128516;with a few :wink:emojis!";
                //String result = EmojiParser.parseToUnicode(str);
                //boxChatLog.append(result);
            }
        });
    }

    public JCheckBox getCbEnterToSend() {
        return cbEnterToSend;
    }

    public ActionListener getAL(){
        return this.AL;
    }

}

class MKeyListener extends KeyAdapter {
    ClientPrivateRoom cl;
    public MKeyListener(ClientPrivateRoom c){
        cl = c;
    };
    @Override
    public void keyPressed(KeyEvent event) {

        if (event.getKeyCode() == VK_ENTER) {
            if(cl.getCbEnterToSend().isSelected()) {
                cl.getAL().actionPerformed(null);
            }
        }
    }
}