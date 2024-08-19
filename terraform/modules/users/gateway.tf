# public API Gateway

resource "aws_api_gateway_rest_api" "users_service_api" {
  name        = "users_service_api"
  description = "Public API for users service"
}

resource "aws_api_gateway_resource" "users_service_proxy_resource" {
  rest_api_id = aws_api_gateway_rest_api.users_service_api.id
  parent_id   = aws_api_gateway_rest_api.users_service_api.root_resource_id
  path_part   = "{proxy+}"
}

resource "aws_api_gateway_method" "users_service_method" {
  rest_api_id   = aws_api_gateway_rest_api.users_service_api.id
  resource_id   = aws_api_gateway_resource.users_service_proxy_resource.id
  http_method   = "ANY"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "users_service_integration" {
  rest_api_id = aws_api_gateway_rest_api.users_service_api.id
  resource_id = aws_api_gateway_resource.users_service_proxy_resource.id
  http_method = aws_api_gateway_method.users_service_method.http_method

  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.users_service.invoke_arn
}

resource "aws_lambda_permission" "users_service_api_permission" {
  statement_id  = "AllowUsersServiceAPIInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.users_service.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "arn:aws:execute-api:${var.region}:${var.caller_identity}:${aws_api_gateway_rest_api.users_service_api.id}/*/*${aws_api_gateway_resource.users_service_proxy_resource.path}"
}

resource "aws_api_gateway_deployment" "users_service_api_deployment" {
  rest_api_id = aws_api_gateway_rest_api.users_service_api.id
  depends_on  = [aws_api_gateway_integration.users_service_integration]
}

resource "aws_api_gateway_stage" "users_service_api_stage" {
  rest_api_id   = aws_api_gateway_rest_api.users_service_api.id
  deployment_id = aws_api_gateway_deployment.users_service_api_deployment.id
  stage_name    = "staging"
}

# private API Gateway

resource "aws_api_gateway_rest_api" "users_service_private_api" {
  name        = "users_service_private_api"
  description = "Private API for users service"

  endpoint_configuration {
    types            = ["PRIVATE"]
    vpc_endpoint_ids = [var.vpc_endpoint_id]
  }
}

resource "aws_api_gateway_resource" "users_service_private_proxy_resource" {
  rest_api_id = aws_api_gateway_rest_api.users_service_private_api.id
  parent_id   = aws_api_gateway_rest_api.users_service_private_api.root_resource_id
  path_part   = "{proxy+}"
}

resource "aws_api_gateway_method" "users_service_private_method" {
  rest_api_id   = aws_api_gateway_rest_api.users_service_private_api.id
  resource_id   = aws_api_gateway_resource.users_service_private_proxy_resource.id
  http_method   = "ANY"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "users_service_private_integration" {
  rest_api_id = aws_api_gateway_rest_api.users_service_private_api.id
  resource_id = aws_api_gateway_resource.users_service_private_proxy_resource.id
  http_method = aws_api_gateway_method.users_service_private_method.http_method

  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.users_service.invoke_arn
}

resource "aws_lambda_permission" "users_service_private_api_permission" {
  statement_id  = "AllowUsersServicePrivateAPIInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.users_service.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "arn:aws:execute-api:${var.region}:${var.caller_identity}:${aws_api_gateway_rest_api.users_service_private_api.id}/*/*${aws_api_gateway_resource.users_service_private_proxy_resource.path}"
}

resource "aws_api_gateway_rest_api_policy" "users_service_private_api_policy" {
  rest_api_id = aws_api_gateway_rest_api.users_service_private_api.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          AWS = "*"
        }
        Action   = "execute-api:Invoke"
        Resource = "arn:aws:execute-api:${var.region}:${var.caller_identity}:${aws_api_gateway_rest_api.users_service_private_api.id}/*"
        Condition = {
          StringEquals = {
            "aws:SourceVpc" : var.vpc_id
          }
        }
      }
    ]
  })
}

resource "aws_api_gateway_deployment" "users_service_private_api_deployment" {
  rest_api_id = aws_api_gateway_rest_api.users_service_private_api.id

  triggers = {
    redeployment = sha1(jsonencode([
      aws_api_gateway_resource.users_service_private_proxy_resource.id,
      aws_api_gateway_method.users_service_private_method.id,
      aws_api_gateway_integration.users_service_private_integration.id,
    ]))
  }

  lifecycle {
    create_before_destroy = true
  }

  depends_on = [
    aws_api_gateway_integration.users_service_private_integration,
    aws_api_gateway_rest_api_policy.users_service_private_api_policy
  ]
}

resource "aws_api_gateway_stage" "users_service_private_api_stage" {
  rest_api_id   = aws_api_gateway_rest_api.users_service_private_api.id
  deployment_id = aws_api_gateway_deployment.users_service_private_api_deployment.id
  stage_name    = "staging"
}
