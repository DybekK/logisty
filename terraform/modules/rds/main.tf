resource "aws_db_parameter_group" "logisty_db_parameter_group" {
  name   = "logisty-db-parameter-group"
  family = "postgres16"

  parameter {
    name         = "rds.force_ssl"
    value        = "0"
    apply_method = "pending-reboot"
  }
}

resource "aws_db_instance" "logisty_db" {
  allocated_storage = 20

  vpc_security_group_ids = [var.vpc_security_group_id]
  db_subnet_group_name   = var.subnet_group_name

  identifier     = "logistydb"
  engine         = "postgres"
  engine_version = "16.3"
  instance_class = "db.t4g.micro"

  parameter_group_name = aws_db_parameter_group.logisty_db_parameter_group.name

  username = var.rds_username
  port     = var.rds_port
  password = var.rds_password

  publicly_accessible          = true
  skip_final_snapshot          = true
  allow_major_version_upgrade  = true
  auto_minor_version_upgrade   = true
  performance_insights_enabled = true
  apply_immediately            = true
}
