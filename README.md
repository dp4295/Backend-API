# Instructions to Run the Project

## Prerequisites 
 - Docker installed on your computer. [Install](https://www.docker.com/products/docker-desktop/) 


## Build the package 
``mvn clean package``

## Build the Docker Image
### Build the Docker image from the Dockerfile
``docker build -t fetch-receipt-process-app .``  [note: Run this command in your root folder] 

### Run the Docker container
``docker run -p 8080:8080 fetch-receipt-process-app`` [note: Run this command in your root folder]
  - Above command will start Spring boot application. 
  - Navigate to http://localhost:8080/swagger-ui/index.html in the browser 


## Addition command 

###  Identify the container ID of the running instance
``docker ps`` 

### Stop the Container
``docker stop <container-id>``




