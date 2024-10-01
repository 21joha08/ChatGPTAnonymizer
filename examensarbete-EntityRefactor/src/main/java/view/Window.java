package view;

import controller.WordController;
import model.TypeOfWord;
import model.domain.AbstractDomain;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.w3c.dom.css.RGBColor;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Window extends JFrame {

    private JTextArea editorPane;
    private JTextArea selectedText;
    private JTextArea chatResponse;

    private JTextArea chatPrompt;
    private WordController wordController;

    private String placeHolderPrompt = "Enter your prompt here";

    private String placeHolderEditor = "Enter your text here";

    private String placeHolderResponse = "ChatGPTs response";


    public Window(WordController wordController) {
        this.wordController = wordController;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 1000);
        setLocationRelativeTo(null);

        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        editorPane = createEditorPane();
        JScrollPane editorScrollPane = new JScrollPane(editorPane);

        JPanel chatPanel = createChatPanel(); // This panel includes both prompt and response

        JPanel selectedWordsPanel = createSelectedWordsPanel();

        JMenuBar menuBar = new JMenuBar();

        buildMenu(menuBar);
        setJMenuBar(menuBar);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        gbc.gridx = 0; // Editor pane is now on the left
        gbc.weightx = 0.33;
        getContentPane().add(editorScrollPane, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.16;
        getContentPane().add(selectedWordsPanel, gbc);

        gbc.gridx = 2; // Chat panel is now on the right
        gbc.weightx = 0.33;
        getContentPane().add(chatPanel, gbc);

        setVisible(true);
    }


    private JTextArea createEditorPane() {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(true);
        textArea.setBackground(new Color(250, 249, 246));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int offset = editorPane.viewToModel2D(e.getPoint());
                    String text = editorPane.getText();
                    selectWordOrBracketedExpression(offset, text);
                }
            }
        });
        return textArea;
    }
    private JPanel createChatPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        chatPrompt = new JTextArea(3, 50); // Define rows and columns
        chatPrompt.setLineWrap(true);
        chatPrompt.setWrapStyleWord(true);
        chatPrompt.setBackground(new Color(250, 249, 246));

        chatResponse = new JTextArea(10, 50); // Give more initial rows to chatResponse
        chatResponse.setEditable(false);
        chatResponse.setLineWrap(true);
        chatResponse.setWrapStyleWord(true);
        chatResponse.setBackground(new Color(250, 249, 246));
        JButton clearButton = new JButton("Clear");
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            String question = chatPrompt.getText();
            chatResponse.setText("Loading...");
            // Invoke the processing on separate thread
            new Thread(() -> sendPrompt(question)).start();
        });

        clearButton.addActionListener(e -> setPlaceholderText(chatPrompt, placeHolderPrompt));

        // Add chatPrompt to the panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.2; // Less weight relative to the response area
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 2; // Span both columns
        panel.add(new JScrollPane(chatPrompt), gbc);

        // Add clearButton to the panel
        gbc.gridx = 0; // Position on the left
        gbc.gridy = 1;
        gbc.weightx = 0; // Don't allow horizontal expansion
        gbc.weighty = 0; // Less weight relative to the response area
        gbc.fill = GridBagConstraints.HORIZONTAL; // Allow button to resize horizontally
        gbc.gridwidth = 1; // Reset grid width
        panel.add(clearButton, gbc);

        // Add sendButton to the panel
        gbc.gridx = 1; // Position on the right
        gbc.weightx = 0; // Don't allow horizontal expansion
        gbc.fill = GridBagConstraints.HORIZONTAL; // Allow button to resize horizontally
        panel.add(sendButton, gbc);

        // Add chatResponse to the panel
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0.8; // Most of the vertical space goes to the response area
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 2; // Span both columns
        panel.add(new JScrollPane(chatResponse), gbc);

        setPlaceholderText(editorPane, placeHolderEditor);
        setPlaceholderText(chatResponse, placeHolderResponse);
        setPlaceholderText(chatPrompt, placeHolderPrompt);

        return panel;
    }

    public void setPlaceholderText(JTextArea chatPrompt, String placeholder) {
        chatPrompt.setText(placeholder);
        chatPrompt.setForeground(Color.GRAY);
        // Add a FocusListener to handle focus events
        chatPrompt.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                removePlaceholderText(chatPrompt, placeholder);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (chatPrompt.getText().isEmpty()) {
                    chatPrompt.setText(placeholder);
                    chatPrompt.setForeground(Color.GRAY); // Set placeholder text color
                }
            }
        });
    }

    private void removePlaceholderText(JTextArea textArea, String placeholder) {
        if (textArea.getText().equals(placeholder)) {
            textArea.setText("");
            textArea.setForeground(Color.BLACK);
        }
    }





    private JPanel createSelectedWordsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Anonymized words", JLabel.CENTER);
        label.setFont(new Font("Serif", Font.BOLD, 14));

        selectedText = new JTextArea();
        selectedText.setEditable(false);
        selectedText.setLineWrap(true);
        selectedText.setWrapStyleWord(true);

        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(selectedText), BorderLayout.CENTER);
        return panel;
    }

    private void buildMenu(JMenuBar menuBar) {

        JButton uploadButton = new JButton("Upload Text");
        uploadButton.addActionListener(e -> openFile());
        menuBar.add(uploadButton);

        JButton selectButton = new JButton("Undo");
        selectButton.addActionListener(e -> undo());
        menuBar.add(selectButton);

        JMenu dropDown = new JMenu("Type of word");
        Icon dropdownIcon = UIManager.getIcon("Menu.arrowIcon"); // Standard icon to indicate a dropdown
        if (dropdownIcon != null)
            dropDown.setIcon(dropdownIcon);

        String[] types = {"Firstname", "Lastname", "Companyname", "SSN", "Amount", "Phonenumber", "Email", "Location"};
        for (String type : types) {
            JMenuItem menuItem = new JMenuItem(type);
            menuItem.addActionListener(e -> anonymize(TypeOfWord.valueOf(type.toUpperCase())));
            dropDown.add(menuItem);
        }
        menuBar.add(dropDown);


    }


    private void selectWordOrBracketedExpression(int offset, String text) {
        if (offset >= text.length() || Character.isWhitespace(text.charAt(offset))) return;
        int start = offset;
        int end = offset;
        // Expand to the left
        while (start > 0 && !Character.isWhitespace(text.charAt(start - 1))) {
            if (text.charAt(start - 1) == '[') {
                start--;
                break;
            }
            start--;
        }
        // Expand to the right
        while (end < text.length() && !Character.isWhitespace(text.charAt(end))) {
            if (text.charAt(end) == ']') {
                end++;
                break;
            }
            end++;
        }
        if (end > start && isPunctuation(text.charAt(end - 1)) && (end == text.length() || Character.isWhitespace(text.charAt(end)))) {
            end--;
        }
        editorPane.setSelectionStart(start);
        editorPane.setSelectionEnd(end);
    }

    private boolean isPunctuation(char c) {
        return c == '.' || c == ',' || c == ';' || c == '!';
    }

    public void sendPrompt(String question) {
        wordController.sendPrompt(question);
        // Update UI Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            chatResponse.setText(wordController.deAnonymizeChatResponse());
        });
    }


    public void anonymize(TypeOfWord typeOfWord) {
        wordController.setUploadedFile(editorPane.getText());
        String selectedWord = editorPane.getSelectedText();
        if (selectedWord == null || selectedWord.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a word to anonymize.",
                    "No word selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        selectedWord = selectedWord.trim();
        // Check if selection is a complete word
        try {
            int start = editorPane.getSelectionStart();
            int end = editorPane.getSelectionEnd();
            String text = editorPane.getText();
            // Check characters before and after the selected word
            if ((start == 0 || !Character.isLetterOrDigit(text.charAt(start - 1))) &&
                    (end == text.length() || !Character.isLetterOrDigit(text.charAt(end)))) {
                // a complete word
                AbstractDomain domain = wordController.createDomain(typeOfWord, selectedWord);
                if (domain != null) {
                    String updatedText = wordController.replaceWord(domain);
                    editorPane.setText(updatedText);
                    updateSelectedText();
                    wordController.setUploadedFile(updatedText);
                } else {
                    JOptionPane.showMessageDialog(this, "Unknown type of word.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Not a complete word
                JOptionPane.showMessageDialog(this, "Please select a complete word, not part of a word.",
                        "Incomplete Selection", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error processing your request.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void undo() {
        String key = editorPane.getSelectedText();
        if (key == null || key.trim().isEmpty() ) {
            // Handle the case where no word is selected
            JOptionPane.showMessageDialog(this, "Please select a word to undo.", "No Word Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        key = key.trim();
        if (key.startsWith("[") && key.endsWith("]")) {
            wordController.deAnonymizeWord(key);
            editorPane.setText(wordController.getUploadedFile());
            wordController.removeSelectedWords(key);
            updateSelectedText();
        }
        else {
            JOptionPane.showMessageDialog(this, "Selected text is not in the expected format.",
                    "Invalid Selection", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSelectedText(){
        StringBuilder sb = new StringBuilder();
        List<List<AbstractDomain>> domains = new ArrayList<>();
        domains.add(wordController.getFirstName());
        domains.add(wordController.getLastNames());
        domains.add(wordController.getCompanyNames());
        domains.add(wordController.getPhoneNumbers());
        domains.add(wordController.getSSNs());
        domains.add(wordController.getAmounts());
        domains.add(wordController.getLocations());
        domains.add(wordController.getEmails());

        for(List<AbstractDomain> domain: domains) {
            sb = updateList(domain, sb);
        }
        selectedText.setText(sb.toString());
    }


    public StringBuilder updateList(List<AbstractDomain> domains, StringBuilder stringBuilder){
        if(domains.size() == 0)
            return stringBuilder;
        for(AbstractDomain domain: domains) {
            stringBuilder.append(domain.toString()).append(" : ").append(domain.getRealValue()).append("\n");
        }
        return stringBuilder;
    }
    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Word Documents", "docx");
        fileChooser.setFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (FileInputStream fis = new FileInputStream(selectedFile)) {
                XWPFDocument document = new XWPFDocument(fis);
                StringBuilder text = new StringBuilder();
                for (XWPFParagraph para : document.getParagraphs()) {
                    text.append(para.getText()).append("\n");
                }
                wordController.setUploadedFile(text.toString());
                editorPane.setText(text.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


}
