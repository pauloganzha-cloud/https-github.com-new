package org.jdesktop.lg3d.displayserver;

import org.jdesktop.lg3d.wg.*;
import org.jdesktop.lg3d.wg.components.*;
import org.jdesktop.lg3d.sg.*;
import org.jdesktop.lg3d.animation.*;

/**
 * Settings Dialog - configuration UI.
 */
public class SettingsDialog extends Frame3D {

    private Slider3D fpsSlider;
    private Slider3D qualitySlider;
    private Checkbox3D vsyncCheck;
    private Checkbox3D shadowsCheck;
    private Slider3D volumeSlider;
    private Checkbox3D soundCheck;
    private Checkbox3D fullscreenCheck;
    private Slider3D fovSlider;

    private SettingsListener listener;

    public SettingsDialog() {
        super("Settings");
        initUI();
    }

    private void initUI() {
        setSize(4.0f, 3.5f);

        Container3D content = getContentPane();
        content.setLayout(new BorderLayout3D());

        Container3D mainPanel = new Container3D("SettingsPanel");
        mainPanel.setSize(3.8f, 3.0f);
        content.add(mainPanel, BorderLayout3D.CENTER);

        Label3D title = new Label3D("Settings");
        title.setTranslation(-1.7f, 1.3f, 0);
        title.setFontSize(0.18f);
        mainPanel.addChild(title);

        float y = 1.0f;

        Label3D displayLabel = new Label3D("Display");
        displayLabel.setTranslation(-1.6f, y, 0);
        displayLabel.setFontSize(0.12f);
        mainPanel.addChild(displayLabel);
        y -= 0.35f;

        Label3D fpsLabel = new Label3D("Frame Rate");
        fpsLabel.setTranslation(-1.6f, y, 0);
        fpsLabel.setFontSize(0.1f);
        mainPanel.addChild(fpsLabel);

        fpsSlider = new Slider3D();
        fpsSlider.setValue(60);
        fpsSlider.setMinValue(30);
        fpsSlider.setMaxValue(120);
        fpsSlider.setTranslation(-0.5f, y, 0);
        mainPanel.addChild(fpsSlider);

        Label3D fpsValue = new Label3D("60");
        fpsValue.setTranslation(1.2f, y, 0);
        fpsValue.setFontSize(0.1f);
        mainPanel.addChild(fpsValue);
        y -= 0.4f;

        Label3D fovLabel = new Label3D("Field of View");
        fovLabel.setTranslation(-1.6f, y, 0);
        fovLabel.setFontSize(0.1f);
        mainPanel.addChild(fovLabel);

        fovSlider = new Slider3D();
        fovSlider.setValue(60);
        fovSlider.setMinValue(30);
        fovSlider.setMaxValue(120);
        fovSlider.setTranslation(-0.5f, y, 0);
        mainPanel.addChild(fovSlider);
        y -= 0.4f;

        vsyncCheck = new Checkbox3D("VSync");
        vsyncCheck.setSelected(true);
        vsyncCheck.setTranslation(-1.6f, y, 0);
        mainPanel.addChild(vsyncCheck);

        shadowsCheck = new Checkbox3D("Shadows");
        shadowsCheck.setSelected(false);
        shadowsCheck.setTranslation(-0.3f, y, 0);
        mainPanel.addChild(shadowsCheck);
        y -= 0.4f;

        fullscreenCheck = new Checkbox3D("Fullscreen");
        fullscreenCheck.setSelected(false);
        fullscreenCheck.setTranslation(-1.6f, y, 0);
        mainPanel.addChild(fullscreenCheck);
        y -= 0.5f;

        Label3D audioLabel = new Label3D("Audio");
        audioLabel.setTranslation(-1.6f, y, 0);
        audioLabel.setFontSize(0.12f);
        mainPanel.addChild(audioLabel);
        y -= 0.35f;

        soundCheck = new Checkbox3D("Enable Sound");
        soundCheck.setSelected(true);
        soundCheck.setTranslation(-1.6f, y, 0);
        mainPanel.addChild(soundCheck);
        y -= 0.4f;

        Label3D volumeLabel = new Label3D("Volume");
        volumeLabel.setTranslation(-1.6f, y, 0);
        volumeLabel.setFontSize(0.1f);
        mainPanel.addChild(volumeLabel);

        volumeSlider = new Slider3D();
        volumeSlider.setValue(0.8f);
        volumeSlider.setTranslation(-0.5f, y, 0);
        mainPanel.addChild(volumeSlider);
        y -= 0.5f;

        Label3D perfLabel = new Label3D("Performance");
        perfLabel.setTranslation(-1.6f, y, 0);
        perfLabel.setFontSize(0.12f);
        mainPanel.addChild(perfLabel);
        y -= 0.35f;

        Label3D qualityLabel = new Label3D("Quality");
        qualityLabel.setTranslation(-1.6f, y, 0);
        qualityLabel.setFontSize(0.1f);
        mainPanel.addChild(qualityLabel);

        qualitySlider = new Slider3D();
        qualitySlider.setValue(1.0f);
        qualitySlider.setTranslation(-0.5f, y, 0);
        mainPanel.addChild(qualitySlider);
        y -= 0.5f;

        Container3D buttons = new Container3D("Buttons");
        buttons.setSize(3.6f, 0.5f);
        buttons.setTranslation(0, -1.2f, 0);
        mainPanel.addChild(buttons);

        Button3D okBtn = new Button3D("OK");
        okBtn.setSize(0.8f, 0.4f);
        okBtn.setTranslation(-1.0f, 0, 0);
        okBtn.addListener(new Button3DListener() {
            @Override
            public void buttonPressed(Button3D button) {
                applySettings();
                close();
            }
        });
        buttons.addChild(okBtn);

        Button3D cancelBtn = new Button3D("Cancel");
        cancelBtn.setSize(0.8f, 0.4f);
        cancelBtn.setTranslation(0, 0, 0);
        cancelBtn.addListener(new Button3DListener() {
            @Override
            public void buttonPressed(Button3D button) {
                close();
            }
        });
        buttons.addChild(cancelBtn);

        Button3D applyBtn = new Button3D("Apply");
        applyBtn.setSize(0.8f, 0.4f);
        applyBtn.setTranslation(1.0f, 0, 0);
        applyBtn.addListener(new Button3DListener() {
            @Override
            public void buttonPressed(Button3D button) {
                applySettings();
            }
        });
        buttons.addChild(applyBtn);
    }

    private void applySettings() {
        if (listener != null) {
            Settings s = new Settings();
            s.frameRate = (int) fpsSlider.getValue();
            s.fov = (int) fovSlider.getValue();
            s.vsync = vsyncCheck.isSelected();
            s.shadows = shadowsCheck.isSelected();
            s.fullscreen = fullscreenCheck.isSelected();
            s.soundEnabled = soundCheck.isSelected();
            s.volume = volumeSlider.getValue();
            s.quality = qualitySlider.getValue();
            listener.settingsApplied(s);
        }
    }

    private void close() {
        setVisible(false);
    }

    public void setSettingsListener(SettingsListener listener) {
        this.listener = listener;
    }

    public void show() {
        setVisible(true);
    }

    static class Settings {
        int frameRate = 60;
        int fov = 60;
        boolean vsync = true;
        boolean shadows = false;
        boolean fullscreen = false;
        boolean soundEnabled = true;
        float volume = 0.8f;
        float quality = 1.0f;
    }
}

interface SettingsListener {
    void settingsApplied(SettingsDialog.Settings settings);
}