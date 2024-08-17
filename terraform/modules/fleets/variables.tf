# rds

variable "security_group_ids" {
  description = "The IDs of the security groups"
  type        = list(string)
}

variable "subnet_group_ids" {
  description = "The ids of the subnet groups"
  type        = list(string)
}

variable "rds_username" {
  description = "The username for the fleets database"
  type        = string
}

variable "rds_password" {
  description = "The password for the fleets database"
  type        = string
  sensitive   = true
}

variable "rds_port" {
  description = "The port for the fleets database"
  type        = string
}

variable "rds_host" {
  description = "The host for the fleets database"
  type        = string
}

variable "rds_database" {
  description = "The name of the fleets database"
  type        = string
}

# sns

variable "sns_exec_role" {
  description = "The name of the SNS execution role"
  type        = string
}

variable "sns_exec_role_arn" {
  description = "The ARN of the SNS execution role"
  type        = string
}

variable "user_invited_topic_arn" {
  description = "The ARN of the user_invited SNS topic"
  type        = string
}

# lambda

variable "users_service_url" {
  description = "The URL of the users service"
  type        = string
}

variable "lambda_exec_role" {
  description = "The name of the Lambda execution role"
  type        = string
}

variable "lambda_exec_role_arn" {
  description = "The ARN of the Lambda execution role"
  type        = string
}
