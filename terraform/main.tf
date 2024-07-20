terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
    }
  }
}

provider "aws" {
  region = "eu-west-3"
}

module "lambda" {
  source = "./modules/lambda"
}

module "vpc" {
  source      = "./modules/vpc"
  ingress_ips = var.ingress_ips
}

module "rds" {
  source = "./modules/rds"

  vpc_security_group_id = module.vpc.logisty_db_security_group_id
  subnet_group_name     = module.vpc.logisty_db_subnet_group_name

  rds_username = var.rds_username
  rds_password = var.rds_password
  rds_port     = var.rds_port
}

module "users" {
  source = "./modules/users"

  rds_host     = module.rds.rds_host
  rds_username = var.rds_username
  rds_password = var.rds_password
  rds_port     = var.rds_port
  rds_database = "users"

  security_group_ids = [module.vpc.logisty_lambda_security_group_id]
  subnet_group_ids   = module.vpc.logisty_db_subnet_group_ids

  lambda_exec_role     = module.lambda.lambda_exec_role
  lambda_exec_role_arn = module.lambda.lambda_exec_role_arn
}
