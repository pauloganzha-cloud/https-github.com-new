# Project Looking Glass 3D (LG3D) - Modernized

A modern fork of Sun Microsystems' Project Looking Glass 3D desktop environment, updated for Java 17+ and modern Linux.

## Status: Early Development

### Original Source
- Source: https://github.com/Ed-Fernando/lg3d-core
- License: GPL-2.0

### Modernization Goals

| Component | Original | Target |
|-----------|----------|--------|
| Java | 1.5/1.6 | 17+ (OpenJDK 21) |
| 3D API | Java 3D | JOGL or OpenJFX |
| Build | Ant | Gradle |
| Platform | i586 | x86_64 |
| X Server | Old X11 | Modern X.org |
| UI | KDE 3 | Modern (optional) |

### Key Modules

```
org.jdesktop.lg3d/
├── appkit        - Application toolkit
├── awt          - AWT to 3D bridge
├── awtpeer      - AWT peer components
├── displayserver - X11 integration (native + Java)
├── scenemanager - Scene management
├── sg           - Scene graph (3D structure)
├── toolkit      - UI toolkit
├── utils        - Utilities
└── wg           - Window manager
```

### Dependencies (To Be Updated)

- Java 3D 1.5.x → JOGL 2.x or OpenJFX 3D
- Escher 0.2.2 → Updated or replaced
- SATIN v2.3 → Updated or removed

### Building

Requirements:
- OpenJDK 17+
- Gradle 8.x
- X11 development libraries
- Linux (tested on Ubuntu 22.04 / Fedora 38)

```bash
# Clone the repo
git clone https://github.com/pauloganzha-cloud/https-github.com-new.git
cd https-github.com-new

# Build
./gradlew build
```

### Project Roadmap

1. ✅ Fork and analyze source
2. ⏳ Convert build system (Ant → Gradle)
3. ⏳ Update Java version
4. ⏳ Replace Java 3D with JOGL
5. ⏳ Update native X11 code
6. ⏳ Build and test on modern Linux

### References

- Original: https://github.com/Ed-Fernando/lg3d-core
- Archived: https://web.archive.org/web/20130609081034/java.net/projects/lg3d
- Wikipedia: https://en.wikipedia.org/wiki/Project_Looking_Glass

---

License: GPL-2.0 (same as original)