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

module "roles" {
  source = "./modules/roles"

  region          = var.region
  caller_identity = data.aws_caller_identity.current.account_id
}

data "aws_caller_identity" "current" {}

module "vpc" {
  source      = "./modules/vpc"
  region      = var.region
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

  region          = var.region
  caller_identity = data.aws_caller_identity.current.account_id

  vpc_id          = module.vpc.logisty_vpc_id
  vpc_endpoint_id = module.vpc.logisty_vpc_endpoint_id

  rds_host     = module.rds.rds_host
  rds_username = var.rds_username
  rds_password = var.rds_password
  rds_port     = var.rds_port
  rds_database = "users"

  security_group_ids = [module.vpc.logisty_lambda_security_group_id]
  subnet_group_ids   = module.vpc.logisty_db_subnet_group_ids

  lambda_security_group_ids = [module.vpc.logisty_lambda_security_group_id]
  lambda_subnet_group_ids   = module.vpc.logisty_db_subnet_group_ids

  lambda_exec_role     = module.roles.lambda_exec_role
  lambda_exec_role_arn = module.roles.lambda_exec_role_arn
}

module "fleets" {
  source = "./modules/fleets"

  region          = var.region
  caller_identity = data.aws_caller_identity.current.account_id

  vpc_id = module.vpc.logisty_vpc_id

  rds_host     = module.rds.rds_host
  rds_username = var.rds_username
  rds_password = var.rds_password
  rds_port     = var.rds_port
  rds_database = "fleets"

  users_service_url      = module.users.users_service_url
  user_invited_topic_arn = module.users.user_invited_topic_arn

  security_group_ids = [module.vpc.logisty_lambda_security_group_id]
  subnet_group_ids   = module.vpc.logisty_db_subnet_group_ids

  lambda_security_group_ids = [module.vpc.logisty_lambda_security_group_id]
  lambda_subnet_group_ids   = module.vpc.logisty_db_subnet_group_ids

  lambda_exec_role     = module.roles.lambda_exec_role
  lambda_exec_role_arn = module.roles.lambda_exec_role_arn
}
