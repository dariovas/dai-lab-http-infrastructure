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
http://localhost:7001/api/bars

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

To be able to run it, you need to build the docker image based on the Dockerfile located in API-Server folder.
```
docker build -t api-server .
```

Then, runs the docker container.
```
docker run --name api-server -d -p 7001:7001 api-server
```

