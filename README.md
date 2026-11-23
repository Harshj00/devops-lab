# DevOps Capstone Project: Cloud-Native Application Deployment Pipeline

**Course:** DevOps & Automation Lab (ENSP461)  
**Degree:** B.Tech Computer Science & Engineering — Semester VII  
**Student Name:** Harsh Jha  
**Docker Hub Image:** `harshj00/devops-lab:1.0`

---

## 1. Project Overview
This project delivers a complete, production-grade DevOps lifecycle implementation for a Cloud-Native Student Management System built on Java 21. It integrates automated Continuous Integration (CI), Continuous Deployment (CD), Containerization, Kubernetes Orchestration, Infrastructure as Code (Terraform), Configuration Management (Ansible), Security Scanning (SonarQube & Trivy), GitOps (ArgoCD), and Observability (Prometheus & Grafana).

---

## 2. Architecture & Pipeline Workflow

```
[Developer] ──> [Git Commit & Push] ──> [GitHub Repository]
                                                 │
                                         [Webhook Trigger]
                                                 ▼
                                     [Jenkins CI/CD Pipeline]
                                       ├─ Checkout & Lint
                                       ├─ SonarQube Static Analysis
                                       ├─ Compile & JUnit Test (Java 21)
                                       ├─ Package Fat JAR Artifact
                                       ├─ Trivy Vulnerability Scan
                                       ├─ Docker Multi-Stage Build
                                       └─ Push Image to Docker Hub
                                                 │
                                      [Kubernetes / GitOps]
                                       ├─ ArgoCD Auto-Sync
                                       ├─ Deployment (3 Replicas)
                                       ├─ ConfigMap & Secrets Injection
                                       ├─ PVC Volume Mount
                                       ├─ Blue-Green Traffic Switch
                                       └─ NodePort Service (Port 30080)
                                                 │
                                       [Observability Stack]
                                       ├─ Prometheus Scrapes /metrics
                                       └─ Grafana Real-Time Dashboards
```

---

## 3. Technology Stack & Directory Structure

```
devops-lab/
├── .gitignore
├── Jenkinsfile                 # Multi-stage CI/CD pipeline definition
├── Dockerfile                  # Multi-stage Docker build (Alpine JRE runtime)
├── docker-compose.yml          # Multi-container orchestration (App + PostgreSQL)
├── pom.xml                     # Maven project configuration & plugins
├── ansible/                    # Configuration management playbooks
│   ├── inventory.ini
│   ├── docker_install.yml
│   ├── app_deploy.yml
│   └── site.yml
├── k8s/                        # Kubernetes production manifests
│   ├── configmap.yaml
│   ├── secret.yaml
│   ├── pvc.yaml
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── rbac.yaml
│   ├── blue-green-deployment.yaml
│   └── argocd-application.yaml
├── monitoring/                 # Observability configuration
│   ├── prometheus.yml
│   └── grafana-dashboard.json
├── terraform/                  # Infrastructure as Code (HCL)
│   └── main.tf
├── src/                        # Java 21 Application & Unit Tests
│   ├── main/java/com/devopslab/App.java
│   └── test/java/com/devopslab/AppTest.java
└── presentation/               # Capstone Presentation & Defense Deliverables
    ├── PROJECT_AUDIT.md
    ├── ARCHITECTURE.md
    ├── PRESENTATION_SCRIPT.md
    └── DEMO_RUNBOOK.md
```

---

## 4. Quick Start & Local Execution

### Compile and Package JAR
```powershell
javac -d target/classes src/main/java/com/devopslab/*.java
jar --create --file target/devops-lab-1.0-SNAPSHOT.jar --main-class com.devopslab.App -C target/classes .
```

### Run Application Locally
```powershell
java -jar target/devops-lab-1.0-SNAPSHOT.jar
```
Visit the interactive dashboard in your browser at:
`http://localhost:8080`

### Available Endpoints
- **Web UI:** `http://localhost:8080/`
- **Health Check:** `http://localhost:8080/health`
- **Prometheus Metrics:** `http://localhost:8080/metrics`
- **REST CRUD API:** `http://localhost:8080/api/students`
- **User Authentication:** `http://localhost:8080/api/login`
