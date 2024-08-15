output "function_url" {
  value = aws_lambda_function_url.users_service_url.function_url
}

output "user_invited_topic_arn" {
  value = aws_sns_topic.user_invited_topic.arn
}