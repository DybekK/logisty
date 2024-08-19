variable "region" {
  description = "The region to deploy the infrastructure to"
  type        = string
  default     = "eu-west-3"
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
