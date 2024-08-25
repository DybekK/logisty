resource "aws_sns_topic" "users_user_invited_event_topic" {
  name = "users_user_invited_event_topic"
}

resource "aws_sns_topic" "users_user_registered_event_topic" {
  name = "users_user_registered_event_topic"
}

locals {
  user_invited_event_subscribers =    [aws_sqs_queue.users_user_invited_event_queue.arn]
  user_registered_event_subscribers = [aws_sqs_queue.users_user_registered_event_queue.arn]
}

resource "aws_sns_topic_subscription" "users_user_invited_topic_subscription" {
  count     = length(local.user_invited_event_subscribers)
  topic_arn = aws_sns_topic.users_user_invited_event_topic.arn
  protocol  = "sqs"
  endpoint  = element(local.user_invited_event_subscribers, count.index)
}

resource "aws_sns_topic_subscription" "users_user_registered_topic_subscription" {
  count     = length(local.user_registered_event_subscribers)
  topic_arn = aws_sns_topic.users_user_registered_event_topic.arn
  protocol  = "sqs"
  endpoint  = element(local.user_registered_event_subscribers, count.index)
}
