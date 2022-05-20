import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Login extends JFrame implements ActionListener {
    JPanel panel;
    JFrame server;
    JLabel user_label, password_label, message;
    JTextField userName_text;
    JPasswordField password_text;
    JButton submit, cancel;
    List<String> fill = new ArrayList<String>();
    ResultSet result;
    Statement stmt;
    Login(List<String> fill1, ResultSet result1,Statement stmt1 ) {
        
        //final parameter values
        fill = fill1;
        result = result1;
        stmt = stmt1;
        
        //Server Panel
        server = new JFrame();
        server.setVisible(true);

        // User Label
        user_label = new JLabel();
        user_label.setText("User Name :");
        userName_text = new JTextField();
        
        // Password
        password_label = new JLabel();
        password_label.setText("Password :");
        password_text = new JPasswordField();

        // Submit
        submit = new JButton("SUBMIT");

        //General Panel Content
        panel = new JPanel(new GridLayout(3, 1));
        panel.add(user_label);
        panel.add(userName_text);
        panel.add(password_label);
        panel.add(password_text);

        message = new JLabel();
        panel.add(message);
        panel.add(submit);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Adding the listeners to components..
        submit.addActionListener(this);
        add(panel, BorderLayout.CENTER);
        setTitle("Please Login Here !");
        setSize(300, 100);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae)  {
        String userName = userName_text.getText();
        String password=String.valueOf(password_text.getPassword());
        JFrame frame = new JFrame("Layne's GUI");
        
        if (userName.trim().equals("manager") && password.trim().equals("4011")) {
            message.setText(" Hello " + userName + "");
            try {
                new ManagerPanel(stmt, frame);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else  if (userName.trim().equals("server") && password.trim().equals("K123")) {
            message.setText(" Hello " + userName + "");
            new ServerPanel(fill, result, stmt, server);
        }

    }
    public static void main(String[] args) {
       // new Login();
    }
}