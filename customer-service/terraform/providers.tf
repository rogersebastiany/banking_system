terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
  access_key = "test"  # Required for LocalStack
  secret_key = "test"  # Required for LocalStack
  skip_credentials_validation = true
  skip_metadata_api_check = true
  skip_requesting_account_id = true

  endpoints {
    dynamodb = var.localstack_endpoint
    sqs      = var.localstack_endpoint
    sns      = var.localstack_endpoint
  }
}

variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "sa-east-1"
}

variable "localstack_endpoint" {
  description = "LocalStack endpoint"
  type        = string
  default     = "http://localhost:4566"
}
