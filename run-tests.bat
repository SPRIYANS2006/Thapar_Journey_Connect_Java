@echo off
TITLE Thapar Journey Connect — Automated JUnit 5 Test Suite Runner
COLOR 0B

echo ===============================================================================
echo     THAPAR JOURNEY CONNECT — AUTOMATED JUNIT 5 LAB TEST SUITE
echo     Department of Computer Science & Engineering
echo     Thapar Institute of Engineering & Technology, Patiala
echo ===============================================================================
echo.

if exist "C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot"
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

set "ROOT_DIR=%~dp0"
set "BACKEND_DIR=%ROOT_DIR%backend"

echo [TEST] Executing academic test suite:
echo        1. Polymorphic Fare Calculation (Cab vs Train vs Bus)
echo        2. Multithreaded Concurrency & Double-Checked Capacity Guard
echo        3. @thapar.edu Domain Invariant Validation
echo        4. Single Ride Policy Enforcement
echo        5. 96-Slot Engine & Two-Way Directional Keys
echo.

if exist "C:\Users\sonip\.gemini\antigravity\scratch\maven\apache-maven-3.9.6\bin\mvn.cmd" (
    call "C:\Users\sonip\.gemini\antigravity\scratch\maven\apache-maven-3.9.6\bin\mvn.cmd" test -f "%BACKEND_DIR%\pom.xml"
) else (
    mvn test -f "%BACKEND_DIR%\pom.xml"
)

echo.
echo ===============================================================================
echo Test run complete. Check results above.
echo ===============================================================================
pause
