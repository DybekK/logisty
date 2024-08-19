resource "aws_lambda_function" "users_service" {
  function_name = "users_service"
  description   = "AWS Lambda function for the users service"
  runtime       = "provided.al2023"
  architectures = ["arm64"]

  handler  = "bootstrap"
  filename = "../backend/target/lambda/users/bootstrap.zip"

  role = var.lambda_exec_role_arn

  environment {
    variables = {
      AWS_LAMBDA_HTTP_IGNORE_STAGE_IN_PATH = "true"
      APP_AWS_REGION                       = var.region
      DATABASE_URL                         = "postgres://${var.rds_username}:${var.rds_password}@${var.rds_host}:${var.rds_port}/${var.rds_database}"
    }
  }

  vpc_config {
    security_group_ids = var.security_group_ids
    subnet_ids         = var.subnet_group_ids
  }
}
