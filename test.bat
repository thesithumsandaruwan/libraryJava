@echo off
echo ================================
echo   Database Integration Test
echo ================================
echo.
echo Compiling test classes...
javac -cp "lib\*" -d "build\classes" src\javaapplication1\DatabaseTest.java src\javaapplication1\DatabaseConnection.java src\javaapplication1\UserDAO.java src\javaapplication1\BookDAO.java

if %ERRORLEVEL% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo Running database tests...
echo.

java -cp "lib\*;build\classes" javaapplication1.DatabaseTest

echo.
echo Test completed.
pause
