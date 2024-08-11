# credentials

variable "aws_access_key_id" {
  description = "The AWS access key ID"
  type        = string
  sensitive   = true
}

variable "aws_secret_access_key" {
  description = "The AWS secret access key"
  type        = string
  sensitive   = true
}

# rds

variable "rds_username" {
  description = "The username for the global database"
  type        = string
}

variable "rds_password" {
  description = "The password for the global database"
  type        = string
  sensitive   = true
}

variable "rds_port" {
  description = "The port for the global database"
  type        = string
}

# vpc

variable "ingress_ips" {
  description = "The IP addresses to allow ingress from"
  type        = string
}
