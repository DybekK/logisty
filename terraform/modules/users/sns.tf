resource "aws_sns_topic" "user_invited_topic" {
  name = "user_invited_topic"
}

# resource "aws_sns_topic_subscription" "user_invited_topic_subscription" {
#   count     = length(var.user_invited_subscribers)
#   topic_arn = aws_sns_topic.user_invited_topic.arn
#   protocol  = "sqs"
#   endpoint  = element(var.user_invited_subscribers, count.index)
# }
