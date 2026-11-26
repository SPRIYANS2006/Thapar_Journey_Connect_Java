@echo off
TITLE Thapar Journey Connect — University Java Lab Edition
COLOR 0A

echo ===============================================================================
echo     THAPAR JOURNEY CONNECT — JAVA FULL-STACK LAB EDITION
echo     Campus Transit & Travel Cohort Operating System
echo     Thapar Institute of Engineering & Technology, Patiala
echo ===============================================================================
echo.

REM 1. Detect Java Environment (Prioritize JDK 21)
if exist "C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot"
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java 17/21 is required but was not found in PATH or standard directories.
    echo Please install JDK 21 or set JAVA_HOME.
    pause
    exit /b 1
)

echo [OK] Using Java runtime:
java -version
echo.

REM 2. Detect Node.js
where npm >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Node.js / npm is required for the React frontend but was not found in PATH.
    pause
    exit /b 1
)

set "ROOT_DIR=%~dp0"
set "BACKEND_DIR=%ROOT_DIR%backend"
set "FRONTEND_DIR=%ROOT_DIR%frontend"
set "JAR_FILE=%BACKEND_DIR%\target\thapar-journey-connect-backend-1.0.0.jar"

REM 3. Verify / Build JAR if not present
if not exist "%JAR_FILE%" (
    echo [BUILD] Backend JAR not found. Building with Maven...
    if exist "C:\Users\sonip\.gemini\antigravity\scratch\maven\apache-maven-3.9.6\bin\mvn.cmd" (
        call "C:\Users\sonip\.gemini\antigravity\scratch\maven\apache-maven-3.9.6\bin\mvn.cmd" package -DskipTests -f "%BACKEND_DIR%\pom.xml"
    ) else (
        mvn package -DskipTests -f "%BACKEND_DIR%\pom.xml"
    )
)

echo.
echo ===============================================================================
echo [1/2] Launching Spring Boot Backend on http://localhost:8080 ...
echo       * Embedded Database : H2 in-memory (zero configuration)
echo       * H2 Console Path   : http://localhost:8080/h2-console
echo       * Test Student #1   : student1@thapar.edu / Thapar@123
echo       * Test Student #2   : student2@thapar.edu / Thapar@123
echo       * Test Admin Desk   : admin@thapar.edu    / Thapar@123
echo ===============================================================================
start "Thapar Journey Connect - Spring Boot Backend (Port 8080)" cmd /k "cd /d \"%BACKEND_DIR%\" && java -jar \"%JAR_FILE%\""

timeout /t 4 /nobreak >nul

echo.
echo ===============================================================================
echo [2/2] Launching React 19 + Tailwind CSS Frontend on http://localhost:5173 ...
echo ===============================================================================
start "Thapar Journey Connect - React Frontend (Port 5173)" cmd /k "cd /d \"%FRONTEND_DIR%\" && npm run dev"

timeout /t 3 /nobreak >nul
start http://localhost:5173/

echo.
echo ===============================================================================
echo     SUCCESSFULLY LAUNCHED LOCAL LAB EVALUATION ENVIRONMENT
echo.
echo     Frontend UI       : http://localhost:5173
echo     Spring REST API   : http://localhost:8080/api/health
echo     H2 DB Console     : http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:thapar_journey)
echo ===============================================================================
echo.
pause
