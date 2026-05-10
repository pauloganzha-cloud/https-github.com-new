# LG3D Modern - Project Looking Glass 3D Desktop

A modernized fork of Sun Microsystems' Project Looking Glass 3D desktop environment, updated for Java 17+ and modern Linux systems.

## Status

**Version:** 1.0.0
**Build:** Passing
**Language:** Java 100%

## Quick Start

### Prerequisites

- Java 17 or higher
- Gradle 8.x
- Linux (Ubuntu 20.04+, Fedora 36+) or Windows with WSL

### Build

```bash
# Clone the repository
git clone https://github.com/pauloganzha-cloud/https-github.com-new.git
cd https-github.com-new

# Build the project
./gradlew build

# Run tests
./gradlew test
```

### Run Demos

```bash
# Basic demo
./gradlew runDemo

# Enhanced demo with 3D windows
./gradlew runEnhancedDemo

# File browser demo
./gradlew runFileBrowserDemo
```

Or use the launcher script:

```bash
# Linux/Mac
chmod +x bin/lg3d.sh
./bin/lg3d.sh demo

# Windows
bin\lg3d.bat
```

## Project Structure

```
lg3d-modern/
├── bin/                    # Launcher scripts
│   ├── lg3d.sh            # Linux/Mac launcher
│   └── lg3d.bat          # Windows launcher
├── src/
│   ├── main/
│   │   ├── c/             # Native C code (X11)
│   │   └── java/
│   │       └── org/jdesktop/lg3d/
│   │           ├── animation/    # Animation system
│   │           ├── appkit/       # Application framework
│   │           ├── displayserver/ # Main server
│   │           ├── scenemanager/ # Scene management
│   │           ├── sg/           # Scene graph
│   │           ├── utils/        # Utilities, Audio, Theme
│   │           └── wg/           # Window manager
│   └── test/              # JUnit tests
├── build.gradle           # Gradle build config
└── README.md              # This file
```

## Features

### Core System

- **Scene Graph** - Transform3D, Node, Group, Shape3D
- **JOGL Integration** - OpenGL rendering via JOGL 2.x
- **X11 Integration** - Native X11 with Java bridge
- **Animation System** - Position, Scale, Rotation animations

### Window Management

- **WindowManager** - Full window lifecycle management
- **Window3D** - 3D window representation
- **Taskbar** - System taskbar with clock and tray

### UI Components

- Button3D, Checkbox3D, Slider3D
- TextField3D, Label3D
- List3D, ComboBox3D, TabbedPane3D
- ScrollPane3D, SplitPane3D, Panel3D

### System Features

- **Theme System** - Dark, Light, Blue, Green themes
- **Screen Capture** - Screenshot and recording
- **Audio System** - Sound playback
- **Clipboard** - Clipboard with history
- **Drag & Drop** - Drag and drop support
- **Settings** - Full settings dialog

## Demos

| Demo | Description |
|------|-------------|
| `DemoLauncher` | Basic 3D scene with rotating objects |
| `EnhancedDemo` | Multiple 3D window frames |
| `FileBrowserDemo` | 3D file browser interface |

## Technology Stack

- **Java** 17+ - Modern Java with records and patterns
- **JOGL** 2.4.0 - OpenGL bindings
- **Gradle** 8.x - Build system
- **JUnit** 4.13.2 - Testing framework

## Roadmap

- [x] Core scene graph
- [x] JOGL rendering
- [x] Window management
- [x] UI components
- [x] X11 integration
- [x] Animation system
- [x] Theme system
- [ ] Full demo integration
- [ ] Native library builds
- [ ] Production release

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests: `./gradlew test`
5. Submit a pull request

## License

GPL-2.0 (same as original Project Looking Glass)

## References

- [Original Project Looking Glass](https://github.com/Ed-Fernando/lg3d-core)
- [JOGL Project](https://jogamp.org/)
- [Wikipedia - Project Looking Glass](https://en.wikipedia.org/wiki/Project_Looking_Glass)

## Credits

Original project by Sun Microsystems.
Modernization by LG3D-Modern Team.