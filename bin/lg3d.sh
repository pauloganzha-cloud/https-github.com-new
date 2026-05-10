#!/bin/bash
# LG3D Modern Launcher Script for Linux/Mac

echo "========================================"
echo "  LG3D Modern - Project Looking Glass"
echo "========================================"
echo ""

if [ ! -f "gradlew" ]; then
    echo "Error: gradlew not found"
    exit 1
fi

chmod +x gradlew

export GRADLE_OPTS="-Xmx512m -XX:MaxMetaspaceSize=256m"

case "$1" in
    demo)
        gradlew runDemo
        ;;
    enhanced)
        gradlew runEnhancedDemo
        ;;
    browser|filebrowser)
        gradlew runFileBrowserDemo
        ;;
    test)
        gradlew test
        ;;
    build)
        gradlew build
        ;;
    clean)
        gradlew clean
        ;;
    "")
        gradlew run
        ;;
    *)
        echo "Usage: $0 [demo|enhanced|browser|test|build|clean]"
        echo ""
        echo "Options:"
        echo "  demo       - Run basic demo"
        echo "  enhanced   - Run enhanced demo with 3D windows"
        echo "  browser    - Run file browser demo"
        echo "  test       - Run tests"
        echo "  build      - Build the project"
        echo "  clean      - Clean build"
        exit 1
        ;;
esac