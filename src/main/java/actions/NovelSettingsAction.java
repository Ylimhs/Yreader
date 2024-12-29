package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import org.jetbrains.annotations.NotNull;
import config.NovelReaderConfigurable;

public class NovelSettingsAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ShowSettingsUtil.getInstance().showSettingsDialog(
            e.getProject(), 
            NovelReaderConfigurable.class
        );
    }
} 