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

variable "rds_host" {
  description = "The host for the users database"
  type        = string
}

variable "rds_database" {
  description = "The name of the users database"
  type        = string
}

# sns

variable "user_invited_subscribers" {
  description = "The ARNs of the SQS queues to subscribe to the user_invited SNS topic"
  type        = list(string)
  default     = []
}

variable "sns_exec_role" {
  description = "The name of the SNS execution role"
  type        = string
}

variable "sns_exec_role_arn" {
  description = "The ARN of the SNS execution role"
  type        = string
}

# lambda

variable "lambda_exec_role" {
  description = "The name of the Lambda execution role"
  type        = string
}

variable "lambda_exec_role_arn" {
  description = "The ARN of the Lambda execution role"
  type        = string
}
