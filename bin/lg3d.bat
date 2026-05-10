@echo off
REM LG3D Modern Launcher Script for Windows

echo ========================================
echo   LG3D Modern - Project Looking Glass
echo ========================================
echo.

if not exist "gradlew.bat" (
    echo Error: gradlew.bat not found
    exit /b 1
)

set GRADLE_OPTS=-Xmx512m -XX:MaxMetaspaceSize=256m

gradlew.bat run %*

if errorlevel 1 (
    echo.
    echo Error running LG3D. Make sure Java 17+ is installed.
    exit /b 1
)