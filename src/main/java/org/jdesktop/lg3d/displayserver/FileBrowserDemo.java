package org.jdesktop.lg3d.displayserver;

import java.io.*;
import java.util.*;
import org.jdesktop.lg3d.wg.*;
import org.jdesktop.lg3d.wg.components.*;
import org.jdesktop.lg3d.sg.*;
import java.awt.*;

/**
 * File Browser Demo - 3D file navigation.
 */
public class FileBrowserDemo {

    private Frame3D mainFrame;
    private String currentPath;
    private List<File> currentFiles;

    public static void main(String[] args) {
        System.out.println("Starting LG3D File Browser Demo...");
        System.setProperty("java.awt.headless", "false");
        new FileBrowserDemo().start();
    }

    public void start() {
        mainFrame = new Frame3D("LG3D File Browser");
        mainFrame.setSize(4.5f, 3.0f);

        Container3D content = mainFrame.getContentPane();
        content.setLayout(new BorderLayout3D());

        createToolbar(content);

        Component3D mainPanel = new Container3D("MainPanel");
        mainPanel.setSize(4.3f, 2.3f);
        content.add(mainPanel, BorderLayout3D.CENTER);

        SplitPane3D split = new SplitPane3D(SplitPane3D.Orientation.HORIZONTAL);
        split.setSize(4.3f, 2.3f);

        Component3D leftPanel = createDirectoryTree();
        split.setLeftComponent(leftPanel);

        Component3D rightPanel = createFileList();
        split.setRightComponent(rightPanel);

        mainPanel.addChild(split);

        try {
            navigateTo(System.getProperty("user.home"));
        } catch (Exception e) {
            navigateTo("/");
        }

        showFrame();
    }

    private void createToolbar(Container3D content) {
        Container3D toolbar = new Container3D("Toolbar");
        toolbar.setSize(4.3f, 0.5f);
        toolbar.setLayout(new FlowLayout3D(FlowLayout3D.LEFT));

        Button3D backBtn = new Button3D("Back");
        backBtn.setSize(0.5f, 0.35f);
        backBtn.addListener(new Button3DListener() {
            @Override
            public void buttonPressed(Button3D button) {
                goUp();
            }
        });
        toolbar.add(backBtn);

        Button3D homeBtn = new Button3D("Home");
        homeBtn.setSize(0.5f, 0.35f);
        homeBtn.addListener(new Button3DListener() {
            @Override
            public void buttonPressed(Button3D button) {
                navigateTo(System.getProperty("user.home"));
            }
        });
        toolbar.add(homeBtn);

        Button3D refreshBtn = new Button3D("Refresh");
        refreshBtn.setSize(0.6f, 0.35f);
        refreshBtn.addListener(new Button3DListener() {
            @Override
            public void buttonPressed(Button3D button) {
                refresh();
            }
        });
        toolbar.add(refreshBtn);

        content.add(toolbar, BorderLayout3D.NORTH);
    }

    private Component3D createDirectoryTree() {
        Container3D treePanel = new Container3D("DirectoryTree");
        treePanel.setSize(1.2f, 2.2f);

        Label3D title = new Label3D("Folders");
        title.setTranslation(-0.5f, 1.0f, 0);
        treePanel.addChild(title);

        ScrollPane3D scrollPane = new ScrollPane3D();
        scrollPane.setSize(1.1f, 1.8f);
        scrollPane.setTranslation(0, -0.1f, 0);

        Container3D items = scrollPane.getContentPane();

        addDriveItems(items);

        treePanel.addChild(scrollPane);
        return treePanel;
    }

    private void addDriveItems(Container3D container) {
        File[] roots = File.listRoots();
        float y = 0.8f;

        for (File root : roots) {
            Component3D driveItem = createDriveItem(root);
            driveItem.setTranslation(-0.4f, y, 0);
            container.addChild(driveItem);
            y -= 0.4f;
        }
    }

    private Component3D createDriveItem(File drive) {
        Component3D item = new Container3D("Drive-" + drive.getPath());
        item.setSize(1.0f, 0.35f);

        Component3D icon = new Component3D("DriveIcon");
        icon.setSize(0.2f, 0.2f);

        Appearance app = new Appearance();
        Material mat = new Material();
        mat.diffuse.set(0.7f, 0.7f, 0.2f);
        app.setMaterial(mat);
        icon.setAppearance(app);
        item.addChild(icon);

        Label3D label = new Label3D(drive.getPath());
        label.setTranslation(0.3f, 0, 0.02f);
        label.setFontSize(0.1f);
        item.addChild(label);

        return item;
    }

    private Component3D createFileList() {
        Container3D listPanel = new Container3D("FileList");
        listPanel.setSize(3.0f, 2.2f);

        Label3D title = new Label3D("Files");
        title.setTranslation(-1.3f, 1.0f, 0);
        listPanel.addChild(title);

        ScrollPane3D scrollPane = new ScrollPane3D();
        scrollPane.setSize(2.9f, 1.8f);
        scrollPane.setTranslation(0, -0.1f, 0);

        Container3D items = scrollPane.getContentPane();
        items.setName("FileItems");

        listPanel.addChild(scrollPane);

        return listPanel;
    }

    private void navigateTo(String path) {
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        currentPath = path;
        currentFiles = new ArrayList<>();

        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (!f.isHidden()) {
                    currentFiles.add(f);
                }
            }
        }

        Collections.sort(currentFiles, (f1, f2) -> {
            if (f1.isDirectory() && !f2.isDirectory()) return -1;
            if (!f1.isDirectory() && f2.isDirectory()) return 1;
            return f1.getName().compareToIgnoreCase(f2.getName());
        });

        updateFileList();
        mainFrame.setTitle("File Browser - " + path);
    }

    private void updateFileList() {
        Container3D content = mainFrame.getContentPane();

        Container3D mainPanel = (Container3D) content.getChildren().get(0);
        if (!(mainPanel instanceof SplitPane3D)) return;

        SplitPane3D split = (SplitPane3D) mainPanel;
        Component3D rightPanel = null;

        for (Component3D child : split.getChildren()) {
            if (child instanceof Container3D && rightPanel == null) {
                rightPanel = child;
            }
        }

        if (rightPanel == null) return;

        Container3D listPanel = (Container3D) rightPanel;

        listPanel.getChildren().clear();
        listPanel.setSize(3.0f, 2.2f);

        ScrollPane3D scrollPane = new ScrollPane3D();
        scrollPane.setSize(2.9f, 1.8f);
        scrollPane.setTranslation(0, -0.1f, 0);

        Container3D items = scrollPane.getContentPane();

        float y = 0.85f;
        for (File file : currentFiles) {
            Component3D item = createFileItem(file);
            item.setTranslation(-1.3f, y, 0);
            items.addChild(item);
            y -= 0.35f;

            if (y < -1.0f) break;
        }

        listPanel.addChild(scrollPane);
    }

    private Component3D createFileItem(File file) {
        Container3D item = new Container3D("File-" + file.getName());
        item.setSize(2.8f, 0.3f);

        Component3D icon = new Component3D("Icon");
        icon.setSize(0.25f, 0.25f);

        Appearance app = new Appearance();
        Material mat = new Material();

        if (file.isDirectory()) {
            mat.diffuse.set(0.3f, 0.5f, 0.8f);
        } else {
            String ext = getExtension(file.getName());
            if (ext.equals("txt") || ext.equals("md")) {
                mat.diffuse.set(0.8f, 0.8f, 0.8f);
            } else if (ext.equals("java") || ext.equals("py") || ext.equals("js")) {
                mat.diffuse.set(0.8f, 0.6f, 0.3f);
            } else if (ext.equals("jpg") || ext.equals("png") || ext.equals("gif")) {
                mat.diffuse.set(0.6f, 0.8f, 0.6f);
            } else {
                mat.diffuse.set(0.7f, 0.7f, 0.7f);
            }
        }
        app.setMaterial(mat);
        icon.setAppearance(app);
        item.addChild(icon);

        Label3D label = new Label3D(truncateName(file.getName()));
        label.setTranslation(0.35f, 0, 0.02f);
        label.setFontSize(0.1f);
        item.addChild(label);

        if (file.isFile()) {
            Label3D size = new Label3D(formatSize(file.length()));
            size.setTranslation(1.8f, 0, 0.02f);
            size.setFontSize(0.08f);
            item.addChild(size);
        }

        return item;
    }

    private String getExtension(String name) {
        int dot = name.lastIndexOf('.');
        if (dot > 0 && dot < name.length() - 1) {
            return name.substring(dot + 1).toLowerCase();
        }
        return "";
    }

    private String truncateName(String name) {
        if (name.length() > 25) {
            return name.substring(0, 22) + "...";
        }
        return name;
    }

    private String formatSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return (size / 1024) + " KB";
        return (size / (1024 * 1024)) + " MB";
    }

    private void goUp() {
        File current = new File(currentPath);
        File parent = current.getParentFile();
        if (parent != null) {
            navigateTo(parent.getAbsolutePath());
        }
    }

    private void refresh() {
        navigateTo(currentPath);
    }

    private void showFrame() {
        System.out.println("[FileBrowserDemo] Frame ready");
    }
}

/**
 * Icon library for different file types.
 */
class IconLibrary {

    public static Component3D getIcon(File file) {
        Component3D icon = new Component3D("Icon");

        Material mat = new Material();
        if (file.isDirectory()) {
            mat.diffuse.set(0.3f, 0.5f, 0.8f);
        } else {
            mat.diffuse.set(0.7f, 0.7f, 0.7f);
        }

        Appearance app = new Appearance();
        app.setMaterial(mat);
        icon.setAppearance(app);
        icon.setSize(0.25f, 0.25f);

        return icon;
    }

    public static Component3D getFolderIcon() {
        Component3D icon = new Component3D("FolderIcon");
        Material mat = new Material();
        mat.diffuse.set(0.3f, 0.5f, 0.8f);

        Appearance app = new Appearance();
        app.setMaterial(mat);
        icon.setAppearance(app);
        icon.setSize(0.3f, 0.3f);

        return icon;
    }

    public static Component3D getFileIcon() {
        Component3D icon = new Component3D("FileIcon");
        Material mat = new Material();
        mat.diffuse.set(0.7f, 0.7f, 0.7f);

        Appearance app = new Appearance();
        app.setMaterial(mat);
        icon.setAppearance(app);
        icon.setSize(0.2f, 0.25f);

        return icon;
    }
}