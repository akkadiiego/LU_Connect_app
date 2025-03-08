package Client.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class HomeScreen extends JPanel {
    private JLabel logoLabel;
    private ImageIcon logoIcon;
    private LU_Connect_App parentApp;

    public HomeScreen(LU_Connect_App luConnectApp){
        this.parentApp = luConnectApp;
        setLayout(new BorderLayout());

        logoIcon = new ImageIcon(getClass().getClassLoader().getResource("LUlogo.svg.png"));
        Image image = logoIcon.getImage().getScaledInstance(luConnectApp.getWidth(), luConnectApp.getHeight(), Image.SCALE_SMOOTH);
        logoLabel = new JLabel(new ImageIcon(image));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        resizeLogo();
        luConnectApp.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeLogo();
            }
        });

        JButton login = new JButton("Login");
        JButton register = new JButton("Register");
        styleButton(login);
        styleButton(register);

        login.addActionListener(e -> luConnectApp.showScreen("Login"));
        register.addActionListener(e -> luConnectApp.showScreen("Register"));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.setBackground(getBackground());
        buttonsPanel.add(login);
        buttonsPanel.add(register);

        add(logoLabel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void resizeLogo() {
        int width = parentApp.getWidth() / 2;
        int height = parentApp.getHeight() / 3;
        if (width > 0 && height > 0) {
            Image scaledImage = logoIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledImage));
        }
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(parentApp.RED);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
