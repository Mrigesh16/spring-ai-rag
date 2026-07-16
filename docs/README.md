# Spring AI RAG - Complete Documentation

Welcome to the Spring AI RAG project documentation. This guide will help you understand, set up, and use the application.

## 📚 Documentation Structure

### [1. Project Overview](01-OVERVIEW.md)
Start here to understand what this project does.

**Contents**:
- What is Spring AI RAG?
- Key concepts (RAG, Semantic Search, Vector DB)
- Architecture overview
- Technology stack
- Core features
- User workflows

**Best for**: New team members, stakeholders, understanding the big picture

---

### [2. Setup & Installation](02-SETUP.md)
Complete guide to install and configure the application.

**Contents**:
- Prerequisites & dependencies
- PostgreSQL + Docker setup
- Ollama installation & model download
- Application configuration
- Build & run instructions
- Troubleshooting guide
- Performance tuning

**Best for**: Developers setting up their environment, DevOps engineers

---

### [3. REST API Documentation](03-API.md)
Comprehensive API reference with examples.

**Contents**:
- All REST endpoints with examples
- Request/response formats
- HTTP status codes
- Error handling
- Rate limiting considerations
- Swagger UI information
- cURL examples

**Best for**: API consumers, frontend developers, integration engineers

---

### [4. Technical Architecture](04-ARCHITECTURE.md)
Deep-dive into technical design and implementation details.

**Contents**:
- System architecture diagram
- Component details (Controllers, Services, Data Layer)
- Data flow diagrams
- Design patterns used
- Database schema & indexing
- Integration with external services
- Scalability considerations
- Security recommendations

**Best for**: Backend developers, architects, code reviewers

---

### [5. Usage Examples & Tutorials](05-USAGE_EXAMPLES.md)
Practical examples and tutorials for common tasks.

**Contents**:
- Basic setup & first query
- Multi-document scenarios
- Conversational chat
- Debugging techniques
- Prompt engineering examples
- Python/JavaScript client examples
- Batch processing scripts
- Performance testing
- Monitoring & debugging

**Best for**: API users, integration engineers, DevOps

---

## 🚀 Quick Start

### For Developers
1. Read [Project Overview](01-OVERVIEW.md) (5 min)
2. Follow [Setup & Installation](02-SETUP.md) (15 min)
3. Try examples from [Usage Examples](05-USAGE_EXAMPLES.md) (10 min)
4. Explore [REST API](03-API.md) for available endpoints (10 min)

### For API Consumers
1. Check [REST API Documentation](03-API.md)
2. Try [Usage Examples](05-USAGE_EXAMPLES.md)
3. Use Swagger UI at `http://localhost:8080/swagger-ui.html`

### For Architects/Reviewers
1. Read [Project Overview](01-OVERVIEW.md)
2. Study [Technical Architecture](04-ARCHITECTURE.md)
3. Review code with architectural knowledge

---

## 🎯 Key Features

✅ **Document Upload & Processing**
- Upload PDFs and automatically extract text
- Intelligent chunking for optimal semantic representation

✅ **Vector Embeddings**
- Local embedding generation using nomic-embed-text
- No external API dependencies

✅ **Semantic Search**
- Find relevant documents using similarity search
- HNSW indexing for fast retrieval

✅ **RAG Pipeline**
- Answer questions grounded in uploaded documents
- Reduces hallucinations through context-aware responses

✅ **Conversational AI**
- Multi-turn chat with context awareness
- Natural dialogue flow

✅ **Local-First Design**
- All processing happens locally
- Complete data privacy control

---

## 📋 Prerequisites

- **Java 21+**
- **Docker & Docker Compose**
- **Ollama**
- **4GB+ RAM** (8GB+ recommended)
- **GPU** (optional, for faster inference)

---

## 🔧 Technology Stack

- **Backend**: Java 21, Spring Boot 3.5.4, Spring AI 1.0.1
- **Database**: PostgreSQL 16 + pgvector
- **Vector Index**: HNSW
- **LLM**: Ollama (Llama 3.1)
- **Embeddings**: nomic-embed-text
- **PDF Processing**: Apache Tika
- **API Documentation**: SpringDoc OpenAPI

---

## 📊 Architecture at a Glance

```
┌──────────────────────────┐
│   User/Client Apps       │
└──────────────┬───────────┘
               │ REST API
┌──────────────▼───────────┐
│   Spring Boot Services   │
│  (RAG, Chat, Embedding)  │
└────┬──────────┬──────┬───┘
     │          │      │
     ▼          ▼      ▼
  Ollama    PostgreSQL  Tika
  (LLM)    (pgvector)  (PDF)
```

---

## 🎓 Learning Path

### Level 1: User
- What does the app do? → Read [Overview](01-OVERVIEW.md)
- How do I use it? → Read [Usage Examples](05-USAGE_EXAMPLES.md)
- What APIs are available? → Read [API Documentation](03-API.md)

### Level 2: Developer
- How do I set it up? → Follow [Setup Guide](02-SETUP.md)
- How is it built? → Study [Architecture](04-ARCHITECTURE.md)
- How do I modify it? → Explore code + [Architecture](04-ARCHITECTURE.md)

### Level 3: Architect
- Why these design choices? → [Architecture](04-ARCHITECTURE.md)
- How does it scale? → See "Scalability" section in [Architecture](04-ARCHITECTURE.md)
- What are the security concerns? → See "Security" section in [Architecture](04-ARCHITECTURE.md)

---

## 📞 Common Tasks

### "I need to upload a document"
→ See [Usage Examples - Section 1](05-USAGE_EXAMPLES.md#1-basic-setup--first-query)

### "I need API documentation"
→ Read [REST API Documentation](03-API.md)

### "I need to set up the app"
→ Follow [Setup & Installation](02-SETUP.md)

### "I need to integrate with Python"
→ See [Usage Examples - Section 6](05-USAGE_EXAMPLES.md#6-python-script-example)

### "I need to integrate with JavaScript"
→ See [Usage Examples - Section 7](05-USAGE_EXAMPLES.md#7-javascriptnode-example)

### "I need to understand the design"
→ Read [Technical Architecture](04-ARCHITECTURE.md)

### "I need to troubleshoot"
→ Check [Setup - Troubleshooting](02-SETUP.md#troubleshooting)

### "I need to optimize performance"
→ See [Setup - Performance Tuning](02-SETUP.md#performance-tuning)

---

## 📈 Monitoring & Debugging

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### API Documentation (Interactive)
```
http://localhost:8080/swagger-ui.html
```

### Database Inspection
```bash
psql -U postgres -d vectordb
# Then run SQL queries to inspect data
```

### Logs
```bash
# Spring Boot logs
tail -f logs/spring.log

# Docker logs
docker logs -f spring-ai-rag
```

---

## 🔄 Development Workflow

### Making Changes
1. Edit code in `src/main/java`
2. Spring DevTools auto-reloads (no restart needed)
3. Test via API or Unit tests

### Running Tests
```bash
mvn test
```

### Building for Production
```bash
mvn clean package
```

### Deploying
- See [Setup Guide - Docker](02-SETUP.md)
- Use `docker-compose` for local deployment
- Package JAR for production deployment

---

## 🤝 Contributing

When contributing to this project:
1. Update relevant documentation
2. Follow existing code patterns
3. Add tests for new features
4. Update API docs if adding endpoints
5. Test thoroughly before submitting

---

## 📝 License

[Your License Here]

---

## ❓ FAQ

**Q: Can I use different LLM models?**
A: Yes, Ollama supports many models. See [Setup - Ollama Models](02-SETUP.md#3-install-ollama-models)

**Q: How many documents can I store?**
A: Theoretically unlimited, but performance depends on your PostgreSQL setup and indexing

**Q: Is it production-ready?**
A: The project demonstrates core RAG concepts. For production, add authentication, rate limiting, monitoring, and security hardening (see [Architecture - Security](04-ARCHITECTURE.md#security-considerations))

**Q: Can I use this with cloud LLM providers?**
A: Yes, Spring AI supports OpenAI, Azure OpenAI, etc. Modify configuration in `application.properties`

**Q: What's the typical response time?**
A: 5-15 seconds (depends on model size and hardware)

---

## 📚 Additional Resources

- [Spring AI Documentation](https://github.com/spring-projects/spring-ai)
- [Ollama Documentation](https://github.com/ollama/ollama)
- [pgvector Documentation](https://github.com/pgvector/pgvector)
- [Apache Tika Documentation](https://tika.apache.org/)

---

**Last Updated**: 2026-07-16  
**Version**: 1.0.0

For questions or issues, please refer to the relevant documentation section or check the troubleshooting guide.
