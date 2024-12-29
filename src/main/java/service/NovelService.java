package service;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.intellij.openapi.project.Project;
import config.NovelReaderConfig;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.fileEditor.FileDocumentManager;

/**
 * 小说阅读器核心服务类
 * 负责小说内容的管理、格式化和状态维护
 */
public class NovelService {
    private static NovelService instance;
    private List<String> formattedContent;
    private int currentPage = 0;
    private int charsPerLine = 20;
    private int linesPerPage = 3;
    
    // 记录最后插入的位置
    private int lastInsertStart = -1;
    private int lastInsertEnd = -1;
    private Document lastDocument = null;
    
    // 防抖动相关
    private Timer debounceTimer;
    private static final long DEBOUNCE_DELAY = 100; // 100ms延迟
    private boolean isProcessing = false;

    private boolean isHidden = false;
    private String lastContent = "";

    private static final String NOVEL_PREFIX = "/**\n * ";
    private static final String NOVEL_LINE_PREFIX = " * ";
    private static final String NOVEL_SUFFIX = " */\n";
    private static final String HIDDEN_CONTENT = "// TODO: Something interesting...\n";

    private Project project;

    private NovelService() {
        formattedContent = new ArrayList<>();
        debounceTimer = new Timer(true);
    }

    public static NovelService getInstance() {
        if (instance == null) {
            instance = new NovelService();
        }
        return instance;
    }

    public void initializeWithProject(Project project) {
        if (this.project == project) {
            return; // 如果是同一个项目，不需要重新初始化
        }
        
        this.project = project;
        // 从配置中恢复状态
        NovelReaderConfig config = NovelReaderConfig.getInstance(project);
        formattedContent = new ArrayList<>(config.getFormattedContent());
        currentPage = config.getCurrentPage();
        isHidden = config.isHidden();
        lastInsertStart = config.getLastInsertStart();
        lastInsertEnd = config.getLastInsertEnd();
        charsPerLine = config.getCharsPerLine();
        linesPerPage = config.getLinesPerPage();
        
        // 恢复文档引用
        if (lastInsertStart >= 0 && lastInsertEnd >= 0) {
            String lastFilePath = config.getLastFilePath();
            if (!lastFilePath.isEmpty()) {
                VirtualFile file = LocalFileSystem.getInstance().findFileByPath(lastFilePath);
                if (file != null) {
                    FileDocumentManager documentManager = FileDocumentManager.getInstance();
                    lastDocument = documentManager.getDocument(file);
                }
            }
        }
    }

    private void saveState() {
        if (project == null) return;
        
        NovelReaderConfig config = NovelReaderConfig.getInstance(project);
        config.setFormattedContent(formattedContent);
        config.setCurrentPage(currentPage);
        config.setHidden(isHidden);
        config.setLastInsertStart(lastInsertStart);
        config.setLastInsertEnd(lastInsertEnd);
    }

    public void setLastInsertRange(Document document, int start, int end) {
        this.lastDocument = document;
        this.lastInsertStart = start;
        this.lastInsertEnd = end;
        saveState();
    }

    public TextRange getLastInsertRange() {
        if (lastInsertStart >= 0 && lastInsertEnd >= 0) {
            return new TextRange(lastInsertStart, lastInsertEnd);
        }
        return null;
    }

    public Document getLastDocument() {
        return lastDocument;
    }

    /**
     * 加载小说文件
     * 将文件内容按照指定字数分行存储
     *
     * @param path 小说文件路径
     */
    public void loadNovelFile(String path) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            formattedContent.clear();
            currentPage = 0;
            formatContent(content);
            
            if (project != null) {
                NovelReaderConfig config = NovelReaderConfig.getInstance(project);
                config.setLastFilePath(path);
                saveState();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前页面内容
     * 根据当前状态返回格式化的内容或隐藏提示
     *
     * @return 格式化后的内容
     */
    public String getCurrentPage() {
        if (isHidden) {
            return HIDDEN_CONTENT;
        }

        StringBuilder display = new StringBuilder();
        display.append(NOVEL_PREFIX);
        
        int startIdx = currentPage * linesPerPage;
        for (int i = 0; i < linesPerPage; i++) {
            int idx = startIdx + i;
            if (idx < formattedContent.size()) {
                if (i > 0) {
                    display.append(NOVEL_LINE_PREFIX);
                }
                display.append(formattedContent.get(idx));
                display.append("\n");
            }
        }
        
        display.append(NOVEL_SUFFIX);
        lastContent = display.toString();
        return lastContent;
    }

    /**
     * 带防抖动的下一页处理
     * 避免快速翻页时的性能问题
     *
     * @param callback 页面更新回调
     */
    public void debounceNextPage(Runnable callback) {
        if (isProcessing || isHidden) return;
        
        if ((currentPage + 1) * linesPerPage < formattedContent.size()) {
            if (debounceTimer != null) {
                debounceTimer.cancel();
            }
            debounceTimer = new Timer(true);
            
            debounceTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isProcessing = true;
                    currentPage++;
                    callback.run();
                    isProcessing = false;
                }
            }, DEBOUNCE_DELAY);
        }
    }

    /**
     * 带防抖动的上一页处理
     * 避免快速翻页时的性能问题
     *
     * @param callback 页面更新回调
     */
    public void debouncePreviousPage(Runnable callback) {
        if (isProcessing || isHidden) return;
        
        if (currentPage > 0) {
            if (debounceTimer != null) {
                debounceTimer.cancel();
            }
            debounceTimer = new Timer(true);
            
            debounceTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isProcessing = true;
                    currentPage--;
                    callback.run();
                    isProcessing = false;
                }
            }, DEBOUNCE_DELAY);
        }
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void toggleHidden() {
        isHidden = !isHidden;
        saveState();
    }

    /**
     * 更新内容并维护插入范围
     *
     * @param document 当前文档
     * @param range 更新范围
     * @return 更新后的内容
     */
    public String updateContent(Document document, TextRange range) {
        String content = getCurrentPage();
        int startOffset = range.getStartOffset();
        setLastInsertRange(document, startOffset, startOffset + content.length());
        return content;
    }

    public void updateSettings(int charsPerLine, int linesPerPage) {
        this.charsPerLine = charsPerLine;
        this.linesPerPage = linesPerPage;
        
        // 重新格式化内容
        if (!formattedContent.isEmpty()) {
            String currentContent = String.join("", formattedContent);
            formattedContent.clear();
            formatContent(currentContent);
        }
    }

    private void formatContent(String content) {
        StringBuilder currentLine = new StringBuilder();
        for (char ch : content.toCharArray()) {
            if (ch == '\n' || ch == '\r') continue;
            
            currentLine.append(ch);
            if (currentLine.length() >= charsPerLine) {
                formattedContent.add(currentLine.toString());
                currentLine = new StringBuilder();
            }
        }
        if (currentLine.length() > 0) {
            formattedContent.add(currentLine.toString());
        }
    }
} 