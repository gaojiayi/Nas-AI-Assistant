# NAS AI Assistant - Intelligent Network Attached Storage AI Platform

<div align="center">

English | [Chinese](README_CN.md)

[![Java](https://img.shields.io/badge/Java-21-orange)]
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)]
[![Vue](https://img.shields.io/badge/Vue-3.4.0-green)]
[![License](https://img.shields.io/badge/License-MIT-yellow)]
[![Docker](https://img.shields.io/badge/Docker-Ready-blue)]

Built with Spring Boot 3.2 + Java 21 + Vue 3, featuring AI-powered NAS management, intelligent chat assistant, community sharing, and knowledge base. Supports VPN setup guidance, backup strategies, and system monitoring. Clean architecture, comprehensive documentation - perfect for NAS enthusiasts and AI application developers.

[Features](#-features) · [Screenshots](#-screenshots) · [Quick Start](#-quick-start) · [Architecture](#-tech-architecture)

</div>

---

## Screenshots

### AI Assistant

<p align="center">
  <img src="doc/ai%E5%8A%A9%E6%89%8B.jpg" alt="AI Assistant" width="800"/>
  <br/>
  <em>AI Assistant - Smart NAS Management Chat</em>
</p>

**Highlights:**

-  Clean and modern chat interface
-  Real-time streaming responses
-  Conversation history management
-  Context-aware NAS assistance

### Community Experience

<p align="center">
  <img src="doc/%E4%BD%93%E9%AA%8C%E7%A4%BE%E5%8C%BA.png" alt="Community" width="800"/>
  <br/>
  <em>Community - NAS Application Showcase</em>
</p>

**Highlights:**

-  Carousel showcasing NAS applications
-  VPN setup tutorials and guides
-  Real-world use cases and solutions
-  Interactive community features

### Knowledge Base

<p align="center">
  <img src="doc/%E7%9F%A5%E8%AF%86%E5%BA%93%E7%AE%A1%E7%90%86.png" alt="Knowledge Base" width="800"/>
  <br/>
  <em>Knowledge Base - Document Management System</em>
</p>

**Highlights:**

-  Document upload and management
-  PDF and Markdown support
-  Search and categorization
-  Knowledge organization

---

## About

NAS AI Assistant is a **production-grade AI-powered platform** specifically designed for Network Attached Storage (NAS) users, combining intelligent chat assistance with comprehensive NAS management features.

### Core Applications

| Application | Description |
| ----------- | ----------- |
| **AI Assistant** | Smart chat for NAS management, troubleshooting, and guidance |
| **Community** | Share and discover NAS applications, tutorials, and solutions |
| **Knowledge Base** | Organized documentation and guides for NAS users |

### Why This Project?

| Feature | Description |
| ------- | ----------- |
| **NAS Focused** | Tailored specifically for NAS users and administrators |
| **AI-Powered** | Intelligent assistance for complex NAS operations |
| **Community Driven** | Share experiences and solutions with other users |
| **Production Ready** | Complete monitoring, logging, and error handling |
| **Easy Deploy** | One-click Docker deployment with all dependencies |

---

## Features

### AI Assistant

-  **Smart Chat**: Context-aware conversations about NAS management
-  **Streaming Responses**: Real-time AI responses with typewriter effect
-  **Conversation History**: Persistent chat sessions with search capability
-  **NAS Expertise**: Specialized knowledge for NAS troubleshooting and optimization

### Community

-  **Application Showcase**: Discover and share NAS applications
-  **Tutorials & Guides**: Step-by-step setup instructions (VPN, backup, etc.)
-  **User Experiences**: Real-world use cases and solutions
-  **Interactive Carousel**: Browse featured content and tutorials

### Knowledge Base

-  **Document Management**: Upload and organize PDF and Markdown files
-  **Smart Search**: Find relevant information quickly
-  **Categorization**: Organize content by topics and tags
-  **Version Control**: Track document updates and changes

---

## Tech Architecture

### System Architecture Diagram

```
                    +------------------------+
                    |     Frontend Layer     |
                    |       (Vue 3)          |
                    +------------------------+
                    |  AI Assistant | Community | Knowledge |
                    +------------------------+
                    |  Chat Window  | Carousel  | Document  |
                    +------------------------+
                              |
                    HTTP/SSE |
                              v
                    +------------------------+
                    |    Backend Layer       |
                    |    (Spring Boot)       |
                    +------------------------+
                    |  ChatController |      |
                    |  CommunityController |  |
                    |  KnowledgeController | |
                    +------------------------+
                              |
                              v
                    +------------------------+
                    |     Service Layer      |
                    +------------------------+
                    |  ChatService |         |
                    |  CommunityService |    |
                    |  KnowledgeService |   |
                    +------------------------+
                              |
                              v
                    +------------------------+
                    |      Data Layer        |
                    +------------------------+
                    |  MySQL (Chat History)  |
                    |  File System (Docs)    |
                    |  AI API (External)     |
                    +------------------------+
```

### Backend Stack

| Technology | Version | Description |
| ---------- | ------- | ----------- |
| Java | 21 | Programming language |
| Spring Boot | 3.2.0 | Application framework |
| Spring AI | 1.0.0 | AI integration framework |
| MyBatis-Plus | 3.5.12 | ORM framework |
| MySQL | 8.0+ | Data persistence |
| Docker | 20.10+ | Containerization |

### Frontend Stack

| Technology | Version | Description |
| ---------- | ------- | ----------- |
| Vue | 3.4.0 | Frontend framework |
| Vue Router | 4.2.0 | Routing management |
| Axios | 1.6.0 | HTTP client |
| Vite | 5.0.0 | Build tool |
| Tailwind CSS | 3.4.0 | Styling framework |

### AI Integration

| Component | Description |
| --------- | ----------- |
| **Chat Model** | OpenAI GPT / Local Models |
| **RAG System** | Knowledge retrieval and augmentation |
| **Context Management** | Conversation state persistence |
| **Tool Integration** | NAS management utilities |

---

## Quick Start

### Option 1: Docker Compose (Recommended)

The easiest way to get started with all dependencies:

```bash
# 1. Clone the repository
git clone https://github.com/your-username/Nas-AI-Assistant.git
cd Nas-AI-Assistant

# 2. Set environment variables
cp .env.example .env
# Edit .env with your API keys and configurations

# 3. Start all services
docker-compose -f docker-compose.local.yml up --build

# 4. Access the application
# Frontend: http://localhost:5123
# Backend API: http://localhost:8123
```

### Option 2: Local Development

#### Prerequisites

-  Java 21+
-  Node.js 18+
-  Maven 3.8+
-  MySQL 8.0+

#### Backend Setup

```bash
# 1. Navigate to backend directory
cd nas-ai-assistant-app

# 2. Configure database
# Edit src/main/resources/application.yml

# 3. Build and run
mvn clean package -DskipTests
java -jar target/nas-ai-assistant-app-0.0.1-SNAPSHOT.jar
```

#### Frontend Setup

```bash
# 1. Navigate to frontend directory
cd nas-ai-assistant-ui

# 2. Install dependencies
npm install

# 3. Start development server
npm run dev
```

---

## Configuration

### Environment Variables

Create a `.env` file in the root directory:

```env
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=nas_ai_assistant
DB_USERNAME=root
DB_PASSWORD=your_password

# AI Configuration
OPENAI_API_KEY=your_openai_api_key
AI_MODEL=gpt-3.5-turbo

# Application Configuration
SERVER_PORT=8123
FRONTEND_PORT=5123
```

### Database Setup

```sql
-- Create database
CREATE DATABASE nas_ai_assistant CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Tables are auto-created by Spring Boot JPA
```

---

## Project Structure

### Overall Project Structure

```
Nas-AI-Assistant/
nas-ai-assistant-app/          # Backend Spring Boot application
nas-ai-assistant-ui/           # Frontend Vue application
doc/                          # Documentation and screenshots
docker-compose.local.yml      # Docker configuration
.env.example                  # Environment variables template
README.md                     # English documentation
README_CN.md                  # Chinese documentation
```

### Backend Structure (nas-ai-assistant-app/)

```
nas-ai-assistant-app/
src/main/java/com/gaojiayi/nasaiassistantapp/
  controller/                  # REST API Controllers
    ChatController.java        # Chat endpoints
    CommunityController.java   # Community endpoints
    KnowledgeController.java   # Knowledge base endpoints
  service/                     # Business Logic
    ChatService.java           # Chat management
    CommunityService.java      # Community content
    KnowledgeService.java      # Document management
  entity/                      # Data Models
    Message.java               # Chat message entity
    Conversation.java         # Conversation entity
    Document.java              # Document entity
  config/                      # Configuration Classes
    WebConfig.java             # Web configuration
    CorsConfig.java           # CORS configuration
  dto/                         # Data Transfer Objects
    MessageVO.java             # Message view object
  NasAiAssistantApp.java      # Main application class
src/main/resources/
  application.yml             # Application configuration
  documents/                   # Knowledge base documents
    nas-*.md                  # NAS related documentation
target/                       # Build output
pom.xml                      # Maven configuration
Dockerfile                   # Docker configuration
```

### Frontend Structure (nas-ai-assistant-ui/)

```
nas-ai-assistant-ui/
src/
  components/                  # Reusable Vue Components
    MainContent.vue           # Main content area
    CommunityCard.vue          # Community card component
    FeatureCard.vue            # Feature card component
    Sidebar.vue                # Navigation sidebar
  views/                       # Page Views
    ChatWindow.vue             # Chat interface
    Community.vue              # Community page
    Knowledge.vue              # Knowledge base page
  services/                    # API Services
    api.js                     # API client configuration
  mock/                        # Mock Data
    communityData.js           # Community mock data
  router/                      # Routing Configuration
    index.js                   # Vue Router setup
  App.vue                     # Root component
  main.js                     # Application entry point
public/                       # Static assets
  images/                     # Image resources
    tiktok.png                # Community images
    backup.png
package.json                 # NPM dependencies
vite.config.js               # Vite configuration
Dockerfile                   # Docker configuration
```

---

## Usage Examples

### AI Assistant

```
User: How do I set up a VPN on my Synology NAS?

AI: I'll help you set up a VPN on your Synology NAS. Here are the steps:
1. Log in to your DSM interface
2. Go to Control Panel > VPN Server
3. Enable the VPN service (PPTP/L2TP/OpenVPN)
4. Configure user permissions
5. Set up port forwarding on your router
6. Connect your client device

Would you like detailed instructions for any specific step?
```

### Community Features

-  Browse VPN setup tutorials
-  Share backup strategies
-  Discover new applications
-  Learn from user experiences

### Knowledge Base

-  Upload technical documentation
-  Search for specific topics
-  Organize guides by category
-  Share knowledge with community

---

## Development Guide

### Adding New Features

1. **Backend**: Create new controllers and services in `nas-ai-assistant-app`
2. **Frontend**: Add components and views in `nas-ai-assistant-ui`
3. **API Integration**: Update service classes for new endpoints
4. **Documentation**: Add guides to the knowledge base

### Mock Data Development

The frontend includes mock data for development:

```javascript
// src/mock/communityData.js
export const communityCardsMock = [
  {
    title: 'VPN Setup Guide',
    description: 'Complete guide for NAS VPN configuration',
    image: 'vpn'
  }
]
```

---

## FAQ

### Q: How do I reset the database?

```bash
# Using Docker
docker-compose down -v
docker-compose up --build

# Manual
mysql -u root -p -e "DROP DATABASE nas_ai_assistant;"
mysql -u root -p -e "CREATE DATABASE nas_ai_assistant;"
```

### Q: Can I use a different AI model?

Yes, update the configuration in `application.yml`:

```yaml
ai:
  model: "gpt-4"  # or your preferred model
```

### Q: How do I add documents to the knowledge base?

Place Markdown or PDF files in `src/main/resources/documents/` directory.

---

## Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot) - Application framework
- [Vue.js](https://vuejs.org/) - Frontend framework
- [Spring AI](https://spring.io/projects/spring-ai) - AI integration
- [NAS Community](https://www.synology.com/) - NAS platform and community

---

## Contact

- GitHub: [@your-username](https://github.com/your-username)
- Repository: [Nas-AI-Assistant](https://github.com/your-username/Nas-AI-Assistant)
- Issues: [Report a bug](https://github.com/your-username/Nas-AI-Assistant/issues)

---

<div align="center">

**If this project helps you manage your NAS better, please give it a star!**

Made with for NAS enthusiasts and AI developers

</div>