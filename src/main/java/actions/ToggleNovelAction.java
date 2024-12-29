package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import service.NovelService;

/**
 * 内容显示切换动作处理类
 * 负责处理小说内容的显示和隐藏
 */
public class ToggleNovelAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        
        if (project == null || editor == null) {
            return;
        }

        // 确保 NovelService 已初始化
        NovelService.getInstance().initializeWithProject(project);

        NovelService service = NovelService.getInstance();
        Document currentDoc = editor.getDocument();
        Document lastDoc = service.getLastDocument();
        TextRange lastRange = service.getLastInsertRange();

        if (lastDoc != currentDoc || lastRange == null) {
            return;
        }

        WriteCommandAction.runWriteCommandAction(project, () -> {
            service.toggleHidden();
            String content = service.getCurrentPage();
            int startOffset = lastRange.getStartOffset();
            currentDoc.replaceString(startOffset, lastRange.getEndOffset(), content);
            service.setLastInsertRange(currentDoc, startOffset, startOffset + content.length());
        });
    }
} 