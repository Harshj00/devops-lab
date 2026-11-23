terraform {
  required_version = ">= 1.5.0"
  required_providers {
    local = {
      source  = "hashicorp/local"
      version = "~> 2.4"
    }
  }
}

variable "environment" {
  type        = string
  description = "Target deployment environment (dev, staging, prod)"
  default     = "production"
}

variable "app_port" {
  type        = number
  description = "Application HTTP listen port"
  default     = 8080
}

variable "db_port" {
  type        = number
  description = "PostgreSQL Database listen port"
  default     = 5432
}

variable "instance_count" {
  type        = number
  description = "Number of application cluster replicas"
  default     = 3
}

resource "local_file" "k8s_env_config" {
  filename = "${path.module}/generated_env.conf"
  content  = <<-EOT
    # Terraform Provisioned Infrastructure Spec
    ENVIRONMENT=${var.environment}
    APP_PORT=${var.app_port}
    DB_PORT=${var.db_port}
    REPLICAS=${var.instance_count}
    PROVISIONED_TIMESTAMP=${timestamp()}
  EOT
}

output "application_endpoint" {
  description = "Primary URL endpoint for the deployed application"
  value       = "http://localhost:${var.app_port}"
}

output "database_connection_string" {
  description = "Database connection host string"
  value       = "postgresql://postgres:password@localhost:${var.db_port}/studentdb"
}

output "cluster_replica_count" {
  description = "Target replica scale configured by Terraform"
  value       = var.instance_count
}
