package GUI;

import multiChat.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientRegister extends JFrame {
    private JTextArea boxUsername;
    private JButton btnSubmit;
    private JLabel labelInstruct;
    private JPanel panel1;
    private boolean flag = false;

    public ClientRegister(Client c)
    {
        add(panel1);
        setSize(500,500);
        setVisible(true);

        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c.setUserName(boxUsername.getText());

               // c.getMainGUI().setVisible(true);
                dispose();
            }
        });
    }

    public JButton getBtnSubmit() {
        return btnSubmit;
    }

    public JTextArea getBoxUsername() {
        return boxUsername;
    }

    public boolean getFlag(){
        return this.flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
