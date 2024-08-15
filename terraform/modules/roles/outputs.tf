# lambda

output "lambda_exec_role" {
  description = "The name of the Lambda execution role"
  value       = aws_iam_role.lambda_exec_role.name
}

output "lambda_exec_role_arn" {
  description = "The ARN of the Lambda execution role"
  value       = aws_iam_role.lambda_exec_role.arn
}

# sns

output "sns_exec_role" {
  description = "The name of the SNS execution role"
  value       = aws_iam_role.sns_exec_role.name
}

output "sns_exec_role_arn" {
  description = "The ARN of the SNS execution role"
  value       = aws_iam_role.sns_exec_role.arn
}