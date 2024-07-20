# rds

variable "rds_username" {
  description = "The username for the users database"
  type        = string
}

variable "rds_password" {
  description = "The password for the users database"
  type        = string
  sensitive   = true
}

variable "rds_port" {
  description = "The port for the users database"
  type        = string
}

# vpc

variable "ingress_ips" {
  description = "The IP addresses to allow ingress from"
  type        = string
}
