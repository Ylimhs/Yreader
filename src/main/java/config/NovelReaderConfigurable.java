package config;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import service.NovelService;

import javax.swing.*;
import java.awt.*;

public class NovelReaderConfigurable implements Configurable {
    private JPanel myPanel;
    private JSpinner charsPerLineSpinner;
    private JSpinner linesPerPageSpinner;
    private final Project project;

    public NovelReaderConfigurable(Project project) {
        this.project = project;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Novel Reader";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        myPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        NovelReaderConfig config = NovelReaderConfig.getInstance(project);

        // 每行字数设置
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 5, 5, 5);
        myPanel.add(new JLabel("每行字数:"), c);

        c.gridx = 1;
        charsPerLineSpinner = new JSpinner(new SpinnerNumberModel(
            config.getCharsPerLine(), 10, 100, 1));
        myPanel.add(charsPerLineSpinner, c);

        // 每页行数设置
        c.gridx = 0;
        c.gridy = 1;
        myPanel.add(new JLabel("每页行数:"), c);

        c.gridx = 1;
        linesPerPageSpinner = new JSpinner(new SpinnerNumberModel(
            config.getLinesPerPage(), 1, 20, 1));
        myPanel.add(linesPerPageSpinner, c);

        return myPanel;
    }

    @Override
    public boolean isModified() {
        NovelReaderConfig config = NovelReaderConfig.getInstance(project);
        return config.getCharsPerLine() != (int) charsPerLineSpinner.getValue() ||
               config.getLinesPerPage() != (int) linesPerPageSpinner.getValue();
    }

    @Override
    public void apply() {
        NovelReaderConfig config = NovelReaderConfig.getInstance(project);
        config.setCharsPerLine((int) charsPerLineSpinner.getValue());
        config.setLinesPerPage((int) linesPerPageSpinner.getValue());
        
        // 更新服务中的设置
        NovelService.getInstance().updateSettings(
            config.getCharsPerLine(),
            config.getLinesPerPage()
        );
    }

    @Override
    public void reset() {
        NovelReaderConfig config = NovelReaderConfig.getInstance(project);
        charsPerLineSpinner.setValue(config.getCharsPerLine());
        linesPerPageSpinner.setValue(config.getLinesPerPage());
    }
} 