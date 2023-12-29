# DAI lab: HTTP Infrastructure
***

## Static Web Server
A static web server has been deployed.

It provides a static website accessible through this URL :
http://localhost

The configuration files can be found in the folder [staticWebServer](./staticWebServer).

They include comments explaining the configuration lines in detail.

### Dockerfile
The Dockerfile defines :
- Which image to use
- Website content and nginx configuration to copy
- Which port to expose for the external connection

### nginx.conf
The nginx.conf defines :
- Maximum connection to handle by worker
- Which port to listen and on which hostname
- Location of the website content to display
- Default page 
- Custom error pages

## Docker Compose 
The docker compose file will be used to generate our infrastructure.

The compose.yaml defines :
- The services to build
- On which port

The file has been also documented with more information.

The services can be started by using the following command :
```
docker compose up -d
```

Moreover, to additionally rebuild the images : 
```
docker compose up --build -d
```

Finaly, the services can be stopped by using the following command :
```
docker compose down
```

## HTTP API Server
An HTTP API Server to handle and manage different bars has been implemented.

It can be acceded on this URL :
http://localhost/api/bars

All configuration files can be found in the folder [api-server](./api-server).

In this API, the following CRUD operations are possible :
- Create
  - POST /api/bars --> creates a new bar.
  - POST /api/bars/{id}/cocktails/add --> adds a new cocktail to a specific bar.
- Read
  - GET /api/bars --> displays all bars.
  - GET /api/bars/{id} --> displays a specific bar.
  - GET /api/bars/{id}/cocktails --> displays a specific bar cocktails list.
- Update
  - PUT /api/bars/{id} --> updates the properties of a specific bar. The following properties are modifiable : name, city, capacity and cocktails list (it is not mandatory to modify all properties at once).
- Delete
  - DELETE /api/bars/{id} --> deletes a specific bar.
  - DELETE /api/bars/{id}/cocktails/del --> removes a cocktail from a specific bar.

Moreover, a [memo](./api-server/MEMO.md) containing an explanation of the different operations above has been written.

Then, to be able to run it, you need to build the docker image based on the Dockerfile located in API-Server folder.
```
docker build -t api-server .
```

Then, runs the docker container.
```
docker run --name api-server -d -p 80:80 api-server
```

## Reverse proxy with Traefik
A reverse proxy with Traefik has been implemented in the infrastructure.

The configuration can be found in the [compose.yaml file](./compose.yaml).

It includes comments explaining the configuration lines for traefik in detail.

We also configured access log to see which service respond to the requests.

### Why a reverse proxy is useful to improve the security of the infrastructure 
A reverse proxy can hide the topology and characteristics of the back-end servers by removing the need to expose them direct to Internet.

So, by intercepting requests for the backend servers, a reverse proxy protects their identities and acts as a security layer in more against the malicious people.

Another important point, it is also a good place to monitor and log what is going to the backend servers.

Moreover, it can also ensure that multiple servers can be accessed through the same URL. 

### Dashboard Traefix
Traefik provides a dashboard on which we can found the different routers and services configured.

It allows us to monitor and configure the services, the routes, the routage rules, and so on.

It is accessible through this URL :
http://localhost:8080/dashboard

## Scalability and load balancing
### Configuration
To start multiple instances of the containers with docker compose, the deploy option has been defined under services in which the scalability is useful.

The replicas parameter of the deploy option describes number of instances to be created when running "docker compose up".

The configuration can be found in the [compose.yaml file](./compose.yaml).

To see if it is working, you can check the access.log file mounted locally in the reverse_proxy folder, then you can see that the reverse proxy redirects the requests on different instances :
```
[29/Dec/2023:13:05:48 +0100] "GET / HTTP/1.1" 304 0 "-" "-" 1 "staticWeb-router@docker" "http://172.27.0.7:80" 2ms
[29/Dec/2023:13:05:52 +0100] "GET /api/bars HTTP/1.1" 200 675 "-" "-" 2 "api-router@docker" "http://172.27.0.6:80" 146ms
[29/Dec/2023:13:05:56 +0100] "GET /api/bars/1 HTTP/1.1" 200 161 "-" "-" 3 "api-router@docker" "http://172.27.0.2:80" 121ms
[29/Dec/2023:13:06:26 +0100] "GET / HTTP/1.1" 304 0 "-" "-" 6 "staticWeb-router@docker" "http://172.27.0.3:80" 4ms
```

### How to dynamically update the number of instances
When the infrastructure is running, the number of instances can be adapted with the docker compose command by specifying the option "--scale" followed by the service for which we need to increase or decrease the number of instances.
```
docker compose up --scale static_web_server=5 --scale api_server=2
```


