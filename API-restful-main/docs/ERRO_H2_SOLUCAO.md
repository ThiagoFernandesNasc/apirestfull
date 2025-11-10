# 🔴 Erro H2 Database - Explicação e Solução

## ❌ Erro que você está vendo:

```
Database "C:/Users/thiag/test" not found, either pre-create it 
or allow remote database creation (not recommended in secure environments) 
[90149-224] 90149/90149
```

## 🔍 O que está acontecendo?

O H2 está tentando usar um banco de dados baseado em **arquivo** (file-based) no caminho `C:/Users/thiag/test`, mas esse arquivo não existe.

### Tipos de Banco H2:

1. **Em Memória (In-Memory)** - `jdbc:h2:mem:testdb`
   - ✅ Dados ficam na RAM
   - ✅ Mais rápido
   - ✅ Não precisa criar arquivo
   - ❌ Dados são perdidos ao reiniciar

2. **Em Arquivo (File-Based)** - `jdbc:h2:C:/Users/thiag/test`
   - ✅ Dados persistem em disco
   - ✅ Dados não são perdidos ao reiniciar
   - ❌ Precisa que o arquivo exista ou criar primeiro
   - ❌ Mais lento

## 🛠️ Como Corrigir

### Opção 1: Usar Banco em Memória (Recomendado para Desenvolvimento)

O projeto já está configurado para usar banco em memória. Verifique o `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb  # ✅ Correto - em memória
    driver-class-name: org.h2.Driver
    username: sa
    password: password
```

**Se você estiver usando o H2 Console, use estas credenciais:**
- **JDBC URL**: `jdbc:h2:mem:testdb` ⚠️ **IMPORTANTE: deve ser `mem:testdb`**
- **Username**: `sa`
- **Password**: `password`

### Opção 2: Usar Banco em Arquivo (Se quiser persistir dados)

Se você realmente quer usar um banco em arquivo, precisa:

1. **Criar o diretório primeiro:**
   ```bash
   # No Windows PowerShell
   mkdir C:\Users\thiag
   ```

2. **Modificar o `application.yml`:**
   ```yaml
   spring:
     datasource:
       url: jdbc:h2:file:C:/Users/thiag/test
       # ou
       url: jdbc:h2:file:./data/testdb  # Cria na pasta do projeto
   ```

3. **Ou permitir criação automática** (não recomendado em produção):
   ```yaml
   spring:
     datasource:
       url: jdbc:h2:file:C:/Users/thiag/test;AUTO_SERVER=TRUE
   ```

## ✅ Solução Rápida

### Se você está no H2 Console:

**Use este JDBC URL:**
```
jdbc:h2:mem:testdb
```

**NÃO use:**
- ❌ `jdbc:h2:C:/Users/thiag/test`
- ❌ `jdbc:h2:file:C:/Users/thiag/test`
- ❌ `jdbc:h2:test`

### Se você modificou o `application.yml`:

**Volte para:**
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
```

## 📝 Diferença entre os Modos

| Modo | JDBC URL | Dados Persistem? | Velocidade |
|------|----------|------------------|------------|
| **Memória** | `jdbc:h2:mem:nome` | ❌ Não | ⚡ Muito rápido |
| **Arquivo** | `jdbc:h2:file:caminho` | ✅ Sim | 🐢 Mais lento |

## 🎯 Para o Projeto Atual

**O projeto está configurado corretamente para desenvolvimento:**
- Usa banco em memória (`jdbc:h2:mem:testdb`)
- Dados são perdidos ao reiniciar (comportamento esperado)
- Dados de exemplo são carregados automaticamente pelo `DataInitializer`

**Se você quiser persistir dados entre reinicializações:**

1. Mude para arquivo:
   ```yaml
   spring:
     datasource:
       url: jdbc:h2:file:./data/cryptodb
   ```

2. Crie a pasta `data` no projeto

3. Ou use um banco de produção (PostgreSQL, MySQL, etc.)

## 💡 Dica

Se você está vendo esse erro no H2 Console, provavelmente:
- Digitou o JDBC URL errado
- Copiou de algum lugar que estava usando arquivo

**Solução:** Use sempre `jdbc:h2:mem:testdb` para o projeto atual!

