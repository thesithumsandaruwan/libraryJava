@echo off
echo ================================
echo   Library Management System
echo ================================
echo.
echo Compiling application...
javac -cp "lib\*" -d "build\classes" src\javaapplication1\*.java

if %ERRORLEVEL% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo Starting application...
echo.
echo Sample Login Credentials:
echo - Member: member@lib.com / password123
echo - Librarian: librarian@lib.com / password123  
echo - Admin: admin@lib.com / password123
echo.

java -cp "lib\*;build\classes" javaapplication1.FirstPage

echo.
echo Application closed.
pause
