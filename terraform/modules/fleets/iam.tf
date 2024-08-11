resource "aws_iam_policy" "fleets_service_invoke_policy" {
  description = "Policy to allow public access to the fleets service Lambda function URL"
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect   = "Allow"
        Action   = "lambda:InvokeFunctionUrl"
        Resource = aws_lambda_function_url.fleets_service_url.function_arn
        Condition = {
          StringEquals = {
            "lambda:FunctionUrlAuthType" = "NONE"
          }
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "fleets_service_invoke_policy_attachment" {
  role       = var.lambda_exec_role
  policy_arn = aws_iam_policy.fleets_service_invoke_policy.arn
}

resource "aws_lambda_permission" "fleets_service_invoke_function_url" {
  statement_id           = "AllowPublicInvoke"
  action                 = "lambda:InvokeFunctionUrl"
  function_name          = aws_lambda_function.fleets_service.function_name
  principal              = "*"
  function_url_auth_type = "NONE"
}