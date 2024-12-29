package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import service.NovelService;

/**
 * 小说插入动作处理类
 * 负责处理小说文件的选择和内容插入
 */
public class InsertNovelAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        
        if (project == null || editor == null) {
            return;
        }

        // 初始化 NovelService
        NovelService.getInstance().initializeWithProject(project);

        FileChooserDescriptor descriptor = new FileChooserDescriptor(
            true, false, false, false, false, false)
            .withFileFilter(file -> file.getExtension() != null && file.getExtension().equals("txt"));

        VirtualFile file = FileChooser.chooseFile(descriptor, project, null);
        if (file != null) {
            NovelService.getInstance().loadNovelFile(file.getPath());
            insertCurrentPage(project, editor);
        }
    }

    private void insertCurrentPage(Project project, Editor editor) {
        String content = NovelService.getInstance().getCurrentPage();
        Document document = editor.getDocument();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            int offset = editor.getCaretModel().getOffset();
            document.insertString(offset, content);
            NovelService.getInstance().setLastInsertRange(
                document, 
                offset, 
                offset + content.length()
            );
        });
    }
} 