import javax.swing.*;
import java.awt.*;

public class CardLayoutExample {
    CardLayoutExample(){
        JFrame frame = new JFrame("Gestion des entretiens de matériels informatiques");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the default font for the frame
        setDefaultFont();

        setDefaultForeground();

        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);
        // Create the main panel
        JPanel mainPanel = new JPanel(new CardLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(cardPanel);

        // Create the side menu panel
        JPanel sideMenuPanel = new JPanel();
        sideMenuPanel.setBackground(Color.decode("#edcf2e"));
        sideMenuPanel.setPreferredSize(new Dimension(230, frame.getHeight())); // Set the preferred width to make it fixed

        JPanel panel1 = new EquipmentSection();
        JPanel panel2 = new MaintainerSection();
        JPanel panel3 = new MaintenanceScheduleSection();
        JPanel panel4 = new PrestationSection();
        JPanel panel5 = new InterventionsList();

        panel1.setBackground(Color.WHITE);
        panel2.setBackground(Color.WHITE);
        panel3.setBackground(Color.WHITE);
        panel4.setBackground(Color.WHITE);
        panel5.setBackground(Color.WHITE);


        cardPanel.add(panel1, "panel1");
        cardPanel.add(panel2, "panel2");
        cardPanel.add(panel3, "panel3");
        cardPanel.add(panel4, "panel4");
        cardPanel.add(panel5, "panel5");

        JMenuBar menuBar = new JMenuBar();
        menuBar.setLayout(new BoxLayout(menuBar, BoxLayout.Y_AXIS));

        JMenuItem menuItem = new JMenuItem("Gestion des entretiens");
        JMenuItem menuItem1 = new JMenuItem("  Matériels");
        JMenuItem menuItem2 = new JMenuItem("  Intervenants");
        JMenuItem menuItem3 = new JMenuItem("  Entretiens");
        JMenuItem menuItem4 = new JMenuItem("  Prestations");
        JMenuItem menuItem5 = new JMenuItem("  Liste des interventions");


        menuItem1.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        menuItem2.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        menuItem3.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        menuItem4.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        menuItem5.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        menuItem.setFont(new Font("Times New Roman", Font.BOLD, 22));

        String menuColor = "#002456";
        Color color = Color.decode(menuColor);
        menuItem.setForeground(color);
        menuItem1.setForeground(color);
        menuItem2.setForeground(color);
        menuItem3.setForeground(color);
        menuItem4.setForeground(color);
        menuItem5.setForeground(color);

        menuItem.setBackground(Color.WHITE);
        menuItem1.setBackground(Color.WHITE);
        menuItem2.setBackground(Color.WHITE);
        menuItem3.setBackground(Color.WHITE);
        menuItem4.setBackground(Color.WHITE);
        menuItem5.setBackground(Color.WHITE);

        menuItem1.setPreferredSize(new Dimension(0, 40));
        menuItem2.setPreferredSize(new Dimension(0, 40));
        menuItem3.setPreferredSize(new Dimension(0, 40));
        menuItem4.setPreferredSize(new Dimension(0, 40));
        menuItem5.setPreferredSize(new Dimension(0, 40));
        menuItem.setPreferredSize(new Dimension(228, 50));

        // Create separators
        JSeparator separator = new JSeparator();
        JSeparator separator1 = new JSeparator();
        JSeparator separator2 = new JSeparator();
        JSeparator separator3 = new JSeparator();
        JSeparator separator4 = new JSeparator();
        JSeparator separator5 = new JSeparator();
        JSeparator separator6 = new JSeparator();

        menuItem1.addActionListener(e -> cardLayout.show(cardPanel, "panel1"));
        menuItem2.addActionListener(e -> cardLayout.show(cardPanel, "panel2"));
        menuItem3.addActionListener(e -> cardLayout.show(cardPanel, "panel3"));
        menuItem4.addActionListener(e -> cardLayout.show(cardPanel, "panel4"));
        menuItem5.addActionListener(e -> cardLayout.show(cardPanel, "panel5"));

        menuBar.add(menuItem);
        menuBar.add(separator);
        menuBar.add(menuItem1);
        menuBar.add(separator1);
        menuBar.add(menuItem2);
        menuBar.add(separator2);
        menuBar.add(menuItem3);
        menuBar.add(separator3);
        menuBar.add(menuItem4);
        menuBar.add(separator4);
        menuBar.add(menuItem5);

        menuBar.setBackground(Color.WHITE);

        // Add the menu bar to the side menu panel
        sideMenuPanel.add(menuBar);

        // Set the layout for the frame
        frame.setLayout(new BorderLayout());

        frame.add(sideMenuPanel, BorderLayout.WEST);

        // Add the main panel to the center
        frame.add(mainPanel, BorderLayout.CENTER);

        frame.setPreferredSize(new Dimension(1460, 870));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    public static void main(String[] args) {
        new CardLayoutExample();
    }

    private static void setDefaultFont() {
        // Create a custom font with desired properties
        Font customFont = new Font("Times New Roman", Font.PLAIN, 16);

        // Set the custom font as the default font for the frame
        UIManager.put("Label.font", customFont);
        UIManager.put("Button.font", customFont);
        UIManager.put("TextField.font", customFont);
        UIManager.put("ComboBox.font", customFont);
    }

    private static void setDefaultForeground(){
        Color color = Color.decode("#002456");
        UIManager.put("Label.foreground", color);
        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("Table.background", Color.WHITE);
    }

}
