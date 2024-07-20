# vpc

variable "vpc_security_group_id" {
  description = "The ID of the security group"
  type        = string
}

variable "subnet_group_name" {
  description = "The name of the subnet group"
  type        = string
}

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
