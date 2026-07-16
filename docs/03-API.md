# REST API Documentation

## Base URL

```
http://localhost:8080
```

## Authentication

Currently, the API has no authentication. For production, implement security measures like JWT or OAuth2.

## API Endpoints

### 1. Upload Documents

**Endpoint**: `POST /documents/upload`

**Description**: Upload a PDF document to the vector store.

**Parameters**:
- `file` (required, multipart/form-data) - PDF file to upload

**cURL Example**:
```bash
curl -X POST \
  -F "file=@/path/to/document.pdf" \
  http://localhost:8080/documents/upload
```

**Response** (Success - 200 OK):
```json
{
  "message": "Uploaded Successfully"
}
```

**Response** (Error - 400 Bad Request):
```json
{
  "error": "File is required",
  "timestamp": "2026-07-16T18:56:07Z"
}
```

**Process**:
1. PDF file is received
2. Text extracted using Apache Tika
3. Text split into chunks using token-based chunking
4. Each chunk is embedded using `nomic-embed-text` model
5. Embeddings stored in PostgreSQL pgvector

---

### 2. Ask Questions (RAG)

**Endpoint**: `POST /ask`

**Description**: Ask a question about uploaded documents using RAG pipeline.

**Request Body**:
```json
{
  "question": "What is the main topic of the documents?"
}
```

**cURL Example**:
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"question": "What are the key findings?"}' \
  http://localhost:8080/ask
```

**Response** (Success - 200 OK):
```
The key findings include... [Answer based on document context]
```

**Process**:
1. Question is converted to embedding
2. Vector database searches for top-3 similar document chunks
3. Retrieved context combined with question
4. LLM (Llama 3.1) generates answer based on context
5. Answer returned to user

**Notes**:
- Response is grounded in uploaded documents
- If context is insufficient, returns "I don't have enough information"
- Typical response time: 5-15 seconds

---

### 3. Chat Endpoint

**Endpoint**: `POST /chat`

**Description**: Multi-turn conversational interface.

**Request Body**:
```json
{
  "message": "Hello, can you help me understand the documents?",
  "conversationId": "conv-123" // Optional
}
```

**cURL Example**:
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "message": "What is mentioned about customer retention?",
    "conversationId": "conv-session-1"
  }' \
  http://localhost:8080/chat
```

**Response** (Success - 200 OK):
```json
{
  "response": "Based on the documents, customer retention...",
  "conversationId": "conv-session-1",
  "timestamp": "2026-07-16T18:56:07Z"
}
```

**Notes**:
- Conversation context is maintained per session
- Previous messages inform LLM responses
- Supports multi-turn dialogue

---

### 4. Generate Embeddings

**Endpoint**: `POST /embedding`

**Description**: Generate vector embeddings for text.

**Request Body**:
```json
{
  "text": "Sample text to embed"
}
```

**cURL Example**:
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"text": "What are embeddings?"}' \
  http://localhost:8080/embedding
```

**Response** (Success - 200 OK):
```json
{
  "embedding": [0.123, -0.456, 0.789, ...],
  "dimension": 768,
  "model": "nomic-embed-text"
}
```

**Technical Details**:
- Embedding dimension: 768
- Model: `nomic-embed-text`
- Normalized: false
- Useful for: custom similarity searches, debugging, experimentation

---

### 5. Retrieve Documents

**Endpoint**: `GET /documents`

**Description**: List all indexed documents.

**Query Parameters**:
- `limit` (optional, default: 10) - Number of documents to return
- `offset` (optional, default: 0) - Pagination offset

**cURL Example**:
```bash
curl "http://localhost:8080/documents?limit=20&offset=0"
```

**Response** (Success - 200 OK):
```json
{
  "documents": [
    {
      "id": "doc-001",
      "filename": "document1.pdf",
      "uploadDate": "2026-07-16T18:56:07Z",
      "chunkCount": 45,
      "contentPreview": "This document contains information about..."
    },
    {
      "id": "doc-002",
      "filename": "document2.pdf",
      "uploadDate": "2026-07-16T18:56:07Z",
      "chunkCount": 32,
      "contentPreview": "Another important document..."
    }
  ],
  "total": 2,
  "limit": 20,
  "offset": 0
}
```

---

### 6. Delete Document

**Endpoint**: `DELETE /documents/{documentId}`

**Description**: Remove a document and its embeddings from the vector store.

**Parameters**:
- `documentId` (path, required) - ID of document to delete

**cURL Example**:
```bash
curl -X DELETE \
  http://localhost:8080/documents/doc-001
```

**Response** (Success - 204 No Content):
```
(Empty response body)
```

**Response** (Not Found - 404):
```json
{
  "error": "Document not found",
  "documentId": "doc-001"
}
```

---

## HTTP Status Codes

| Code | Meaning | Typical Cause |
|------|---------|---------------|
| 200 | OK | Request succeeded |
| 201 | Created | Resource created successfully |
| 204 | No Content | Delete/update successful |
| 400 | Bad Request | Invalid input parameters |
| 404 | Not Found | Resource doesn't exist |
| 500 | Server Error | Internal application error |
| 503 | Service Unavailable | External service (Ollama/DB) unreachable |

---

## Error Response Format

All errors follow this format:

```json
{
  "error": "Description of what went wrong",
  "code": "ERROR_CODE",
  "timestamp": "2026-07-16T18:56:07Z",
  "details": {
    "field": "explanation"
  }
}
```

---

## Rate Limiting & Performance

- **No built-in rate limiting** (implement in production)
- **Concurrent requests**: Limited by thread pool (default: 10-20)
- **Timeout**: 30 seconds per request
- **Max upload size**: 10 MB (configurable)

---

## Testing Endpoints with Swagger UI

Interactive API documentation available at:

```
http://localhost:8080/swagger-ui.html
```

Features:
- ✅ Test all endpoints directly in browser
- ✅ Auto-generated documentation
- ✅ Request/response examples
- ✅ Schema validation

---

## API Evolution & Versioning

Current version: **v1** (implicit in all endpoints)

Future versions will use:
- `/api/v2/ask` for breaking changes
- Backward compatibility maintained where possible

