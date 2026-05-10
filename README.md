# Project Looking Glass 3D (LG3D) - Modernized

A modern fork of Sun Microsystems' Project Looking Glass 3D desktop environment, updated for Java 17+ and modern Linux.

## Status: Development - Building

### Original Source

- Source: [https://github.com/Ed-Fernando/lg3d-core](https://github.com/Ed-Fernando/lg3d-core)
- License: GPL-2.0

## Quick Start

### Run Demo (easiest way to test)

```bash
./gradlew run
```

Or run directly:

```bash
java -cp build/classes/java/main:build/resources/main \
    org.jdesktop.lg3d.displayserver.DemoLauncher
```

### Build

```bash
./gradlew build
```

### Run Tests

```bash
./gradlew test
```

## Project Structure

```
lg3d-modern/
├── src/main/java/org/jdesktop/lg3d/
│   ├── displayserver/    # Main display server
│   │   ├── Main.java           # Entry point
│   │   ├── DisplayServer.java  # Display server implementation
│   │   └── DemoLauncher.java   # Standalone 3D demo
│   ├── sg/               # Scene graph (3D)
│   │   ├── SceneGraphObject.java   # Base class
│   │   ├── Node.java              # Scene node
│   │   ├── Group.java            # Group nodes
│   │   ├── Transform3D.java      # Matrix transforms
│   │   ├── Shape3D.java           # 3D shape with geometry
│   │   └── JoglCanvas.java       # JOGL rendering
│   ├── wg/               # Window manager
│   │   ├── Window3D.java         # 3D window
│   │   ├── Component3D.java      # UI component
│   │   └── event/               # Event system
│   └── utils/            # Utilities
│       └── Utils.java           # Vector math, etc.
├── src/test/             # JUnit tests
├── build.gradle          # Gradle build config
└── .github/workflows/    # CI/CD
```

## Key Components

### Scene Graph (sg/)

| Class | Description |
|-------|-------------|
| `SceneGraphObject` | Base for all scene graph nodes |
| `Node` | Scene node with parent/child hierarchy |
| `Group` | Container for child nodes |
| `TransformGroup` | Group with transform matrix |
| `BranchGroup` | Root of a scene branch |
| `Shape3D` | 3D shape with geometry |
| `Transform3D` | 4x4 transformation matrix |
| `JoglCanvas` | JOGL canvas for OpenGL rendering |

### Window Manager (wg/)

| Class | Description |
|-------|-------------|
| `Window3D` | 3D window representation |
| `Component3D` | Base UI component |
| `Cursor3D` | 3D cursor |

### Display Server

| Class | Description |
|-------|-------------|
| `DisplayServer` | Main display server |
| `SceneGraph` | Scene management |
| `VirtualDisplay` | Virtual 3D display |
| `X11Bridge` | X11 integration (placeholder) |

## Modernization Goals

| Component | Original | Target |
|-----------|----------|--------|
| Java | 1.5/1.6 | 17+ (OpenJDK 21) |
| 3D API | Java 3D | JOGL 2.x |
| Build | Ant | Gradle |
| Platform | i586 | x86_64 |
| X Server | Old X11 | Modern X.org |

## Running the Demo

The demo launcher shows a simple 3D scene with floating window frames:

1. A floor plane
2. Three 3D window frames floating in space
3. Auto-rotation animation
4. OpenGL lighting

### Controls
- **ESC** - Exit demo

## CI/CD

GitHub Actions automatically builds and tests on:
- Ubuntu (latest)
- JDK 17

## Dependencies

- **JOGL 2.4.0-rc** - OpenGL bindings for Java
- **JUnit 4.13.2** - Testing framework

## Roadmap

- [x] Fork and analyze source
- [x] Create Gradle build
- [x] Java 17+ support
- [x] Scene graph (Transform3D, Node, Shape)
- [x] JOGL integration
- [x] Window manager (Window3D, Component3D)
- [x] Event system
- [x] Demo launcher
- [ ] X11 integration
- [ ] Native X11 code port
- [ ] Full UI components
- [ ] Production build

## References

- Original: [https://github.com/Ed-Fernando/lg3d-core](https://github.com/Ed-Fernando/lg3d-core)
- Archived: [https://web.archive.org/web/20130609081034/java.net/projects/lg3d](https://web.archive.org/web/20130609081034/java.net/projects/lg3d)
- Wikipedia: [https://en.wikipedia.org/wiki/Project_Looking_Glass](https://en.wikipedia.org/wiki/Project_Looking_Glass)
- JOGL: [https://jogamp.org/](https://jogamp.org/)

---

License: GPL-2.0 (same as original)