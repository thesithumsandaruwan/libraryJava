@echo off
echo ================================
echo   Database Repair Utility
echo ================================
echo.
echo This utility will fix common database issues:
echo - Missing user_role column
echo - Missing borrowings table
echo - Data inconsistencies
echo.
echo Compiling repair utility...
javac -cp "lib\*" -d "build\classes" src\javaapplication1\DatabaseRepair.java src\javaapplication1\DatabaseConnection.java

if %ERRORLEVEL% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo Running database repair...
echo.

java -cp "lib\*;build\classes" javaapplication1.DatabaseRepair

echo.
echo Repair completed.
pause
