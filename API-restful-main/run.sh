#!/bin/bash

echo "========================================"
echo "   CryptoGecko API - Sistema de Criptomoedas"
echo "========================================"
echo

echo "Verificando se o Maven está instalado..."
if ! command -v mvn &> /dev/null; then
    echo "ERRO: Maven não encontrado! Instale o Maven e adicione ao PATH."
    exit 1
fi

echo
echo "Compilando o projeto..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "ERRO: Falha na compilação!"
    exit 1
fi

echo
echo "========================================"
echo "   Iniciando a aplicação..."
echo "========================================"
echo
echo "URLs importantes:"
echo "- API: http://localhost:8080"
echo "- Swagger UI: http://localhost:8080/swagger-ui.html"
echo "- H2 Console: http://localhost:8080/h2-console"
echo
echo "Pressione Ctrl+C para parar a aplicação"
echo

mvn spring-boot:run

