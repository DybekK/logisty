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
      APP_AWS_REGION                       = var.region
      AWS_LAMBDA_HTTP_IGNORE_STAGE_IN_PATH = "true"
      USERS_URL                            = var.users_service_url
      USER_INVITED_EVENT_TOPIC_ARN         = var.user_invited_event_topic_arn
      DATABASE_URL                         = "postgres://${var.rds_username}:${var.rds_password}@${var.rds_host}:${var.rds_port}/${var.rds_database}"
    }
  }

  vpc_config {
    security_group_ids = var.security_group_ids
    subnet_ids         = var.subnet_group_ids
  }
}
