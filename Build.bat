@echo off

echo ==== Build backend: Maven ====
call steam-api\mvnw.cmd -f pom.xml -DskipTests package
if %ERRORLEVEL% NEQ 0 (
  echo 后端构建失败，停止。
  pause
  exit /b 1
)

echo ==== Start Backend ====
start "SteamLauncher" cmd /k "java -jar steam-launcher\target\steam-launcher-0.0.1.jar"

echo ==== Start Frontend ====
start "Vite" cmd /k "cd vue && npm run dev"

echo ==== Build completed successfully ====
exit /b 0
