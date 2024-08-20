resource "aws_sns_topic" "user_invited_event_topic" {
  name = "user_invited_event_topic"
}

locals {
  user_invited_event_subscribers = concat([aws_sqs_queue.users_user_invited_event_queue.arn], var.user_invited_event_subscribers)
}

resource "aws_sns_topic_subscription" "user_invited_topic_subscription" {
  count     = length(local.user_invited_event_subscribers)
  topic_arn = aws_sns_topic.user_invited_event_topic.arn
  protocol  = "sqs"
  endpoint  = element(local.user_invited_event_subscribers, count.index)
}
