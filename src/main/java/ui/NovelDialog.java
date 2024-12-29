package ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NovelDialog extends DialogWrapper {
    private TextFieldWithBrowseButton txtPath;
    private JTextArea previewArea;
    private List<String> formattedContent;
    private int currentPage = 0;
    private static final int CHARS_PER_LINE = 20;
    private static final int LINES_PER_PAGE = 3;

    public NovelDialog(Project project) {
        super(project);
        formattedContent = new ArrayList<>();
        init();
        setTitle("选择小说文件");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());
        
        // 文件选择区域
        JPanel topPanel = new JPanel(new BorderLayout());
        txtPath = new TextFieldWithBrowseButton();
        txtPath.addBrowseFolderListener("选择小说文件", "请选择TXT文件", null, 
            new com.intellij.openapi.fileChooser.FileChooserDescriptor(
                true, false, false, false, false, false));
        topPanel.add(txtPath, BorderLayout.CENTER);
        
        // 预览区域
        previewArea = new JTextArea(LINES_PER_PAGE, CHARS_PER_LINE);
        previewArea.setEditable(false);
        previewArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton prevButton = new JButton("上一页");
        JButton nextButton = new JButton("下一页");
        JButton loadButton = new JButton("加载文件");
        
        prevButton.addActionListener(e -> showPreviousPage());
        nextButton.addActionListener(e -> showNextPage());
        loadButton.addActionListener(e -> loadNovelFile());
        
        buttonPanel.add(prevButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(nextButton);
        
        dialogPanel.add(topPanel, BorderLayout.NORTH);
        dialogPanel.add(new JScrollPane(previewArea), BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return dialogPanel;
    }

    private void loadNovelFile() {
        String path = txtPath.getText();
        formattedContent.clear();
        currentPage = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            StringBuilder currentLine = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                char ch = (char) c;
                if (ch == '\n' || ch == '\r') continue;
                
                currentLine.append(ch);
                if (currentLine.length() >= CHARS_PER_LINE) {
                    formattedContent.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
            }
            if (currentLine.length() > 0) {
                formattedContent.add(currentLine.toString());
            }
            showCurrentPage();
        } catch (IOException e) {
            previewArea.setText("无法读取文件: " + e.getMessage());
        }
    }

    private void showCurrentPage() {
        StringBuilder display = new StringBuilder();
        int startIdx = currentPage * LINES_PER_PAGE;
        
        for (int i = 0; i < LINES_PER_PAGE; i++) {
            int idx = startIdx + i;
            if (idx < formattedContent.size()) {
                display.append(formattedContent.get(idx));
            }
            display.append("\n");
        }
        
        previewArea.setText(display.toString());
    }

    private void showNextPage() {
        if ((currentPage + 1) * LINES_PER_PAGE < formattedContent.size()) {
            currentPage++;
            showCurrentPage();
        }
    }

    private void showPreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            showCurrentPage();
        }
    }

    public String getSelectedText() {
        return previewArea.getText();
    }
} 