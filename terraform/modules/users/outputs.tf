output "users_service_url" {
  value = aws_api_gateway_stage.users_service_private_api_stage.invoke_url
}

output "user_invited_event_topic_arn" {
  value = aws_sns_topic.users_user_invited_event_topic.arn
}