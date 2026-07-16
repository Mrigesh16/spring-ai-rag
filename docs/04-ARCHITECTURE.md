# Technical Architecture

## System Architecture

```
┌───────────────────────────────────────────────────────────────────┐
│                         CLIENT LAYER                              │
│         (Web Browser, Mobile App, External Services)              │
└────────────────────────┬────────────────────────────────────────┘
                         │
                    REST API (JSON)
                         │
┌────────────────────────▼────────────────────────────────────────┐
│                    PRESENTATION LAYER                            │
│  Controllers (Handle HTTP requests/responses)                    │
│  ├─ AskController                                                │
│  ├─ ChatController                                               │
│  ├─ PdfController                                                │
│  ├─ EmbeddingController                                          │
│  └─ DocumentController                                           │
└────────────────────────┬────────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────────┐
│                    SERVICE LAYER                                 │
│  Business Logic (Core application functionality)                 │
│  ├─ AskService          (RAG pipeline coordination)              │
│  ├─ ChatService         (Conversation management)                │
│  ├─ PdfService          (PDF processing & chunking)              │
│  ├─ EmbeddingService    (Vector generation)                      │
│  ├─ DocumentService     (Document metadata)                      │
│  └─ RetrievalService    (Vector search)                          │
└────────────────────────┬────────────────────────────────────────┘
                         │
          ┌──────────────┼──────────────┐
          │              │              │
          ▼              ▼              ▼
    ┌─────────┐   ┌──────────┐   ┌─────────────┐
    │ Ollama  │   │PostgreSQL│   │ Apache Tika │
    │ Service │   │ pgvector │   │ (PDF Parser)│
    └─────────┘   └──────────┘   └─────────────┘
```

---

## Component Details

### 1. Controllers (Presentation Layer)

Handles HTTP routing, request validation, and response formatting.

**Key Controllers**:

#### AskController
```java
@PostMapping
public String ask(@RequestBody AskRequest request)
```
- Single responsibility: Answer questions using RAG
- Delegates to `AskService`
- Input: Question
- Output: String answer

#### ChatController
```java
@PostMapping
public ChatResponse chat(@RequestBody ChatRequest request)
```
- Multi-turn conversation support
- Maintains conversation context
- Delegates to `ChatService`

#### PdfController
```java
@PostMapping("/upload")
public ResponseEntity<UploadResponse> upload(MultipartFile file)
```
- Handles PDF file uploads
- Validates file type and size
- Delegates to `PdfService`

---

### 2. Services (Business Logic Layer)

Implements core business logic and coordinates components.

#### **AskService** - RAG Pipeline
```
Question → Embed → Search → Retrieve Context → Augment Prompt → LLM Response
```

**Flow**:
1. Receives user question
2. Generates embedding using `EmbeddingService`
3. Searches vector store for top-3 similar documents
4. Creates system prompt with context + question
5. Sends to LLM (Ollama Llama 3.1)
6. Returns LLM response

**Key Code**:
```java
List<Document> documents = vectorStore.similaritySearch(
    SearchRequest.builder()
        .query(question)
        .topK(3)
        .build()
);
```

**Hyperparameters**:
- `topK = 3`: Retrieve top 3 most similar chunks
- `temperature = 0.7` (default): Controls randomness in responses
- `maxTokens = 2048` (default): Limit response length

---

#### **PdfService** - Document Processing
```
PDF File → Text Extraction → Chunking → Embedding → Vector Store
```

**Flow**:
1. Receives PDF file
2. Extracts text using Apache Tika
3. Splits text into chunks (token-based)
4. Generates embeddings for each chunk
5. Stores in PostgreSQL pgvector

**Key Configuration**:
```properties
# Chunk size in tokens (approximately)
chunk.size=512
chunk.overlap=50
```

**Text Extraction**: Apache Tika handles various formats (PDF, DOCX, etc.)

**Chunking Strategy**:
- Token-based (preserves semantic units)
- Overlap enabled (50 tokens overlap between chunks)
- Prevents information loss at chunk boundaries

---

#### **EmbeddingService** - Vector Generation
```
Text → nomic-embed-text Model → 768-dimensional Vector
```

**Flow**:
1. Receives text
2. Calls Ollama's embedding model
3. Returns 768-dimensional vector
4. Cached for performance

**Model Details**:
- **Model**: `nomic-embed-text`
- **Output Dimension**: 768
- **Approach**: Local embedding (no external API)
- **Performance**: ~100ms per request

---

#### **RetrievalService** - Semantic Search
```
Query Vector → HNSW Index Search → Top-K Similar Vectors → Chunk Retrieval
```

**Similarity Metrics**:
- Cosine distance (default)
- Euclidean distance (configurable)

**HNSW Index**:
- Type: Hierarchical Navigable Small World
- M parameter: 16 (connections per element)
- ef_construction: 200 (construction parameter)
- Query time: O(log n) complexity

---

### 3. Data Layer

#### PostgreSQL + pgvector

**Tables**:

1. **ai_document** (Documents metadata)
```sql
CREATE TABLE ai_document (
    id UUID PRIMARY KEY,
    filename VARCHAR(255),
    content_type VARCHAR(100),
    upload_date TIMESTAMP,
    metadata JSONB
);
```

2. **ai_document_metadata** (Vector storage)
```sql
CREATE TABLE ai_document_metadata (
    id UUID PRIMARY KEY,
    document_id UUID REFERENCES ai_document(id),
    content TEXT,
    embedding vector(768),  -- 768-dimensional vector
    chunk_index INTEGER
);
```

**Indexes**:
```sql
-- HNSW index for fast similarity search
CREATE INDEX ON ai_document_metadata USING hnsw (embedding vector_cosine_ops)
WITH (m=16, ef_construction=200);
```

**Vector Operations**:
```sql
-- Cosine similarity search
SELECT * FROM ai_document_metadata
ORDER BY embedding <=> query_vector
LIMIT 3;
```

---

### 4. Integration with Ollama

**Two Models Used**:

#### Chat Model (Llama 3.1)
- Purpose: Generate responses
- Size: 8.1B parameters
- Endpoint: `http://localhost:11434/api/generate`
- Latency: 5-15 seconds per request

#### Embedding Model (nomic-embed-text)
- Purpose: Convert text to vectors
- Size: 274 MB
- Endpoint: `http://localhost:11434/api/embeddings`
- Latency: 100-300ms per request

---

## Data Flow Diagram

### Upload Flow
```
User
  ↓
[POST /documents/upload]
  ↓
PdfController.upload()
  ↓
PdfService.upload()
  ├─ Apache Tika extracts text
  ├─ Text is chunked
  ├─ EmbeddingService generates vectors
  └─ VectorStore.add() saves to pgvector
  ↓
PostgreSQL (pgvector table updated)
```

### Query Flow (RAG)
```
User Question
  ↓
[POST /ask]
  ↓
AskController.ask()
  ↓
AskService.ask()
  ├─ EmbeddingService.generateEmbedding(question)
  ├─ VectorStore.similaritySearch(top-3)
  │  └─ PostgreSQL HNSW search
  ├─ Format context from retrieved docs
  └─ ChatModel.call(prompt + context)
     └─ Ollama LLM inference
  ↓
LLM Response
  ↓
Return to User
```

---

## Design Patterns Used

### 1. **Dependency Injection (Spring)**
Services are injected using `@RequiredArgsConstructor` (Lombok)

```java
@Service
@RequiredArgsConstructor
public class AskService {
    private final VectorStore vectorStore;
    private final ChatModel chatModel;
}
```

### 2. **Repository Pattern**
Data access abstracted through Spring Data JPA

### 3. **Strategy Pattern**
Different retrieval strategies can be swapped (similarity search, BM25, etc.)

### 4. **Adapter Pattern**
Spring AI abstracts Ollama, making it easy to swap providers

---

## Scalability Considerations

### Horizontal Scaling
- Stateless services (can run multiple instances)
- Load balancer directs requests to different instances
- PostgreSQL acts as shared state

### Vertical Scaling
- Increase JVM heap for more concurrent requests
- Use GPU acceleration in Ollama for faster inference

### Database Optimization
- HNSW index efficiently handles millions of vectors
- Connection pooling (HikariCP) for database connections
- Query optimization with proper indexes

### Caching Opportunities
- Cache frequently used embeddings
- Redis for chat conversation history
- HTTP caching headers for API responses

---

## Error Handling

**Strategy**: Try-catch with custom exceptions and logging

```java
try {
    // Service logic
} catch (Exception e) {
    logger.error("Operation failed", e);
    throw new ServiceException("User-friendly message", e);
}
```

**Error Recovery**:
- Retry logic for transient failures
- Circuit breaker pattern for external services
- Graceful degradation when services unavailable

---

## Security Considerations

### Current State
- No authentication/authorization
- No rate limiting
- No input validation beyond basic checks

### Production Recommendations
1. **Authentication**: JWT, OAuth2
2. **Authorization**: Role-based access control
3. **Validation**: Input sanitization, file type verification
4. **Encryption**: TLS for data in transit, encryption at rest for DB
5. **Monitoring**: Logging, metrics, alerting
6. **API Security**: Rate limiting, CORS configuration

