resource "aws_sns_topic" "user_invited_topic" {
  name = "user_invited_topic"
}

resource "aws_iam_policy" "user_invited_topic_policy" {
  name        = "user_invited_topic_policy"
  description = "Policy to allow publishing to the user_invited_topic"
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect   = "Allow"
        Action   = "sns:Publish"
        Resource = aws_sns_topic.user_invited_topic.arn
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "user_invited_topic_policy_attachment" {
  role       = var.sns_exec_role
  policy_arn = aws_iam_policy.user_invited_topic_policy.arn
}

resource "aws_sns_topic_subscription" "user_invited_topic_subscription" {
  count     = length(var.user_invited_subscribers)
  topic_arn = aws_sns_topic.user_invited_topic.arn
  protocol  = "sqs"
  endpoint  = element(var.user_invited_subscribers, count.index)
}
