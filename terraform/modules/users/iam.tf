resource "aws_iam_policy" "users_service_invoke_policy" {
  description = "Policy to allow public access to the users service Lambda function URL"
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect   = "Allow"
        Action   = "lambda:InvokeFunctionUrl"
        Resource = aws_lambda_function_url.users_service_url.function_arn
        Condition = {
          StringEquals = {
            "lambda:FunctionUrlAuthType" = "NONE"
          }
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "users_service_invoke_policy_attachment" {
  role       = var.lambda_exec_role
  policy_arn = aws_iam_policy.users_service_invoke_policy.arn
}

resource "aws_lambda_permission" "users_service_invoke_function_url" {
  statement_id           = "AllowPublicInvoke"
  action                 = "lambda:InvokeFunctionUrl"
  function_name          = aws_lambda_function.users_service.function_name
  principal              = "*"
  function_url_auth_type = "NONE"
}