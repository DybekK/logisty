resource "aws_sqs_queue" "users_user_invited_event_queue" {
  name = "users_user_invited_event_queue"
}

resource "aws_sqs_queue_policy" "users_user_invited_event_queue_policy" {
  queue_url = aws_sqs_queue.users_user_invited_event_queue.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect    = "Allow"
        Principal = "*"
        Action    = "sqs:SendMessage"
        Resource  = aws_sqs_queue.users_user_invited_event_queue.arn
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = aws_sns_topic.user_invited_event_topic.arn
          }
        }
      }
    ]
  })
}
