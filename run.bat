@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

title Silent-SOS Emergency Response System (Java 21)
color 0A

echo =================================================================
echo   SILENT-SOS: COVERT EMERGENCY RESPONSE SYSTEM (JAVA 21)
echo   B.Tech Engineering Microproject
echo =================================================================
echo.

where java >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Java is not detected in your PATH.
    echo Please install JDK 21 and ensure 'java' is configured.
    pause
    exit /b 1
)

echo [1/3] Creating output directory...
if not exist "bin" mkdir "bin"
if not exist "data" mkdir "data"

echo [2/3] Compiling Java source files...
javac -d bin -sourcepath src/main/java src/main/java/com/silentsos/Main.java src/main/java/com/silentsos/model/*.java src/main/java/com/silentsos/service/*.java src/main/java/com/silentsos/controller/*.java src/main/java/com/silentsos/util/*.java

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Java compilation failed! Please inspect the error messages above.
    pause
    exit /b 1
)

echo [3/3] Launching Silent-SOS Server on http://localhost:8080 ...
echo.
java -cp bin com.silentsos.Main

pause
