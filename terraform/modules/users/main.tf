locals {
  variables = {
    AWS_LAMBDA_HTTP_IGNORE_STAGE_IN_PATH = "true"
    USER_REGISTERED_EVENT_TOPIC_ARN      = aws_sns_topic.users_user_registered_event_topic.arn
    APP_AWS_REGION                       = var.region
    DATABASE_URL                         = "postgres://${var.rds_username}:${var.rds_password}@${var.rds_host}:${var.rds_port}/${var.rds_database}"
  }
}

resource "aws_lambda_function" "users_service" {
  function_name = "users_service"
  description   = "AWS Lambda function for the users service"
  runtime       = "provided.al2023"
  architectures = ["arm64"]

  handler  = "bootstrap"
  filename = "../backend/target/lambda/users/bootstrap.zip"

  role = var.lambda_exec_role_arn

  environment {
    variables = local.variables
  }

  vpc_config {
    security_group_ids = var.security_group_ids
    subnet_ids         = var.subnet_group_ids
  }
}

resource "aws_lambda_function" "users_event_consumer" {
  function_name = "users_event_consumer"
  description   = "AWS Lambda function for the users event consumer"
  runtime       = "provided.al2023"
  architectures = ["arm64"]

  handler  = "bootstrap"
  filename = "../backend/target/lambda/users_event_consumer/bootstrap.zip"

  role = var.lambda_exec_role_arn

  environment {
    variables = local.variables
  }

  vpc_config {
    security_group_ids = var.security_group_ids
    subnet_ids         = var.subnet_group_ids
  }
}


resource "aws_lambda_event_source_mapping" "user_invited_event_consumer_mapping" {
  event_source_arn = aws_sqs_queue.users_user_invited_event_queue.arn
  function_name    = aws_lambda_function.users_event_consumer.arn
  batch_size       = 10
  enabled          = true
}

resource "aws_lambda_function" "users_projection_consumer" {
  function_name = "users_projection_consumer"
  description   = "AWS Lambda function for the users projection consumer"
  runtime       = "provided.al2023"
  architectures = ["arm64"]

  handler  = "bootstrap"
  filename = "../backend/target/lambda/users_projection_consumer/bootstrap.zip"

  role = var.lambda_exec_role_arn

  environment {
    variables = local.variables
  }

  vpc_config {
    security_group_ids = var.security_group_ids
    subnet_ids         = var.subnet_group_ids
  }
}

locals {
  users_projection_consumer_mappings = {
    user_registered_event = aws_sqs_queue.users_user_registered_event_queue.arn,
  }
}

resource "aws_lambda_event_source_mapping" "users_projection_consumer_mappings" {
  for_each         = local.users_projection_consumer_mappings
  event_source_arn = each.value
  function_name    = aws_lambda_function.users_projection_consumer.arn
  batch_size       = 10
  enabled          = true
}