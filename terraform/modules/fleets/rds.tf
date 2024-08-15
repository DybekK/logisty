terraform {
  required_providers {
    postgresql = {
      source  = "cyrilgdn/postgresql"
      version = "1.22.0"
    }
  }
}

provider "postgresql" {
  host     = var.rds_host
  port     = var.rds_port
  username = var.rds_username
  password = var.rds_password
}

resource "postgresql_database" "postgresql" {
  provider = postgresql
  name     = "fleets"
}
