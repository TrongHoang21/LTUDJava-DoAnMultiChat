package GUI;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class ClientPrivateRoom extends JFrame {
    private JTextArea boxChatLog;
    private JButton btnBack;
    private JButton btnSend;
    private JTextArea boxMsg;
    private JLabel labelDestination;
    private JPanel panel1;
    private ArrayList<String> listChatLog;

    public ClientPrivateRoom(String nameDest)
    {
        listChatLog = new ArrayList<String>();
        labelDestination.setText(nameDest);
        add(panel1);
        setSize(500,500);
        setVisible(true);
    }

    public String getBoxMsg() {
        return boxMsg.getText();
    }

    public void setBoxMsg(String t) {
        this.boxMsg.setText(t);
    }

    public JButton getBtnSend() {
        return btnSend;
    }

    public void showMsgOnScreen(String msg){
        listChatLog.add(msg);
        SwingUtilities.invokeLater(new Runnable() { //invokeAndWait() is meant to be called from the non-GUI thread
            @Override
            public void run() {
                boxChatLog.append("\n");
                boxChatLog.append(msg);
            }
        });

    }
}
