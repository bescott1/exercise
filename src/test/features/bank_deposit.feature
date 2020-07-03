Feature: Deposit into bank account

    Scenario: Create a new customer and deposit money
        Given a customer with first name "Ben" and last name "Scott" is created
        When the customer deposits 3.50 into his account
        Then the customer balance is 3.50

    Scenario: Create a customer and deposit multiple times
        Given a customer with first name "Clips" and last name "Awesome" is created
        When the customer deposits 3.53 into his account
        And the customer deposits 10.00 into his account
        And the customer deposits 1000.47 into his account
        Then the customer balance is 1014.0
        When the customer deposits 200.70 into his account
        And the customer deposits 5.0 into his account
        Then the customer balance is 1219.7
