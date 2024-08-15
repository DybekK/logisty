resource "aws_lambda_function" "fleets_service" {
  function_name = "fleets_service"
  description   = "AWS Lambda function for the fleets service"
  runtime       = "provided.al2023"
  architectures = ["arm64"]

  handler  = "bootstrap"
  filename = "../backend/target/lambda/fleets/bootstrap.zip"

  role = var.lambda_exec_role_arn

  environment {
    variables = {
      AWS_REGION   = "eu-west-3"
      USERS_URL    = var.users_service_url
      TOPIC_ARN_USER_INVITED = var.user_invited_topic_arn
      DATABASE_URL = "postgres://${var.rds_username}:${var.rds_password}@${var.rds_host}:${var.rds_port}/${var.rds_database}"
    }
  }

  vpc_config {
    security_group_ids = var.security_group_ids
    subnet_ids         = var.subnet_group_ids
  }
}

resource "aws_lambda_function_url" "fleets_service_url" {
  function_name      = aws_lambda_function.fleets_service.function_name
  authorization_type = "NONE"

  cors {
    allow_origins = ["*"]
    allow_headers = ["*"]
    allow_methods = ["GET", "POST", "DELETE", "PUT"]
    max_age       = 300
  }
}