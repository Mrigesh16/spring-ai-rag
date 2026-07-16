# Setup & Installation Guide

## Prerequisites

- **Java 21+** - Download from [oracle.com](https://www.oracle.com/java/technologies/downloads/#java21)
- **Docker & Docker Compose** - Download from [docker.com](https://www.docker.com/products/docker-desktop)
- **Ollama** - Download from [ollama.ai](https://ollama.ai)
- **Git** - For version control

## Installation Steps

### 1. Clone the Repository

```bash
git clone <repository-url>
cd spring-ai-rag
```

### 2. Start PostgreSQL with pgvector

```bash
docker-compose up -d
```

This starts:
- **PostgreSQL 16** on port 5432
- **pgvector extension** for vector operations
- Database: `vectordb`
- User: `postgres` / Password: `postgres`

Verify the container is running:

```bash
docker ps
docker logs <container-id>
```

### 3. Install Ollama Models

Download and install Ollama from [ollama.ai](https://ollama.ai)

Start Ollama server:

```bash
ollama serve
```

In a new terminal, pull required models:

```bash
# Chat Model
ollama pull llama3.1

# Embedding Model
ollama pull nomic-embed-text
```

Verify models are available:

```bash
ollama list
```

Expected output:
```
NAME                    ID              SIZE      MODIFIED
llama3.1                asdf1234...     8.1 GB    2 hours ago
nomic-embed-text        qwer5678...     274 MB    1 hour ago
```

### 4. Configure Application Properties

Edit `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/vectordb
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Ollama Configuration
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.model=llama3.1
spring.ai.ollama.embedding.model=nomic-embed-text

# Vector Store Configuration
spring.ai.vectorstore.pgvector.initialization-mode=validate
spring.ai.vectorstore.pgvector.index-type=hnsw
spring.ai.vectorstore.pgvector.distance-type=cosine_distance

# Logging
logging.level.root=INFO
logging.level.com.mrigesh.springairag=DEBUG
```

### 5. Build and Run the Application

Using Maven:

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

Or using IDE:
- Open in IntelliJ IDEA / Eclipse / VS Code
- Run `SpringAiRagApplication.java` as a Spring Boot application

### 6. Verify Installation

Check if the application is running:

```bash
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

Access API documentation:

```
http://localhost:8080/swagger-ui.html
```

## Environment Checklist

Before using the application, ensure:

- ✅ Docker Desktop is running
- ✅ PostgreSQL container is running (`docker ps`)
- ✅ Ollama server is running (`ollama serve`)
- ✅ Required models are downloaded (`ollama list`)
- ✅ Application starts without errors
- ✅ API endpoints are accessible
- ✅ Swagger UI loads at `http://localhost:8080/swagger-ui.html`

## Troubleshooting

### PostgreSQL Connection Error

**Problem**: `could not connect to server: Connection refused`

**Solution**:
```bash
docker-compose up -d
docker logs vectordb
```

### Ollama Connection Error

**Problem**: `Failed to connect to Ollama at http://localhost:11434`

**Solution**:
```bash
ollama serve
# In another terminal, verify:
curl http://localhost:11434/api/version
```

### Out of Memory

**Problem**: Java heap space or GPU memory exhausted

**Solution**:
```bash
# Increase Java heap
export JAVA_OPTS="-Xmx4g"

# For Ollama GPU issues, run in CPU mode:
OLLAMA_NUM_GPU=0 ollama serve
```

### Models Not Found

**Problem**: `Model not found: llama3.1` or `nomic-embed-text`

**Solution**:
```bash
ollama pull llama3.1
ollama pull nomic-embed-text
```

## Performance Tuning

### PostgreSQL Vector Index

For large document collections, optimize pgvector index:

```sql
-- Connect to PostgreSQL
psql -U postgres -d vectordb

-- Check index stats
SELECT * FROM pg_stat_indexes WHERE indexname LIKE '%hnsw%';

-- Adjust M parameter (connections per element) if needed:
-- CREATE INDEX ON ai_document_metadata USING hnsw (embedding vector_cosine_ops)
-- WITH (m=16, ef_construction=200);
```

### Ollama Performance

- **CPU Mode**: Slower but requires less resources - `OLLAMA_NUM_GPU=0 ollama serve`
- **GPU Mode**: Faster inference - ensure CUDA/Metal support is available
- **Model Selection**: Larger models (llama3.1 8B) are slower than smaller variants

### Spring Boot JVM

```properties
# Increase heap size for better performance
-Xmx4g -Xms2g
```

## Next Steps

- Read [API.md](03-API.md) to learn about available endpoints
- Check [ARCHITECTURE.md](04-ARCHITECTURE.md) for technical details
- See [USAGE_EXAMPLES.md](05-USAGE_EXAMPLES.md) for practical examples
