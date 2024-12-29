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
 * 下一页动作处理类
 * 处理小说内容的下一页显示
 * 支持键盘快捷键和鼠标滚轮
 */
public class NextPageAction extends AnAction {
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

        service.debounceNextPage(() -> {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                String content = service.getCurrentPage();
                currentDoc.replaceString(lastRange.getStartOffset(), lastRange.getEndOffset(), content);
                service.setLastInsertRange(currentDoc, lastRange.getStartOffset(), 
                    lastRange.getStartOffset() + content.length());
            });
        });
    }
} 