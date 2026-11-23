# Cloud-Native End-to-End DevOps Architecture

**Project Title:** Design and Implementation of an End-to-End DevOps Pipeline for Cloud-Native Application Deployment  
**Course Code:** ENSP461 — DevOps & Automation Lab  
**Author:** Harsh Jha  

---

## 1. High-Level System Architecture Diagram

```mermaid
flowchart TD
    subgraph DEV ["1. Source & Version Control"]
        Dev["Developer"] -->|git commit & push| GitRepo["GitHub Repository\n(Branches: master, develop, feature)"]
    end

    subgraph CI ["2. Continuous Integration & Quality (Jenkins)"]
        GitRepo -->|Webhook Trigger| Jenkins["Jenkins CI Pipeline"]
        Jenkins --> Stage1["Static Code Analysis\n(SonarQube)"]
        Stage1 --> Stage2["Build & JUnit 5 Tests\n(Java 21)"]
        Stage2 --> Stage3["Package Artifact\n(Fat JAR)"]
        Stage3 --> Stage4["Security & Vulnerability Scan\n(Trivy)"]
    end

    subgraph CD ["3. Containerization & Registry"]
        Stage4 --> DockerBuild["Multi-Stage Docker Build\n(Alpine JRE 21)"]
        DockerBuild --> DockerHub["Container Registry\n(Docker Hub: harshj00/devops-lab:1.0)"]
    end

    subgraph GITOPS ["4. Continuous Delivery & GitOps"]
        DockerHub -.->|New Image Tag| Argo["GitOps Engine\n(ArgoCD)"]
        GitRepo -.->|Manifest Sync| Argo
        Argo --> K8sCluster["Kubernetes Cluster"]
    end

    subgraph K8S ["5. Production Orchestration (Kubernetes)"]
        K8sCluster --> Ingress["NodePort Service (Port 30080)"]
        Ingress --> Strat{"Deployment Strategy\n(Blue-Green / RollingUpdate)"}
        Strat --> Pod1["Pod Replica 1\n(App Container)"]
        Strat --> Pod2["Pod Replica 2\n(App Container)"]
        Strat --> Pod3["Pod Replica 3\n(App Container)"]
        
        ConfigMap["ConfigMap\n(Environment, Port, DB URL)"] --> Pod1
        Secret["Secret\n(DB & JWT Credentials)"] --> Pod1
        PVC["Persistent Volume Claim\n(HostPath / StorageClass)"] --> Pod1
        
        Pod1 --> DB[("PostgreSQL Database\n(Port 5432)")]
    end

    subgraph OBS ["6. Observability & Monitoring"]
        Prometheus["Prometheus Server\n(Scrapes /metrics)"] -->|Scrape metrics every 10s| Pod1
        Grafana["Grafana Dashboards\n(CPU, RAM, JVM, Uptime, Req/sec)"] --> Prometheus
    end

    subgraph USERS ["7. End Users & Verification"]
        User["End User / Professor"] -->|HTTP Browser / REST API| Ingress
        User -->|View Dashboard| Grafana
    end

    classDef working fill:#dcfce7,stroke:#16a34a,stroke-width:2px,color:#065f46;
    classDef planned fill:#fef3c7,stroke:#d97706,stroke-width:2px,color:#92400e;
    class Dev,GitRepo,Jenkins,Stage2,Stage3,DockerBuild,DockerHub,Ingress,Pod1,Pod2,Pod3,ConfigMap,Secret,PVC,Prometheus,Grafana,User working;
    class Stage1,Stage4,Argo planned;
```

---

## 2. Infrastructure as Code & Configuration Management

```mermaid
flowchart LR
    subgraph IAC ["Infrastructure as Code (Terraform)"]
        TF["Terraform Engine\n(main.tf)"] -->|Declares| VPC["VPC & Subnet Network"]
        TF -->|Provisions| Compute["VM / Node Instances"]
        TF -->|Outputs| Conn["Connection Strings & Endpoints"]
    end

    subgraph CM ["Configuration Management (Ansible)"]
        Playbook["Ansible Playbooks\n(site.yml)"] -->|docker_install.yml| DockerInstall["Install Docker CE & Compose"]
        Playbook -->|app_deploy.yml| AppDeploy["Deploy Multi-Container Stack"]
        Playbook -->|health_check.yml| Check["Verify /health HTTP 200"]
    end

    TF -->|Generates Target Host IPs| Playbook
```

---

## 3. Blue-Green Zero-Downtime Deployment Flow

```mermaid
sequenceDiagram
    autonumber
    actor Admin as DevOps Engineer / GitOps
    participant SVC as Production Service (Port 30080)
    participant Blue as Blue Deployment (v1.0 - Active)
    participant Green as Green Deployment (v2.0 - Candidate)
    actor Users as End Users

    Users->>SVC: Incoming Traffic
    SVC->>Blue: Routes traffic to Blue (selector: version=v1.0)
    Admin->>Green: Deploy new release candidate v2.0
    Green->>Green: Health check passes (/health returns 200 OK)
    Admin->>SVC: Update Service selector to (version=v2.0)
    Note over SVC,Green: Instant traffic switchover (Zero Downtime)
    Users->>SVC: Incoming Traffic
    SVC->>Green: Routes traffic to Green
    Note over Blue: Blue retained on standby for immediate rollback if needed
```
