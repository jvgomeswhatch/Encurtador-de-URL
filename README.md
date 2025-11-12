# 🔗 Encurtador de URLs

Encurtador de URLs desenvolvido com Spring Boot, PostgreSQL, Docker e Traefik com validação de URLs e personalização de códigos.

## 🚀 Funcionalidades

- ✅ **Encurtamento de URLs** com geração automática de código
- 🎨 **Personalização de URLs** - defina seu próprio código curto
- ✔️ **Validação de URLs** - verifica se a URL existe antes de encurtar
- 📊 **Estatísticas de acesso** - contador de cliques
- 🔄 **Redirecionamento automático** para a URL original
- 🐳 **Containerizado** com Docker e orquestrado com Traefik
- 💾 **Persistência** com PostgreSQL

## 📋 Estrutura do Projeto

```
url-shortener/
├── src/
│   └── main/
│       ├── java/com/urlshortener/
│       │   ├── controller/
│       │   │   └── UrlShortenerController.java
│       │   ├── dto/
│       │   │   └── UrlDTO.java
│       │   ├── entity/
│       │   │   └── UrlEntity.java
│       │   ├── exception/
│       │   │   ├── Exceptions.java
│       │   │   └── GlobalExceptionHandler.java
│       │   ├── repository/
│       │   │   └── UrlRepository.java
│       │   ├── service/
│       │   │   └── UrlShortenerService.java
│       │   ├── validator/
│       │   │   └── UrlValidator.java
│       │   └── UrlShortenerApplication.java
│       └── resources/
│           └── application.properties
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## 🛠️ Tecnologias

- **Java 17**
- **Spring Boot 3.2.0**
- **PostgreSQL 15**
- **Docker & Docker Compose**
- **Traefik 2.10** (Reverse Proxy)
- **Lombok**
- **Spring Data JPA**
- **Bean Validation**

## 🚀 Como Executar

### Pré-requisitos
- Docker e Docker Compose instalados

### Opção 1: Executar Tudo com Docker (Recomendado)

1. **Clone o repositório** (ou crie os arquivos)

2. **Estruture os arquivos:**
```bash
mkdir -p src/main/java/com/urlshortener/{controller,dto,entity,exception,repository,service,validator}
mkdir -p src/main/resources
```

3. **Suba os containers:**
```bash
docker-compose up --build
```

4. **Acesse:**
- **API:** http://localhost
- **Traefik Dashboard:** http://localhost:8080

### Opção 2: Executar Spring Boot Localmente

1. **Instale PostgreSQL localmente** (se ainda não tiver)

2. **Crie o banco de dados:**
```sql
CREATE DATABASE urlshortener;
CREATE USER shortener WITH PASSWORD 'shortener123';
GRANT ALL PRIVILEGES ON DATABASE urlshortener TO shortener;
```

3. **Execute apenas o PostgreSQL via Docker:**
```bash
docker-compose up postgres
```

4. **Execute a aplicação Spring Boot:**
```bash
mvn spring-boot:run
```

5. **Acesse:** http://localhost:8081

## 📡 Endpoints da API

### 1. Encurtar URL

**POST** `/api/shorten`

```json
{
  "url": "https://www.google.com",
  "customCode": "google",
  "createdBy": "usuario123"
}
```

**Resposta:**
```json
{
  "originalUrl": "https://www.google.com",
  "shortUrl": "http://localhost/google",
  "shortCode": "google",
  "accessCount": 0,
  "createdAt": "2024-11-12T10:30:00",
  "isCustom": true
}
```

### 2. Redirecionar

**GET** `/{shortCode}`

Redireciona automaticamente para a URL original.

Exemplo: `http://localhost/google` → `https://www.google.com`

### 3. Estatísticas

**GET** `/api/stats/{shortCode}`

```json
{
  "originalUrl": "https://www.google.com",
  "shortUrl": "http://localhost/google",
  "shortCode": "google",
  "accessCount": 42,
  "createdAt": "2024-11-12T10:30:00",
  "isCustom": true
}
```

### 4. Health Check

**GET** `/api/health`

## 🔍 Validações

### Formato da URL
- Valida se a URL tem formato válido
- Aceita URLs com ou sem `http://` ou `https://`
- Normaliza URLs automaticamente

### Existência da URL
- Faz requisição HEAD para verificar se a URL existe
- Timeout de 5 segundos
- Aceita códigos HTTP 2xx e 3xx

### Código Personalizado
- Mínimo de 3 caracteres
- Máximo de 20 caracteres
- Apenas letras, números, hífens e underscores
- Único (não pode repetir)

## 🧪 Exemplos de Uso

### cURL

```bash
# Encurtar URL com código automático
curl -X POST http://localhost/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://github.com"}'

# Encurtar URL com código personalizado
curl -X POST http://localhost/api/shorten \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://github.com",
    "customCode": "gh",
    "createdBy": "joao"
  }'

# Obter estatísticas
curl http://localhost/api/stats/gh

# Redirecionar (no navegador)
# http://localhost/gh
```

## ⚙️ Configurações

Edite `application.properties` para alterar:

```properties
# URL base para os links encurtados
url.shortener.base-url=http://localhost

# Tamanho do código personalizado
url.shortener.custom-code-min-length=3
url.shortener.custom-code-max-length=20
```

## 🗄️ Banco de Dados

A tabela `urls` é criada automaticamente:

| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGINT | ID único |
| original_url | VARCHAR(2048) | URL original |
| short_code | VARCHAR(20) | Código curto (único) |
| created_at | TIMESTAMP | Data de criação |
| access_count | BIGINT | Contador de acessos |
| is_custom | BOOLEAN | Se é código personalizado |
| created_by | VARCHAR(50) | Criador (opcional) |

## 🔒 Tratamento de Erros

| Código | Erro | Descrição |
|--------|------|-----------|
| 400 | Bad Request | URL inválida ou validação falhou |
| 404 | Not Found | Código curto não encontrado |
| 409 | Conflict | Código personalizado já existe |

## 🐳 Docker

### Serviços:

1. **Traefik** - Reverse proxy (porta 80)
2. **PostgreSQL** - Banco de dados (porta 5432)
3. **App** - Aplicação Spring Boot (porta interna 8081)

### Comandos úteis:

```bash
# Ver logs
docker-compose logs -f app

# Parar containers
docker-compose down

# Limpar volumes (apaga dados)
docker-compose down -v

# Reconstruir
docker-compose up --build --force-recreate
```

## 📝 Notas

- O código gerado automaticamente tem 6 caracteres
- URLs duplicadas retornam o mesmo código (se não for personalizado)
- O contador de acessos é incrementado a cada redirecionamento
- Traefik Dashboard disponível em http://localhost:8080

## 🤝 Contribuindo

Sinta-se à vontade para fazer fork e melhorias!