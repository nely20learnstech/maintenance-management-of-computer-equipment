import javax.swing.*;
import java.awt.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Arrays;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PrestationSection extends JPanel {

    JTable prestationTable;
    private JTextField searchTextField;

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    final String DB_URL = "jdbc:mysql://localhost/maintenance_management?serverTimezone=UTC";
    final String USERNAME = "your_username";
    final String PASSWORD = "your_password";
    JLabel totalValueLabel;
    DefaultTableModel tableModel;
    JLabel year;
    JScrollPane scrollPane;

    PrestationSection() {

        JLabel prestationLabel = new JLabel();
        prestationLabel.setText("Etat des prestations");

        prestationLabel.setBounds(10, 0, 900, 100);
        prestationLabel.setForeground(Color.decode("#002456"));
        prestationLabel.setFont(new Font("Times New Roman", Font.PLAIN, 70));

        add(prestationLabel, BorderLayout.NORTH);


        searchTextField = new JTextField();
        searchTextField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        searchTextField.setBounds(25, 130, 200, 30);
        add(searchTextField, BorderLayout.NORTH);

        JButton searchButton = new JButton();
        searchButton.setText("Rechercher");
        searchButton.setBackground(Color.decode("#64b9ee"));
        searchButton.setForeground(Color.white);
        searchButton.setBounds(270, 130, 120, 30);
        add(searchButton, BorderLayout.NORTH);

        totalValueLabel = new JLabel();

        year = new JLabel();
        searchButton.addActionListener(e -> {
            clearTableAndTotal();
            String search = searchTextField.getText().trim();
            if (!search.isEmpty()) {

                try {
                    int value = Integer.parseInt(searchTextField.getText().trim());
                    year.setText("Année : " + value);
                    getPrestationData();
                    prestationTotal();

                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(null, "Veuillez saisir une année valide!", "Année non valide", JOptionPane.ERROR_MESSAGE);
                    return;
                }

            } else {
                clearTableAndTotal();
                JOptionPane.showMessageDialog(null, "Veuillez remplir le champ.", "Champ vide", JOptionPane.ERROR_MESSAGE);
            }

        });

        // Set the table model for the JTable
        tableModel = new DefaultTableModel();
        prestationTable = new JTable(tableModel);
        prestationTable.setDefaultEditor(Object.class, null);
        // Center Text Renderer
        prestationTable.setDefaultRenderer(Object.class, new EquipmentSection.CenterTextRenderer());

        // Total labels
        totalValueLabel.setBounds(25, 620, 200, 50);
        year.setBounds(25, 170, 200, 50);
        year.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        add(year);
        setSize(1200, 800);

        JButton downloadButton = new JButton("Télécharger en PDF");
        downloadButton.setBackground(Color.decode("#002456"));
        downloadButton.setForeground(Color.white);
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = new File("prestation.pdf");
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException exception) {
                    exception.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Le fichier ne peut pas être téléchargé", "Erreur de téléchargement", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        downloadButton.setBounds(25, 680, 200, 30);
        add(downloadButton);

        prestationTable.setModel(tableModel);

        scrollPane = new JScrollPane(prestationTable);
        scrollPane.setBounds(25, 230, 600, 400);
        scrollPane.setBackground(Color.white);
        scrollPane.setVisible(false);
        add(scrollPane, BorderLayout.SOUTH);
        add(totalValueLabel, BorderLayout.BEFORE_LINE_BEGINS);

        setLayout(null);
        setVisible(true);
    }

    public void getPrestationData() {
//        clearTable();
        scrollPane.setVisible(true);
        prestationTable.getTableHeader().setBackground(Color.decode("#c9e4fe"));
        prestationTable.getTableHeader().setForeground(Color.decode("#002456"));
        prestationTable.getTableHeader().setFont(new Font("Times New Roman", Font.CENTER_BASELINE, 16));

        prestationTable.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        prestationTable.setRowHeight(40);

        try {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT maintainer.maintainer_number as 'N° Intervenant', maintainer_lastname as 'Nom', maintainer_firstname as 'Prénom(s)', " +
                    "sum(salary * duration) as Prestation FROM maintenance3 JOIN maintainer " +
                    "ON maintainer.maintainer_number = maintenance3.maintainer_number WHERE year(intervention_date) = ? Group by maintainer.maintainer_number";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, searchTextField.getText().trim());
            resultSet = preparedStatement.executeQuery();

            // Get the ResultSet metadata
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Create the table model with column names

            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                tableModel.addColumn(metaData.getColumnLabel(columnIndex));
            }

            // Populate the table model with data from the ResultSet
            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    rowData[columnIndex - 1] = resultSet.getObject(columnIndex);
                }
                System.out.println(Arrays.toString(rowData));
                tableModel.addRow(rowData);

            }

        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null, exception);
        }


        // Generate the prestation PDF
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("prestation.pdf"));
            document.open();


            // Add additional information to the PDF
            Paragraph paragraph = new Paragraph("ETAT DES PRESTATIONS ", new com.lowagie.text.Font(Font.CENTER_BASELINE, 18));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            Paragraph yearParagraph = new Paragraph(year.getText(), new com.lowagie.text.Font(Font.CENTER_BASELINE, 14));
            yearParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(yearParagraph);

            document.add(Chunk.NEWLINE);

            // Create the PDF table
            PdfPTable pdfTable = new PdfPTable(prestationTable.getColumnCount());
            pdfTable.setWidthPercentage(100);


            // Set column headers
            for (int i = 0; i < prestationTable.getColumnCount(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(prestationTable.getColumnName(i)));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(10);
                pdfTable.addCell(cell);
            }

            // Set table data
            for (int i = 0; i < prestationTable.getRowCount(); i++) {
                for (int j = 0; j < prestationTable.getColumnCount(); j++) {
                    PdfPCell cell = new PdfPCell(new Phrase(prestationTable.getValueAt(i, j).toString()));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(10);
                    pdfTable.addCell(cell);
                }
            }

            // Add the PDF table to the document
            document.add(pdfTable);
            String total = prestationTotal();
            Paragraph totalParagraph = new Paragraph(total);
            totalParagraph.setAlignment(Element.ALIGN_JUSTIFIED);
            document.add(totalParagraph);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

    }

    public String prestationTotal() {
        // Prestation Total

        String total = new String();
        try {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT sum(salary * duration) \n" +
                    "FROM maintenance3 \n" +
                    "JOIN maintainer ON maintainer.maintainer_number = maintenance3.maintainer_number \n" +
                    "WHERE year(intervention_date) = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, searchTextField.getText());
            resultSet = preparedStatement.executeQuery();

            // Get the ResultSet metadata
            ResultSetMetaData metaData = resultSet.getMetaData();

            if (resultSet.next()) {
                int totalPrestation = resultSet.getInt(1);

                totalValueLabel.setText("MONTANT TOTAL : " + totalPrestation + "Ar");
                total = "MONTANT TOTAL : " + totalPrestation + "Ar";
            }

        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null, exception);
        } finally {
            closeResultSet();
            closePreparedStatement();
        }

        return total;
    }

    private void closeResultSet() {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                // Ignore
            }
        }
    }

    private void clearTableAndTotal() {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
    }

    private void closePreparedStatement() {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                // Ignore
            }
        }
    }

    private void clearTable() {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
    }

    public void clearSearchTextField() {
        searchTextField.setText("");
    }

}


