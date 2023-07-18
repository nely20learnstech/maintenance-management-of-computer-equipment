import java.awt.*;
import java.sql.*;
import javax.swing.*;
// import javax.swing.table.DefaultTableModel;
// import java.awt.BorderLayout;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
// Table column
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.*;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

public class EquipmentSection extends JPanel implements ActionListener{
    JTable equipmentTable;

    private JTextField equipmentNumberTextField;
    private JTextField descriptionTextField;
    private JTextArea characteriticsTextArea;

    private JTextField searchField;
    JComboBox stateComboBox;
    private JButton insertButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton cancelButton;

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    JFreeChart pieChart;
    ChartPanel chartPanel;

    final String DB_URL = "jdbc:mysql://localhost/maintenance_management?serverTimezone=UTC";
    final String USERNAME = "root";
    final String PASSWORD = "";


    // State ComboBox
    private JComboBox<String> switchTableBox;
    private JLabel switchLabel;

    // Selected option checker
    String selectedOption;

    JLabel inGoodState;
    JLabel inDestroyedState;
    JLabel inDamagedState;

    // Search TextField


    private TableRowSorter<DefaultTableModel> sorter;

    EquipmentSection()
    {
//        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 800);
        this.setLayout(new FlowLayout());
//        this.setLayout(null);

        this.setVisible(true);

        // Add different state labels
        inGoodState = new JLabel();
        inDamagedState = new JLabel();
        inDestroyedState = new JLabel();

//        inGoodState.setBounds(500, 650, 150, 30);
//        inDamagedState.setBounds(600, 650, 150, 30);
//        inDestroyedState.setBounds(700, 650, 150, 30);

        JPanel equipmentStatePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        equipmentStatePanel.setBounds(15, 470, 430, 40);
//        equipmentStatePanel.setBackground(Color.decode("#e1c1a4"));


        equipmentStatePanel.add(inGoodState);
        equipmentStatePanel.add(inDamagedState);
        equipmentStatePanel.add(inDestroyedState);
        add(equipmentStatePanel);


        JLabel equipmentSectionLabel = new JLabel();
        equipmentSectionLabel.setText("Matériels");

        // Set label icon

        // Set bounds
        equipmentSectionLabel.setBounds(10,0,900,100);
        equipmentSectionLabel.setForeground(Color.decode("#002456"));
        equipmentSectionLabel.setFont(new Font("Times New Roman", Font.PLAIN,70));


        // Table Section
        JPanel tablePanel = new JPanel();
//        tablePanel.setBackground(Color.BLUE);
        tablePanel.setBounds(580, 200, 600, 600);
        //tablePanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        equipmentTable = new JTable();

        // Set the font for the table
        Font font = new Font("Times New Roman", Font.PLAIN, 15);
        equipmentTable.setFont(font);

        // Center Text Renderer
        equipmentTable.setDefaultRenderer(Object.class, new CenterTextRenderer());
        // equipmentTable.setBounds(0, 0, 800, 500);
        // tablePanel.setBackground(Color.GREEN);
        // equipmentTable.setLayout(new FlowLayout(FlowLayout.CENTER));
        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(equipmentTable);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        tablePanel.add(scrollPane);



        // Show state
        showStateCounts();
        fetchStateData();

        // Show table
        showTableData();

        equipmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Get the selected row index
                int selectedRow = equipmentTable.getSelectedRow();

                // Check if a row is selected
                if (selectedRow != -1) {
                    // Retrieve the data from the selected row for each column
                    Object data1 = equipmentTable.getValueAt(selectedRow, 0);
                    Object data2 = equipmentTable.getValueAt(selectedRow, 1);
                    Object data3 = equipmentTable.getValueAt(selectedRow, 2);
                     Object data4 = equipmentTable.getValueAt(selectedRow, 3);

                    // Populate the JTextField with the retrieved data
                    equipmentNumberTextField.setText(data1.toString());
                    descriptionTextField.setText(data2.toString());
                    characteriticsTextArea.setText(data3.toString());
                    stateComboBox.setSelectedItem(data4.toString());
                } else {
                    // No row is selected, clear the JTextField
                    equipmentNumberTextField.setText("");
                    descriptionTextField.setText("");
                    characteriticsTextArea.setText("");
                    // stateComboBox.setText("");
                }
            }
        });


        // Label that indicates the current table format
        switchLabel = new JLabel();
        switchLabel.setText("Matériels listés par ordre d'ajout");


        // Form Section
        JPanel crudSection = new JPanel();
//        crudSection.setBackground(Color.GRAY);
        crudSection.setBounds(10, 150, 500, 300);
        crudSection.setLayout(null);

        JLabel equipmentNumberLabel = new JLabel();
        equipmentNumberLabel.setBounds(5, 5, 200, 30);
        equipmentNumberLabel.setText("Numéro de l'équipement : ");
        equipmentNumberLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));
        crudSection.add(equipmentNumberLabel);

        equipmentNumberTextField = new JTextField();
        equipmentNumberTextField.setFont(new Font("Times New Roman", Font.PLAIN,16));
        equipmentNumberTextField.setBounds(175, 5, 200, 30);
        // equipmentNumberTextField.setEditable(false);
        crudSection.add(equipmentNumberTextField);


        JLabel descriptionLabel = new JLabel();
        descriptionLabel.setBounds(5, 55, 150, 30);
        descriptionLabel.setText("Description : ");
        descriptionLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));
        crudSection.add(descriptionLabel);

        descriptionTextField = new JTextField();
        descriptionTextField.setFont(new Font("Times New Roman", Font.PLAIN,16));
        descriptionTextField.setBounds(175, 55, 200, 30);
        crudSection.add(descriptionTextField);

        JLabel characteristicsLabel = new JLabel();
        characteristicsLabel.setBounds(5, 110, 200, 30);
        characteristicsLabel.setText("Caractéristiques : ");
        characteristicsLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));
        crudSection.add(characteristicsLabel);

        characteriticsTextArea = new JTextArea();
        characteriticsTextArea.setFont(new Font("Times New Roman", Font.PLAIN,16));
        characteriticsTextArea.setBounds(175, 110, 200, 60);
        characteriticsTextArea.setLineWrap(true);
        characteriticsTextArea.setBorder(new LineBorder(Color.LIGHT_GRAY)); // Set a simple line border
        crudSection.add(characteriticsTextArea);

        JLabel stateLabel = new JLabel();
        stateLabel.setBounds(5, 185, 150, 50);
        stateLabel.setText("Etat : ");
        stateLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));

        crudSection.add(stateLabel);

        String[] state = {"bon état", "mauvais état", "abimé"};
        stateComboBox = new JComboBox<>(state);
        stateComboBox.addActionListener(this);
        stateComboBox.setBounds(175, 195, 100, 30);
        crudSection.add(stateComboBox);

        // Buttons

        insertButton = new JButton("Ajouter");
        insertButton.setBounds(5, 265, 100, 30);

        crudSection.add(insertButton);

        String addColor = "#2ecc71";
        // Convert hexadecimal color code to Color object
        Color color = Color.decode(addColor);
        insertButton.setBackground(color);
        insertButton.setForeground(Color.WHITE);

        updateButton = new JButton("Modifier");
        updateButton.setBounds(115, 265, 100, 30);
        String forEdit = "#64b9ee";
        Color editColor = Color.decode(forEdit);
        updateButton.setBackground(editColor);
        updateButton.setForeground(Color.WHITE);
        crudSection.add(updateButton);

        deleteButton = new JButton("Supprimer");
        deleteButton.setBounds(225, 265, 100, 30);
        crudSection.add(deleteButton);

        String deleteColor = "#ff4c58";
        // Convert hexadecimal color code to Color object
        Color forDelete = Color.decode(deleteColor);
        deleteButton.setBackground(forDelete);
        deleteButton.setForeground(Color.WHITE);

        cancelButton = new JButton("Effacer");
        cancelButton.setBounds(335, 265, 100, 30);
        crudSection.add(cancelButton);
        cancelButton.setBackground(Color.GRAY);
        cancelButton.setForeground(Color.WHITE);


        this.add(equipmentSectionLabel);
        this.add(tablePanel);
        this.add(crudSection);
        this.setSize(1200, 800);
        // this.setLayout(new FlowLayout());
        this.setLayout(null);
        this.setMinimumSize(new Dimension(1100, 800));
        this.setVisible(true);

//        this.pack();

        // Create the switch table JComboBox with options
        switchTableBox = new JComboBox<>();
        switchTableBox.addItem("Lister par ordre d'ajout");
        switchTableBox.addItem("Lister par état");


        switchTableBox.setBounds(995, 80, 180, 30);
        switchTableBox.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        switchLabel.setBounds(580, 100, 450, 40);
        switchLabel.setFont(new Font("Times New Roman", Font.CENTER_BASELINE, 20));
        add(switchTableBox);
        add(switchLabel);

        switchTableBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedOption = (String) switchTableBox.getSelectedItem();

                if(selectedOption.equals("Lister par ordre d'ajout") || selectedOption == null){
                    switchLabel.setText("Matériels listés par ordre d'ajout");
                    showTableData();
                } else if (selectedOption.equals("Lister par état")) {
                    switchLabel.setText("Matériels listés par état");
                    showOrderedTableData();
                }
                else
                {
                    showTableData();
                    showStateCounts();
                }
            }
        });


        // buttons perform actions
        insertButton.addActionListener(e -> {
            String equipment = equipmentNumberTextField.getText().trim();
            JLabel  equipmentErrorLabel = new JLabel();
            JLabel   descriptionErrorLabel = new JLabel();
            JLabel characteristicsErrorLabel = new JLabel();
            equipmentErrorLabel.setBounds(5, 30, 400, 30);
            descriptionErrorLabel.setBounds(5, 80, 400, 30);
            characteristicsErrorLabel.setBounds(5, 165, 400, 30);
            if(equipment.isEmpty() || descriptionTextField.getText().trim().isEmpty() || characteriticsTextArea.getText().trim().isEmpty()){

                if(equipment.isEmpty()){
                    String errorMessage = "*Veuillez saisir un numéro pour votre matériel.";
                    JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);

                }

                if (descriptionTextField.getText().trim().isEmpty()) {
                    String errorMessage = "*Veuillez saisir une description pour votre matériel.";
                    JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);



                }


                if (characteriticsTextArea.getText().trim().isEmpty()) {
                    String errorMessage = "*Veuillez saisir des caractéristiques pour votre matériel.";
                    JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);


                }

            }
            else
            {

                try{
                    String sql = "INSERT INTO equipment (equipment_number, designation, characteristic, current_state) VALUES (?,?,?,?)";
                    connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
//                    Statement stat = connection.createStatement();
                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1,equipmentNumberTextField.getText().trim());
                    preparedStatement.setString(2,descriptionTextField.getText().trim());
                    preparedStatement.setString(3,characteriticsTextArea.getText().trim());
                    preparedStatement.setString(4,stateComboBox.getSelectedItem().toString().trim());
                    preparedStatement.executeUpdate();


//                JOptionPane.showMessageDialog(null, "Inserted successfully");
                }
                catch (SQLException exception){

                    if (exception.getErrorCode() == 1062)
                    {
                        // Extract the necessary information to create a readable error message
                        String constraintName = exception.getMessage(); // Constraint name, e.g., "PRIMARY"
                        String columnName = ""; // Column name, e.g., "id"
                        // You can extract the column name from the SQLException message or retrieve it from the table metadata

                        // Create a readable error message
//                    String errorMessage = "Primary key constraint violation: Duplicate value for column " + columnName;

                        String errorMessage = "Ce numéro appartient déjà à un équipment. \n Veuillez saisir un autre numéro.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Duplication de numéro", JOptionPane.WARNING_MESSAGE);
                        // Display the error message to the user (e.g., show a dialog box or set the text of a label)
                        System.out.println(errorMessage);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, exception);
                    }

                }

                showTableData();
                showStateCounts();
            }

        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(equipmentNumberTextField.getText().trim().isEmpty()){
                    String errorMessage = "Veuillez saisir un numéro pour votre matériel.";
                    JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);


                }
                else {
                    int choice = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer?",
                            "Delete Confirmation", JOptionPane.YES_NO_OPTION);

                    // Handle the user's response
                    if(choice == JOptionPane.YES_OPTION){
                        // User clicked "Yes" - proceed with deletion
                        deleteRecord();
                    }
                    showTableData();
                    showStateCounts();
//                    fetchUpdatedStateData();
                }

            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(equipmentNumberTextField.getText().trim().isEmpty() || descriptionTextField.getText().trim().isEmpty() || characteriticsTextArea.getText().trim().isEmpty()){

                    if(equipmentNumberTextField.getText().trim().isEmpty()){
                        String errorMessage = "*Veuillez saisir un numéro pour votre matériel.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);
                    }

                    if (descriptionTextField.getText().trim().isEmpty()) {
                        String errorMessage = "*Veuillez saisir une description pour votre matériel.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);
                    }


                    if (characteriticsTextArea.getText().trim().isEmpty()) {
                        String errorMessage = "*Veuillez saisir des caractéristiques pour votre matériel.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);
                    }

                }
                else {
                    int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to update the record?",
                            "Update Confirmation", JOptionPane.YES_NO_OPTION);

                    // Handle the user's response
                    if (choice == JOptionPane.YES_OPTION) {
                        // User clicked "Yes" - proceed with deletion
                        updateRecord();
                    }
                    showTableData();
                    showStateCounts();

                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearTextField();
            }
        });


        // Search
        JLabel searchLabel = new JLabel("Rechercher ici : ");
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(230, 30));

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                search(searchField.getText().trim());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                search(searchField.getText().trim());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                search(searchField.getText().trim());
            }

            public void search(String query) {
                if (query.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query, 0, 1, 2, 3));
                }
            }
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        searchPanel.setBounds(550, 150, 400, 50);


        add(searchPanel);
    }

    public void showTableData(){

        // Create a custom header renderer

        equipmentTable.getTableHeader().setBackground(Color.decode("#c9e4fe"));
        equipmentTable.getTableHeader().setForeground(Color.decode("#002456"));
        equipmentTable.getTableHeader().setFont(new Font("Times New Roman", Font.CENTER_BASELINE, 16));

        try{
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT equipment_number as 'N° Matériel', designation as 'Désignation', characteristic as 'Caractéristiques', current_state as 'Etat' FROM equipment";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            // Get the ResultSet metadata
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();


            // Create the table model with column names
            DefaultTableModel tableModel = new DefaultTableModel();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++){
                tableModel.addColumn(metaData.getColumnLabel(columnIndex));
            }

            // Set the row height
            equipmentTable.setRowHeight(40);

            // Disable cell editing
            equipmentTable.setDefaultEditor(Object.class, null);

            // Populate the table model with data from the ResultSet
            while(resultSet.next()){
                Object[] rowData = new Object[columnCount];
                for(int columnIndex = 1; columnIndex <= columnCount; columnIndex++){
                    rowData[columnIndex - 1] = resultSet.getObject(columnIndex);

                }
                tableModel.addRow(rowData);

                // Set the table model for the JTable
                equipmentTable.setModel(tableModel);

                sorter = new TableRowSorter<>(tableModel);
                equipmentTable.setRowSorter(sorter);
            }

        }
        catch (Exception exception){
            JOptionPane.showMessageDialog(null, exception);
        }
    }

    public void showStateCounts(){

        try{
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql ="SELECT current_state, count(current_state) FROM equipment group by current_state";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()){
                String state = resultSet.getString("current_state");
                int count = resultSet.getInt("count(current_state)");

                if (state.equals("bon état")){
                    inGoodState.setText("Bon état : " + count);
                    inGoodState.setForeground(Color.decode("#64b9ee"));
                    inGoodState.setFont(new Font("Times New Roman", Font.BOLD, 18));
                }
                if(state.equals("mauvais état")){
                    inDamagedState.setText("Mauvais état : " + count);
                    inDamagedState.setForeground(Color.decode("#edcf2e"));
                    inDamagedState.setFont(new Font("Times New Roman", Font.BOLD, 18));
                }
                if(state.equals("abimé")){inDamagedState.setForeground(Color.orange);
                    inDestroyedState.setText("Abimé : " + count);
                    inDestroyedState.setForeground(Color.decode("#002456"));
                    inDestroyedState.setFont(new Font("Times New Roman", Font.BOLD, 18));
                }
            }

        }
        catch (SQLException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    // Equipment order by state
    public void showOrderedTableData(){
        try{
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT equipment_number as 'N° Matériel', designation as 'Désignation', characteristic as 'Caractéristiques', current_state as 'Etat' FROM equipment order by field(current_state, 'bon état', 'mauvais état', 'abimé');";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            // Get the ResultSet metadata
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();


            // Create the table model with column names
            DefaultTableModel tableModel = new DefaultTableModel();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++){
                tableModel.addColumn(metaData.getColumnLabel(columnIndex));
            }

            // Populate the table model with data from the ResultSet
            while(resultSet.next()){
                Object[] rowData = new Object[columnCount];
                for(int columnIndex = 1; columnIndex <= columnCount; columnIndex++){
                    rowData[columnIndex - 1] = resultSet.getObject(columnIndex);
                }
                tableModel.addRow(rowData);
                // equipmentTable.getColumnModel().getColumn(1).setPreferredWidth(100);

                // Set the table model for the JTable
                equipmentTable.setModel(tableModel);

                sorter = new TableRowSorter<>(tableModel);
                equipmentTable.setRowSorter(sorter);
            }

        }
        catch (Exception exception){
            JOptionPane.showMessageDialog(null, exception);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == stateComboBox)
        {
            System.out.println(stateComboBox.getSelectedItem());
            // System.out.println(comboBox.getSelectedIndex());
        }
    }

    public void fetchStateData(){
        // Data structures to store the extracted values
        List<String> states = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        try{
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql ="SELECT current_state, count(current_state) FROM equipment group by current_state";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();


            ResultSetMetaData metaData = resultSet.getMetaData();


            while (resultSet.next()){
                String state = resultSet.getString("current_state");
                int count = resultSet.getInt("count(current_state)");

                states.add(state);
                counts.add(count);
            }

        }
        catch (SQLException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }


        DefaultPieDataset dataset = new DefaultPieDataset();
        for (int i = 0; i < states.size(); i++){
            dataset.setValue(states.get(i), counts.get(i));
        }

        String pieChartTitle = new String("Etat des matériels");

        pieChart = createPieChart(dataset, pieChartTitle);


        chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(300, 300));
        chartPanel.setBounds(15, 520, 400, 300);

        // Add the chart panel to your GUI or display it in a JFrame
        chartPanel.repaint();
        add(chartPanel);

    }

    public void fetchUpdatedStateData(){
        // Data structures to store the extracted values
        List<String> states = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        try{
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql ="SELECT current_state, count(current_state) FROM equipment group by current_state";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();


            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()){

                String state = resultSet.getString("current_state");
                int count = resultSet.getInt("count(current_state)");

                states.add(state);
                counts.add(count);
            }

        }
        catch (SQLException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (int i = 0; i < states.size(); i++){
            dataset.setValue(states.get(i), counts.get(i));
        }
        pieChart = createPieChart(dataset, "Equipment Status");

        chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(300, 300));
        chartPanel.setBounds(10, 600, 300, 300);
        // Add the chart panel to your GUI or display it in a JFrame

        add(chartPanel);

    }

    // Create pie chart method
    public static JFreeChart createPieChart(PieDataset dataset, String title){
        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true,true,false);

        // Customize the chart
        // Get the plot of the chart
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setCircular(true);

        // Set the font for the title
        Font titleFont = new Font("Times New Roman", Font.BOLD, 18);
        chart.getTitle().setFont(titleFont);

        // Set the font for the section labels
        Font labelFont = new Font("Times New Roman", Font.PLAIN, 12);
        plot.setLabelFont(labelFont);

        // Set custom colors for the pie sections
        String goodColor = "#64b9ee";
        Color forGood = Color.decode(goodColor);
        plot.setSectionPaint("bon état", forGood);
        String damagedColor = "#edcf2e";
        Color forDamaged = Color.decode(damagedColor);
        plot.setSectionPaint("mauvais état", forDamaged);
        String destroyedColor = "#002456";
        Color forDestroyed = Color.decode(destroyedColor);
        plot.setSectionPaint("abimé", forDestroyed);
        // Set custom font for the pie section labels
        plot.setLabelFont(new Font("Times New Roman", Font.PLAIN, 16));
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({2})"));
        return chart;
    }

    private void deleteRecord(){
        try{
            String sql = "DELETE FROM equipment WHERE equipment_number = ?";
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,equipmentNumberTextField.getText().trim());
            preparedStatement.executeUpdate();
        }
        catch (SQLException | HeadlessException exception){
            JOptionPane.showMessageDialog(null, exception, "Try again",JOptionPane.ERROR_MESSAGE);
        }

    }

    private void updateRecord(){
        try{
            String sql = "UPDATE equipment SET designation=?, characteristic=? , current_state=? WHERE equipment_number=? ";
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(4, equipmentNumberTextField.getText().trim());
            preparedStatement.setString(1,descriptionTextField.getText().trim());
            preparedStatement.setString(2,characteriticsTextArea.getText().trim());
            preparedStatement.setString(3,stateComboBox.getSelectedItem().toString());
            preparedStatement.executeUpdate();
        }
        catch (SQLException | HeadlessException exception){
            JOptionPane.showMessageDialog(null, exception);
        }
    }


    // Clear textFields

    public void clearTextField(){
        equipmentNumberTextField.setText("");
        descriptionTextField.setText("");
        characteriticsTextArea.setText("");
    }

    static class CenterTextRenderer extends DefaultTableCellRenderer{
        public CenterTextRenderer(){
            setHorizontalAlignment(SwingConstants.CENTER);
        }
    }

}



