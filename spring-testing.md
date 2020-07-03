## Introduction

This CLIP is getting an overview of testing within a java microservice. To start this CLIP download the exercise folder as it will be the starting point of this exercise.

### Project

We will be building a very basic bank application. This bank application already supports creating a new bank user and supports the deposit and check balance operations.
Check the code to understand the different levels of testing and have reference material.

You will enhance this application with these features:

- Enable a customer to withdraw funds.
- When a customer deposits $1000 they will get a $100 bonus. This applies only once for the lifetime of the customer.

## Levels of testing

Read about the [testing triangle](https://martinfowler.com/articles/practical-test-pyramid.html)

In this exercise we will be doing:

- **Unit testing** (Does not bring up the spring context)
  - Check BankAccountServiceTest
- **Integration** testing (Brings up a portion or all of the spring context)
  - Check BankResourceIT
  - Check CustomerRepositoryIT
- **Acceptance** testing (Loads the entire spring context and treats the application as a black box)
  - Check the cucumber package

## What to test

I purposefully don't test getters and setters directly, they are tested via their regular usage in the code.

Note the BankResourceIT. This is an integration tests that validates the contract at the RestController level. It ensures that my validation is correct, and it ensures that my error messaging is what I expect.

Note the way Spring is setup between BankResourceIT and CucumberContextConfiguration. The former only loads the RestController and the ControllerAdvice, the later loads the entire Spring context. When doing integration tests only load what you need as it will make your tests faster.

In the current example, the CustomerRepositoryTest isn't actually necessary as it's testing Spring functionality. I put it there as an example of how to set up a Repository test. You should test a repository as soon as you add specific queries in your JPA Repository Interfaces, and always with JDBCTemplates.

## Process

When presented by a feature, it's a good practice to start development by writing a cucumber feature file that describes the scope of that feature. Once the feature passes you know you've mostly implemented the story. A feature file may not cover all edge cases, negative scenarios etc. So make sure you still handle those before marking a story as done.

Once a feature file is created, we write the glue code to write our first acceptance test. A feature may one or more acceptance test.

From this point on you will have a few options on how to proceed:

- Write your API layer first
- Write your business logic first
- Write your data layer first

It is important to not tie your data layer with your API layer. Think of your API as how a client interacts with your domain. It's the UX of a back end service. Your Data layer should be resilient to feature enhancements and be able to support multiple views of your data. Changing an API layer doesn't necessarily mean changing the data layer, the changing the data layer should not mean having to change your API layer.
