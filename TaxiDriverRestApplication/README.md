## TaxiDriverRestApplication

---

## Table of Contents
1. [General Information](#general-information)
2. [Application Launch](#application-launch)
3. [Tests](#tests)

## General Information
Our project features two primary entities: companies and taxi drivers.
These entities are represented in the system in a JSON format. 


```json
{
  "name" : "Verlie",
  "surname" : "Albine",
  "companyId" : null,
  "age" : 36,
  "drivingExperience" : 2,
  "salary" : 9154,
  "cars" : "Toyota Prius, Ford Mustang"
}
```

More drivers' examples ypu can find in `src/main/resources/drivers.json` file.
In this structure, there are specific constraints to consider; notably, a taxi driver may not necessarily 
be associated with a company. Among the entities, the primary focus is on taxi drivers, while 
companies serve as a secondary entity. The relationship between these two entities is defined as a
one-to-many interaction, where one company can be associated with multiple taxi drivers. 

## Application Launch

In the project, there is no need for initial manual setup to start the application.
The main entry point is provided by the `TaxiDriverRestApplication.class`. 
Additionally, a Liquibase script has been integrated which automatically executes at
the launch of the application. However, it is essential to modify the `application.properties`
file before these processes can be initiated and database will be injected.
Also, there are different files were created for LiquidBase, sorted by versions.

## Tests
To verify how drivers are integrated with a JSON file in the project (**/upload** endpoint), 
a file named `resourses/drivers(91,9).json` has been created. This file contains 100 entries, 
of which the 91st entry is correct, while the 9th entry is incorrect for various reasons. 
This setup allows for the testing of the application's validation processes to ensure 
they are functioning correctly. It confirms that valid drivers are added to the system properly, 
while entries that do not meet the criteria are appropriately rejected, highlighting the robustness of
the application's data validation mechanisms. All test were separated by endpoints and were placed into nested
classes for better visual understanding.