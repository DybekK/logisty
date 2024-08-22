resource "aws_vpc" "logisty_vpc" {
  cidr_block = "10.0.0.0/16"

  enable_dns_support   = true
  enable_dns_hostnames = true
}

resource "aws_internet_gateway" "logisty_ig" {
  vpc_id = aws_vpc.logisty_vpc.id
}

resource "aws_route" "logisty_internet_route" {
  destination_cidr_block = "0.0.0.0/0"

  route_table_id = aws_vpc.logisty_vpc.main_route_table_id
  gateway_id     = aws_internet_gateway.logisty_ig.id
}

data "aws_availability_zones" "logisty_available_zones" {
  state = "available"
}

resource "aws_subnet" "logisty_subnets" {
  count      = length(data.aws_availability_zones.logisty_available_zones.names)
  cidr_block = "10.0.${count.index}.0/24"

  vpc_id                  = aws_vpc.logisty_vpc.id
  availability_zone       = element(data.aws_availability_zones.logisty_available_zones.names, count.index)
}

resource "aws_db_subnet_group" "logisty_db_subnet_group" {
  name        = "logisty_db_subnet_group"
  description = "Subnet group for Logisty databases"

  subnet_ids = aws_subnet.logisty_subnets.*.id
}

resource "aws_security_group" "logisty_db_security_group" {
  name        = "logisty_db_security_group"
  description = "Security group for Logisty database"

  vpc_id = aws_vpc.logisty_vpc.id

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    # todo: change to defined variable
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "logisty_lambda_security_group" {
  name        = "logisty_lambda_security_group"
  description = "Security group for Lambda function to access RDS"
  vpc_id      = aws_vpc.logisty_vpc.id

  ingress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group_rule" "logisty_lambda_security_group_rule" {
  type                     = "ingress"
  from_port                = 5432
  to_port                  = 5432
  protocol                 = "tcp"
  security_group_id        = aws_security_group.logisty_db_security_group.id
  source_security_group_id = aws_security_group.logisty_lambda_security_group.id
}

resource "aws_vpc_endpoint" "logisty_api_gateway_endpoint" {
  vpc_id              = aws_vpc.logisty_vpc.id
  service_name        = "com.amazonaws.${var.region}.execute-api"
  vpc_endpoint_type   = "Interface"
  private_dns_enabled = true

  security_group_ids = [aws_security_group.logisty_lambda_security_group.id]
  subnet_ids         = aws_subnet.logisty_subnets.*.id
}

resource "aws_vpc_endpoint" "logisty_sns_endpoint" {
  vpc_id              = aws_vpc.logisty_vpc.id
  service_name        = "com.amazonaws.${var.region}.sns"
  vpc_endpoint_type   = "Interface"
  private_dns_enabled = true

  security_group_ids = [aws_security_group.logisty_lambda_security_group.id]
  subnet_ids         = aws_subnet.logisty_subnets.*.id
}
