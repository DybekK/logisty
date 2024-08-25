resource "aws_api_gateway_rest_api" "fleets_service_api" {
  name        = "fleets_service_api"
  description = "Public API for fleets service"
}

resource "aws_api_gateway_resource" "fleets_service_proxy_resource" {
  rest_api_id = aws_api_gateway_rest_api.fleets_service_api.id
  parent_id   = aws_api_gateway_rest_api.fleets_service_api.root_resource_id
  path_part   = "{proxy+}"
}

resource "aws_api_gateway_method" "fleets_service_method" {
  rest_api_id   = aws_api_gateway_rest_api.fleets_service_api.id
  resource_id   = aws_api_gateway_resource.fleets_service_proxy_resource.id
  http_method   = "ANY"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "fleets_service_integration" {
  rest_api_id = aws_api_gateway_rest_api.fleets_service_api.id
  resource_id = aws_api_gateway_resource.fleets_service_proxy_resource.id
  http_method = aws_api_gateway_method.fleets_service_method.http_method

  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.fleets_service.invoke_arn
}

resource "aws_lambda_permission" "fleets_service_api_permission" {
  statement_id  = "AllowFleetsServiceAPIInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.fleets_service.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "arn:aws:execute-api:${var.region}:${var.caller_identity}:${aws_api_gateway_rest_api.fleets_service_api.id}/*/*${aws_api_gateway_resource.fleets_service_proxy_resource.path}"
}

resource "aws_api_gateway_deployment" "fleets_service_api_deployment" {
  rest_api_id = aws_api_gateway_rest_api.fleets_service_api.id
  depends_on  = [aws_api_gateway_integration.fleets_service_integration]
}

resource "aws_api_gateway_stage" "fleets_service_api_stage" {
  rest_api_id   = aws_api_gateway_rest_api.fleets_service_api.id
  deployment_id = aws_api_gateway_deployment.fleets_service_api_deployment.id
  stage_name    = "staging"
}
