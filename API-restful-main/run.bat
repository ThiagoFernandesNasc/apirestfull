@echo off
echo ========================================
echo    CryptoGecko API - Sistema de Criptomoedas
echo ========================================
echo.

echo Verificando se o Maven esta instalado...
mvn --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: Maven nao encontrado! Instale o Maven e adicione ao PATH.
    pause
    exit /b 1
)

echo.
echo Compilando o projeto...
mvn clean compile

if %errorlevel% neq 0 (
    echo ERRO: Falha na compilacao!
    pause
    exit /b 1
)

echo.
echo ========================================
echo    Iniciando a aplicacao...
echo ========================================
echo.
echo URLs importantes:
echo - API: http://localhost:8080
echo - Swagger UI: http://localhost:8080/swagger-ui.html
echo - H2 Console: http://localhost:8080/h2-console
echo.
echo Pressione Ctrl+C para parar a aplicacao
echo.

mvn spring-boot:run

pause

