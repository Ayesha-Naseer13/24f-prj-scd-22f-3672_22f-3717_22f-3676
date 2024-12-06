package pl;

import dal.DataAccessLayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class POSUI {
    private DataAccessLayer dataAccessLayer;

    public POSUI() throws Exception {
        dataAccessLayer = new DataAccessLayer();
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        // Create the frame with size 500x300
        JFrame frame = new JFrame("POS Finder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);

        // Create UI elements
        JLabel label = new JLabel("Enter Arabic Word:");
        JTextField textField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        // Table with scroll pane for Stem Word and POS details
        JTable posTable = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(posTable);
        tableScrollPane.setPreferredSize(new Dimension(450, 200)); // Adjusted size for the JTable

        // Layout setup
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(label);
        inputPanel.add(textField);
        inputPanel.add(searchButton);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Stem Word and POS"));
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Add components to frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(tablePanel, BorderLayout.CENTER);

        // Button click event
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String arabicWord = textField.getText().trim();
                if (!arabicWord.isEmpty()) {
                    try {
                        // Fetch POS details
                        List<String[]> posDetails = dataAccessLayer.getPosDetails(arabicWord);
                        if (posDetails.isEmpty()) {
                            JOptionPane.showMessageDialog(frame, "No results found for the word: " + arabicWord, "No Results", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            // Prepare data for the JTable
                            String[] columnNames = {"Stem Word", "POS"};
                            Object[][] rowData = new Object[posDetails.size()][2];

                            for (int i = 0; i < posDetails.size(); i++) {
                                rowData[i][0] = posDetails.get(i)[1]; // Stem Word
                                rowData[i][1] = posDetails.get(i)[2]; // POS
                            }

                            // Update the JTable
                            posTable.setModel(new javax.swing.table.DefaultTableModel(rowData, columnNames));
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please enter a word to search.", "Input Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Display the frame
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            new POSUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
