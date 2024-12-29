package config;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

@State(
    name = "NovelReaderConfig",
    storages = {@Storage("novelReaderPlugin.xml")}
)
public class NovelReaderConfig implements PersistentStateComponent<NovelReaderConfig.State> {
    private State myState = new State();

    public static class State {
        public int charsPerLine = 20;
        public int linesPerPage = 3;
        public List<String> formattedContent = new ArrayList<>();
        public int currentPage = 0;
        public String lastFilePath = "";
        public boolean isHidden = false;
        public int lastInsertStart = -1;
        public int lastInsertEnd = -1;
    }

    public static NovelReaderConfig getInstance(Project project) {
        return project.getService(NovelReaderConfig.class);
    }

    @Nullable
    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

    public int getCharsPerLine() {
        return myState.charsPerLine;
    }

    public void setCharsPerLine(int charsPerLine) {
        myState.charsPerLine = charsPerLine;
    }

    public int getLinesPerPage() {
        return myState.linesPerPage;
    }

    public void setLinesPerPage(int linesPerPage) {
        myState.linesPerPage = linesPerPage;
    }

    public List<String> getFormattedContent() {
        return myState.formattedContent;
    }

    public void setFormattedContent(List<String> content) {
        myState.formattedContent = new ArrayList<>(content);
    }

    public int getCurrentPage() {
        return myState.currentPage;
    }

    public void setCurrentPage(int page) {
        myState.currentPage = page;
    }

    public String getLastFilePath() {
        return myState.lastFilePath;
    }

    public void setLastFilePath(String path) {
        myState.lastFilePath = path;
    }

    public boolean isHidden() {
        return myState.isHidden;
    }

    public void setHidden(boolean hidden) {
        myState.isHidden = hidden;
    }

    public int getLastInsertStart() {
        return myState.lastInsertStart;
    }

    public void setLastInsertStart(int start) {
        myState.lastInsertStart = start;
    }

    public int getLastInsertEnd() {
        return myState.lastInsertEnd;
    }

    public void setLastInsertEnd(int end) {
        myState.lastInsertEnd = end;
    }
} 