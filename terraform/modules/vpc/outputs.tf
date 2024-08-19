output "logisty_vpc_id" {
  value = aws_vpc.logisty_vpc.id
}

output "logisty_db_security_group_id" {
  value = aws_security_group.logisty_db_security_group.id
}

output "logisty_lambda_security_group_id" {
  value = aws_security_group.logisty_lambda_security_group.id
}

output "logisty_db_subnet_group_ids" {
  value = aws_db_subnet_group.logisty_db_subnet_group.subnet_ids
}

output "logisty_db_subnet_group_name" {
  value = aws_db_subnet_group.logisty_db_subnet_group.name
}

output "logisty_vpc_endpoint_id" {
  value = aws_vpc_endpoint.logisty_api_gateway_endpoint.id
}