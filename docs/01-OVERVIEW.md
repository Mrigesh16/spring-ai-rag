# Spring AI RAG - Project Overview

## What is This Project?

**Spring AI RAG** is a **Retrieval-Augmented Generation (RAG)** application that enables intelligent document querying using local AI models. Instead of relying solely on an LLM's pre-trained knowledge, it retrieves relevant information from uploaded documents to provide accurate, context-aware answers.

## Key Concepts

### Retrieval-Augmented Generation (RAG)
RAG is a technique that combines:
1. **Document Retrieval**: Finding relevant documents/chunks using semantic search
2. **LLM Generation**: Using the retrieved context to generate accurate answers

This approach ensures answers are grounded in actual document content, reducing hallucinations.

### Semantic Search
Rather than keyword matching, semantic search uses **embeddings** (vector representations) to understand the meaning of text and find conceptually similar content, even if exact keywords don't match.

### Vector Database
PostgreSQL with **pgvector** extension stores document embeddings and performs similarity searches using **HNSW** (Hierarchical Navigable Small World) indexes for efficient retrieval.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    REST API Endpoints                       │
│  /documents/upload  |  /ask  |  /chat  |  /embedding        │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   Spring Boot Services                      │
│  ├─ PdfService          (PDF processing & chunking)         │
│  ├─ EmbeddingService    (Vector generation)                 │
│  ├─ AskService          (RAG pipeline)                      │
│  ├─ ChatService         (Conversational AI)                 │
│  └─ DocumentService     (Vector store management)           │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│              External Services & Databases                  │
│  ├─ Ollama (LLM & Embeddings)                               │
│  └─ PostgreSQL + pgvector (Vector Storage)                  │
└─────────────────────────────────────────────────────────────┘
```

## Technology Stack

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **Language** | Java 21 | Modern, performant language with strong typing |
| **Framework** | Spring Boot 3.5.4 | RESTful API development |
| **AI Framework** | Spring AI 1.0.1 | Unified AI abstraction layer |
| **Local LLM** | Ollama + Llama 3.1 | Running LLMs locally (privacy-first) |
| **Embeddings** | nomic-embed-text | Fast, efficient text embeddings |
| **Database** | PostgreSQL | Reliable relational database |
| **Vector Store** | pgvector | Native vector storage in PostgreSQL |
| **Vector Index** | HNSW | Fast similarity search |
| **PDF Processing** | Apache Tika | Extracting text from PDFs |
| **API Documentation** | SpringDoc OpenAPI | Auto-generated OpenAPI/Swagger docs |
| **Containerization** | Docker Compose | Local development environment |

## Core Features

✅ **Document Upload & Processing**
- Upload PDF documents via REST API
- Automatic text extraction using Apache Tika
- Intelligent token-based chunking

✅ **Vector Generation**
- Generate embeddings for document chunks
- Local embedding model (no external API calls)

✅ **Semantic Search**
- Query vector database for relevant content
- HNSW indexing for fast retrieval

✅ **RAG-Powered Q&A**
- Answer questions using document context
- Prevents hallucinations by grounding responses in source material

✅ **Conversational AI**
- Multi-turn chat with context awareness
- System-aware prompting

✅ **Local-First Design**
- No cloud dependencies
- Complete privacy control over data
- Cost-effective inference

## User Workflows

### 1. Document Indexing
```
User uploads PDF → Tika extracts text → Chunked into segments → 
Embeddings generated → Stored in pgvector → Ready for queries
```

### 2. Question Answering (RAG)
```
User asks question → Generate question embedding → 
Search vector DB for top-K similar documents → 
Send context + question to LLM → LLM generates answer
```

### 3. Conversational Chat
```
User sends message → Maintain conversation history → 
Generate embeddings for context → Search documents if needed → 
LLM processes full context → Return conversational response
```

## Getting Started

See [SETUP.md](02-SETUP.md) for detailed installation and configuration instructions.

## API Documentation

See [API.md](03-API.md) for comprehensive REST API endpoint documentation.

## Architecture Details

See [ARCHITECTURE.md](04-ARCHITECTURE.md) for deep-dive technical architecture.
