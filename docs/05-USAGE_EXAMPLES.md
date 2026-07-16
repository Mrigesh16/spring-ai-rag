# Usage Examples & Tutorials

## 1. Basic Setup & First Query

### Step 1: Start all services

```bash
# Terminal 1: Start PostgreSQL
docker-compose up -d

# Terminal 2: Start Ollama
ollama serve

# Terminal 3: Start Spring Boot app
mvn spring-boot:run
```

### Step 2: Upload a document

```bash
# Save sample PDF (or use any PDF)
curl -X POST \
  -F "file=@example.pdf" \
  http://localhost:8080/documents/upload

# Response
# {"message":"Uploaded Successfully"}
```

### Step 3: Ask a question

```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"question":"What are the main topics in this document?"}' \
  http://localhost:8080/ask

# Response (text)
# Based on the document, the main topics are: ...
```

---

## 2. Multi-Document Scenario

### Upload multiple documents

```bash
# Upload document 1
curl -X POST -F "file=@report1.pdf" http://localhost:8080/documents/upload

# Upload document 2
curl -X POST -F "file=@report2.pdf" http://localhost:8080/documents/upload

# Upload document 3
curl -X POST -F "file=@report3.pdf" http://localhost:8080/documents/upload
```

### Query across documents

```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"question":"Compare the findings across all reports"}' \
  http://localhost:8080/ask
```

**What happens**:
1. Question is embedded
2. Top-3 most relevant chunks from ANY document are retrieved
3. Context combined and sent to LLM
4. LLM synthesizes comparison based on all documents

---

## 3. Conversational Chat Example

### Start a conversation

```bash
# User message 1
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Tell me about customer retention strategies",
    "conversationId": "chat-session-001"
  }' \
  http://localhost:8080/chat

# Response
# {"response": "The documents mention several customer retention strategies...", "conversationId": "chat-session-001"}
```

### Follow-up question

```bash
# User message 2 (same conversation)
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Can you elaborate on the first strategy?",
    "conversationId": "chat-session-001"
  }' \
  http://localhost:8080/chat

# Response references previous context
# {"response": "Regarding the first strategy we discussed, which is...", "conversationId": "chat-session-001"}
```

---

## 4. Debugging: Check Embeddings

### Generate embedding for a phrase

```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"text":"revenue growth"}' \
  http://localhost:8080/embedding

# Response
{
  "embedding": [0.123, -0.456, 0.789, ...],  // 768-dimensional array
  "dimension": 768,
  "model": "nomic-embed-text"
}
```

### Use embedding for manual similarity search

```bash
# Get embedding for query
QUERY_EMBEDDING=$(curl -X POST -H "Content-Type: application/json" \
  -d '{"text":"How did revenue change?"}' \
  http://localhost:8080/embedding | jq '.embedding')

# Manual search in database (for debugging)
psql -U postgres -d vectordb -c "
SELECT id, content, embedding <=> '$QUERY_EMBEDDING'::vector as distance
FROM ai_document_metadata
ORDER BY distance ASC
LIMIT 5;
"
```

---

## 5. Advanced: Custom Prompt Engineering

### Example 1: Summarization

```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"question":"Create a 3-sentence summary of the main findings"}' \
  http://localhost:8080/ask
```

### Example 2: Fact Extraction

```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"question":"List all mentioned company names and their revenue figures"}' \
  http://localhost:8080/ask
```

### Example 3: Comparison

```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"question":"What are the differences between Method A and Method B as described in the documents?"}' \
  http://localhost:8080/ask
```

---

## 6. Python Script Example

### Using `requests` library

```python
import requests
import json

BASE_URL = "http://localhost:8080"

def upload_pdf(file_path):
    """Upload a PDF document"""
    with open(file_path, 'rb') as f:
        files = {'file': f}
        response = requests.post(f"{BASE_URL}/documents/upload", files=files)
    return response.json()

def ask_question(question):
    """Ask a question using RAG"""
    payload = {"question": question}
    response = requests.post(f"{BASE_URL}/ask", json=payload)
    return response.text

def chat(message, conversation_id="default"):
    """Send a chat message"""
    payload = {
        "message": message,
        "conversationId": conversation_id
    }
    response = requests.post(f"{BASE_URL}/chat", json=payload)
    return response.json()

def get_embeddings(text):
    """Get embeddings for text"""
    payload = {"text": text}
    response = requests.post(f"{BASE_URL}/embedding", json=payload)
    return response.json()

# Usage
if __name__ == "__main__":
    # Upload document
    result = upload_pdf("sample.pdf")
    print("Upload result:", result)
    
    # Ask question
    answer = ask_question("What are the main topics?")
    print("Answer:", answer)
    
    # Chat
    chat_response = chat("Tell me more about topic X", "session-1")
    print("Chat response:", chat_response)
    
    # Get embeddings
    embeddings = get_embeddings("sample text")
    print("Embedding dimension:", embeddings['dimension'])
```

---

## 7. JavaScript/Node.js Example

### Using `fetch` API

```javascript
const BASE_URL = "http://localhost:8080";

async function uploadPDF(file) {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await fetch(`${BASE_URL}/documents/upload`, {
    method: 'POST',
    body: formData
  });
  return await response.json();
}

async function askQuestion(question) {
  const response = await fetch(`${BASE_URL}/ask`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ question })
  });
  return await response.text();
}

async function chat(message, conversationId = 'default') {
  const response = await fetch(`${BASE_URL}/chat`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ message, conversationId })
  });
  return await response.json();
}

async function getEmbeddings(text) {
  const response = await fetch(`${BASE_URL}/embedding`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ text })
  });
  return await response.json();
}

// Usage
(async () => {
  const fileInput = document.getElementById('fileInput');
  const result = await uploadPDF(fileInput.files[0]);
  console.log('Upload result:', result);
  
  const answer = await askQuestion('What is the document about?');
  console.log('Answer:', answer);
})();
```

---

## 8. Batch Processing Example

### Process multiple files in sequence

```bash
#!/bin/bash

# Directory containing PDFs
PDF_DIR="./documents"

# Upload all PDFs
for pdf_file in "$PDF_DIR"/*.pdf; do
  echo "Uploading: $pdf_file"
  curl -X POST \
    -F "file=@$pdf_file" \
    http://localhost:8080/documents/upload
  echo "✓ Uploaded"
  sleep 2  # Rate limiting
done

# Ask comprehensive question
echo "Processing question across all documents..."
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"question":"Synthesize findings from all documents"}' \
  http://localhost:8080/ask
```

---

## 9. Docker-based Testing

### Using Docker to test without local dependencies

```dockerfile
FROM curlimages/curl:latest

COPY test.sh /test.sh
RUN chmod +x /test.sh

CMD ["/test.sh"]
```

### Test script (`test.sh`)

```bash
#!/bin/sh

API_URL="http://spring-app:8080"

# Wait for API to be ready
sleep 10

# Test health
echo "Testing health..."
curl $API_URL/actuator/health

# Test upload
echo "Testing upload..."
curl -X POST \
  -F "file=@test.pdf" \
  $API_URL/documents/upload

# Test ask
echo "Testing RAG..."
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"question":"test"}' \
  $API_URL/ask

echo "All tests completed"
```

---

## 10. Performance Testing

### Load test with `wrk`

```bash
# Install wrk
# brew install wrk (macOS)
# apt-get install wrk (Linux)

# Simple load test
wrk -t4 -c100 -d30s http://localhost:8080/actuator/health

# Custom script for RAG endpoint
cat > test.lua << 'EOF'
request = function()
   wrk.method = "POST"
   wrk.headers["Content-Type"] = "application/json"
   wrk.body = '{"question":"What are the key findings?"}'
   return wrk.format(nil, "/ask")
end
EOF

wrk -t4 -c50 -d60s -s test.lua http://localhost:8080
```

---

## 11. Monitoring & Debugging

### Check application logs

```bash
# View Spring Boot logs
docker logs -f <container-id>

# View Ollama logs
tail -f ~/.ollama/logs.txt
```

### Monitor database

```bash
# Connect to PostgreSQL
psql -U postgres -d vectordb

# Check document count
SELECT COUNT(*) FROM ai_document;

# Check vector metadata count
SELECT COUNT(*) FROM ai_document_metadata;

# Check index size
SELECT pg_size_pretty(pg_relation_size('ai_document_metadata')) as size;
```

### Performance metrics

```bash
# JVM metrics via actuator
curl http://localhost:8080/actuator/metrics

# Specific metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.used
curl http://localhost:8080/actuator/metrics/http.server.requests
```

