@echo off
SETLOCAL ENABLEDELAYEDEXPANSION

:: =========================
:: 参数1 = 软件目录
:: =========================
set "APPDIR=%~1"
set "LOGDIR=%APPDIR%\logs"
set "LOGFILE=%LOGDIR%\upgrade.log"



:: =========================
:: 准备日志目录
:: =========================
if not exist "%LOGDIR%" mkdir "%LOGDIR%"

:: =========================
:: 清空日志并写首行
:: =========================
> "%LOGFILE%" echo [%date% %time%] upgrade start


:: =========================
:: 启动升级提示窗口（VB / HTA）
:: =========================
start "" mshta "%~dp0upgrade.hta"
call :log wait dialog opened



call :log app dir = %APPDIR%

:: =========================
:: 查找升级包
:: =========================
set "UPGRADE_ZIP="
for %%F in ("%APPDIR%\dbboys.upgrade.*.zip") do (
    set "UPGRADE_ZIP=%%F"
    goto :FOUND_ZIP
)

:FOUND_ZIP
if not defined UPGRADE_ZIP (
    call :log ERROR upgrade package not found
    exit /b 1
)

call :log found upgrade package: %UPGRADE_ZIP%

:: =========================
:: 关闭主程序
:: =========================
call :log closing dbboys.exe
taskkill /F /IM dbboys.exe >> "%LOGFILE%" 2>&1

:: 等待进程完全退出
:WAIT_PROCESS
tasklist /FI "IMAGENAME eq dbboys.exe" | find /I "dbboys.exe" >nul
if %ERRORLEVEL%==0 (
    call :log waiting dbboys.exe exit...
    timeout /t 1 /nobreak >nul
    goto WAIT_PROCESS
)

call :log dbboys.exe closed

:: =========================
:: 修改权限（去只读）
:: =========================
attrib -R "%APPDIR%\*" /S /D >> "%LOGFILE%" 2>&1
call :log change file permission finished

:: =========================
:: 解压升级包
:: =========================

call :log unzip upgrade package begin

"%~dp07za.exe" x "%UPGRADE_ZIP%" -o"%APPDIR%" -y -aoa 2>> "%LOGFILE%"


if %ERRORLEVEL% NEQ 0 (
    call :log ERROR unzip failed
    exit /b 2
)

call :log unzip upgrade package finished


:: =========================
:: 清理临时文件
:: =========================
del /Q "%UPGRADE_ZIP%" >> "%LOGFILE%" 2>&1


:: =========================
:: 关闭升级提示窗口
:: =========================
taskkill /F /IM mshta.exe >nul 2>&1
call :log wait dialog closed


:: =========================
:: 启动主程序
:: =========================
call :log start dbboys.exe
echo Set WshShell = WScript.CreateObject("WScript.Shell") > "%TEMP%\activate.vbs"
echo WshShell.Run """%APPDIR%\dbboys.exe""" >> "%TEMP%\activate.vbs"
echo WshShell.AppActivate "dbboys" >> "%TEMP%\activate.vbs"
cscript //nologo "%TEMP%\activate.vbs"
del "%TEMP%\activate.vbs"

call :log upgrade finished

ENDLOCAL
exit /b 0


:: =====================================================
:: 日志子程序（关键：时间每次都会重新获取）
:: =====================================================
:log
echo [%date% %time%] %* >> "%LOGFILE%"
exit /b
