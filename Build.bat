@echo off

echo ==== Build backend: Maven ====
call steam-api\mvnw.cmd -f pom.xml -DskipTests clean package
if %ERRORLEVEL% NEQ 0 (
  echo Build Failed!
  pause
  exit /b 1
)

echo Stopping any running steam-launcher java processes (if present)...
powershell -NoProfile -Command "Get-CimInstance Win32_Process | Where-Object { $_.CommandLine -and $_.CommandLine -match 'steam-launcher-0.0.1.jar' } | ForEach-Object { Write-Host 'Stopping PID:' $_.ProcessId; Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue }"

echo ==== Start Backend ====
echo Starting SteamLauncher in UTF-8 console
start "SteamLauncher" cmd /k "chcp 65001>nul & java -Dfile.encoding=UTF-8 -jar steam-launcher\target\steam-launcher-0.0.1.jar"

echo ==== Start Frontend ====
start "Vite" cmd /k "cd vue && npm run dev"

echo ==== Build completed successfully ====
exit /b 0
