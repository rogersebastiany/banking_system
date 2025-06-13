# 1. Define the DynamoDB table for our customers.
# The schema here must match our Customer domain model.
resource "aws_dynamodb_table" "customers_table" {
  name         = "customers"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "customerId"

  attribute {
    name = "customerId"
    type = "S" # S for String
  }
}

# 2. Define an SQS queue for future customer registration events.
resource "aws_sqs_queue" "customer_registered_queue" {
  name = "customer-registered-events"
}

# Output the table name and queue URL for easy access
output "customers_table_name" {
  value = aws_dynamodb_table.customers_table.name
}

output "customer_registered_queue_url" {
  value = aws_sqs_queue.customer_registered_queue.id
}
