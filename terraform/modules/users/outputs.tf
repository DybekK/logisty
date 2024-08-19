output "users_service_url" {
  value = aws_api_gateway_stage.users_service_private_api_stage.invoke_url
}

output "user_invited_topic_arn" {
  value = aws_sns_topic.user_invited_topic.arn
}