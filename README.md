# Akka HTTP Proto

A prototype (proto) project to manipulate Akka HTTP's concepts

It consists of a CRUD Rest service for managing persons. 
A person has the flowing attributes : firstName, lastName, age.

The proto covers :

- use of the Akka HTTP's routing DSL
- creating directives
- parsing models to and from JSON
- unit testing routes using the Akka HTTP's testkit and Scalatest

# Requirements

- SBT
- Docker 
- JDK 8 

# Running

From the project directory just type on the terminal :

` sbt run `

Or ` sbt test ` to run just unit tests

# Packaging with Docker

The project is dockerised. To package it as a docker image just type : 

`sbt docker:publishLocal`

This command will generate and push an image to the local docker repository ([see here](https://www.scala-sbt.org/sbt-native-packager/formats/docker.html)). 

To verify the generated image (akkahttp-proto) : 

`docker images`

let's run the image :

`docker run --name proto -d -p 9000:9000 akkahttp-proto:0.3`

Now the server is running and wainting requests on the port *9000*. Let's create a person :

`curl -H "Content-Type: application/json" -X POST --data '{"firstName":"Beau", "lastName":"Goss", "age": 18}' localhost:9000/persons`

we can get all added persons by :

`curl -X GET localhost:9000/persons`

We can also use a tool like postman.


Thanks to Miguel Lopez for his [tutorials](https://www.codemunity.io/tutorials/)
