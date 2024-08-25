variable "region" {
  description = "The region to deploy the infrastructure to"
  type        = string
}

variable "ingress_ips" {
  description = "The IP addresses to allow ingress from"
  type        = string
}
