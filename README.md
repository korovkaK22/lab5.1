## Task 5.1

---

1. [General Information](#general-information)
2. [Application Launch](#application-launch)
3. [Tests](#tests)

## General Information
This repository contains two tasks: Task 2 in the `/TaxiDriverRestApplication` folder, and Task 5.1 in the
`/mail-sender-service folder`. Task 2 has been rewritten to send messages to Kafka, and Docker files
have been added to automatically set it up with PostgreSQL. Ports for Kibana and the backend have been 
mapped to the local machine to allow immediate access to these services.



## Application Launch
In the `/mail-sender-service folder`, create a `.env` file and add the following fields with your email credentials:
```properties
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_USERNAME=xxxx@gmail.com
SPRING_MAIL_PASSWORD=xxxx xxxx xxxx xxxx
SPRING_MAIL_PORT=465
SPRING_MAIL_PROTOCOL=smtp
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
MAIL_DEBUG=true
SPRING_MAIL_SMTP_AUTH=true
SPRING_MAIL_SMTP_STARTTLS_ENABLE=true
```
Then, check if everything is up by making a request to retrieve all companies from the backend:

```cmd
docker-compose up --build
```

Then, check if everything is up by making a request to retrieve all companies from the backend:

```cmd
docker-compose up --build
```

<p align="center">
  <img src="https://i.imgur.com/pYo5JRt.png" alt="Request photo"/>
</p>

Since we have a Docker container for PostgreSQL and Liquibase scripts for data insertion, 
we immediately get a ready-to-use database with pre-populated data. After this,
to test the Kafka setup and our entire system, send a POST request to create a new company using Postman:

```cmd
http://localhost:8082/api/company
```

```json
{   
  "name": "Our new Company",
  "country": "Ukraine",
  "workingCarsAmount":5
}
```
If the email credentials are correct, the email server will send an email to the addresses
specified in the `TaxiDriverRestApplication/.../application.properties` file and log 
a message in the Elasticsearch database indicating whether the sending was successful or not.
If sending fails, a scheduler will attempt to resend the email after 5 minutes.


<p align="center">
  <img src="https://i.imgur.com/0oXA84T.png" alt="mail sended"/>
</p>

<p align="center">
  <img src="https://i.imgur.com/501URRz.png" alt="mail sended"/>
</p>

Notifications will also be sent on the creation, editing, and deletion of companies.

To view the Elasticsearch logs through Kibana, navigate to the following URL:

```cmd
http://localhost:5601/
```

In the ***Discover*** tab, you can view the email sending logs using the appropriate index.

<p align="center">
  <img src="https://i.imgur.com/5gM5ALQ.png" alt="mail sended"/>
</p>


## Tests
To run the tests, navigate to the `/mail-sender-service` folder and start Docker Compose for tests:

```cmd
docker-compose -f docker-compose.test.yml up --build
```

The tests are primitive and serve to demonstrate the setup of test containers. Since a significant 
portion of the functionality needs to be mocked for proper testing, the tests are basic. However, 
during testing, Maven is downloaded afresh each time, which takes about 5 minutes. I haven't figured 
out how to fix this yet, but I will address it in the future.