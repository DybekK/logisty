resource "aws_iam_role" "sns_exec_role" {
  name = "sns_exec_role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "sns.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "sns_exec_policy" {
  role       = aws_iam_role.sns_exec_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonSNSRole"
}
