# DAI lab: HTTP Infrastructure
***

## Step 1: Static Web site
The first step was to configure NGINX in a container to be able to run a static web site.

The configuration files can be found in the folder [static_web_server](./static_web_server).

As content for our static site, the template site Grayscale from [Start Bootstrap](https://startbootstrap.com/themes) has been chosen.

In the Dockerfile, we specified the NGINX image, the nginx configuration and the site content to copy in the container and the port to expose.
```
FROM nginx
COPY config /etc/nginx
COPY content /usr/share/nginx/html
EXPOSE 80
```

Below is the nginx.conf file with comments explaining each line :
```
http {
    server{
        # Tells NGINX hostname/IP and the TCP port where it should listen for HTTP connection.
        listen 80;
        server_name localhost;

        # Helps provide a hint as to how the content should be processed and displayed.
        include /etc/nginx/mime.types;

        # Location of website files
        location / {
            root /usr/share/nginx/html;
            index index.html;
        }

        # Configures logging.
        error_log  /var/log/nginx/error.log warn;
        access_log  /var/log/nginx/access.log;

        # Custom error pages for following codes
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   /usr/share/nginx/html;
        }
    }
}
```

The static website is accessible through this URL :
https://localhost

## Step 2: Docker compose
The docker compose file will be used to generate our infrastructure.

The [compose.yaml](./compose.yaml). defines :
- The services to build
- The labels for the Traefik configuration

The file has been also documented with more information.

The services can be started by using the following command :
```
docker compose up -d
```

Moreover, to additionally rebuild the images : 
```
docker compose up --build -d
```

Finally, the services can be stopped by using the following command :
```
docker compose down
```

## HTTP API Server
An HTTP API Server to handle and manage different bars has been implemented.

It can be acceded on this URL :
https://localhost/api/bars

All configuration files can be found in the folder [api_server](./api_server).

In this API, the following CRUD operations are possible :
- Create
  - `POST /api/bars` --> creates a new bar.
  - `POST /api/bars/{id}/cocktails/add` --> adds a new cocktail to a specific bar.
- Read
  - `GET /api/bars` --> displays all bars.
  - `GET /api/bars/{id}` --> displays a specific bar.
  - `GET /api/bars/{id}/cocktails` --> displays a specific bar cocktails list.
- Update
  - `PUT /api/bars/{id}` --> updates the properties of a specific bar.  
    The following properties are modifiable : name, city, capacity and cocktails list (it is not mandatory to modify all properties at once).
- Delete
  - `DELETE /api/bars/{id}` --> deletes a specific bar.
  - `DELETE /api/bars/{id}/cocktails/del` --> removes a cocktail from a specific bar.

Moreover, a [memo](./api_server/doc/MEMO.md) containing an explanation of the different operations above has been written.

Then, to be able to run it, you need to build the docker image based on the Dockerfile located in API-Server folder.
```
docker build -t api-server .
```

Then, runs the docker container.
```
docker run --name api-server -d -p 80:80 api-server
```

It has been also included in the docker compose file.

## Step 4: Reverse proxy with Traefik
### Configuration
To configure a reverse proxy in the infrastructure, a new service called `reverse_proxy` has been added.

In this one, we've configured the Traefix image and defined that it should listen to docker.
We've also added an access log to see which service is responding to HTTP requests.

Then, on the remaining two services `static_web_server` and `api_server`, we configured labels used by Traefik to know where and how the requests must be routed.

Below, the configuration applied to the `api_server` which is the same like the `static_web_server`, but we defined the domain "localhost" without the path-prefixed.
```
# Defines the path-prefixed domain to which the service responds.
- "traefik.http.routers.api-router.rule=Host(`localhost`) && PathPrefix(`/api`)"
# Routes all requests to the service api-service.
- "traefik.http.routers.api-router.service=api-service"
# Defines the port on which the service responds.
- "traefik.http.services.api-service.loadbalancer.server.port=80"
```

The configuration can be found in the [compose.yaml file](./compose.yaml).

### Why a reverse proxy is useful to improve the security of the infrastructure 
A reverse proxy can hide the topology and characteristics of the back-end servers by removing the need to expose them direct to Internet.

So, by intercepting requests for the backend servers, a reverse proxy protects their identities and acts as a security layer in more against the malicious people.

Another important point, it is also a good place to monitor and log what is going to the backend servers.

Moreover, it can also ensure that multiple servers can be accessed through the same URL. 

### Dashboard Traefix
Traefik provides a dashboard on which we can found the different routers and services configured.

It allows us to monitor the services, the routes, the routage rules, and so on.

It is accessible through this URL :
http://localhost:8080/dashboard

## Step 5: Scalability and load balancing
### Configuration
To start multiple instances of the containers with docker compose, the `deploy` option has been defined under services in which the scalability is useful.

The `replicas` parameter of the `deploy` option describes number of instances to be created when running "docker compose up".
```
deploy:
replicas: 3
```

The configuration can be found in the [compose.yaml file](./compose.yaml).

To see if it works, you can check the access.log file mounted locally in the reverse_proxy folder, then you can see that the reverse proxy redirects the requests on different instances :
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

## Step 6: Load balancing with round-robin and sticky sessions
### Configuration
To allow sticky sessions for the api-server, the following labels has been added to the compose.yaml file under the service api_server.
```
# Enables the sticky session for the api-service.
- "traefik.http.services.api-service.loadbalancer.sticky=true"
# Sets the cookie name to identify the session and enables the security on it.
- "traefik.http.services.api-service.loadbalancer.sticky.cookie.name=apicookie"
- "traefik.http.services.api-service.loadbalancer.sticky.cookie.secure=true"
```

### Testing
#### Static web server
If we tried to access the static web server from a browser, Traefik does round-robin :
```
[29/Dec/2023:13:15:04 +0100] "GET / HTTP/1.1" 304 0 "-" "-" 14 "staticWeb-router@docker" "http://172.28.0.4:80" 1ms
[29/Dec/2023:13:15:05 +0100] "GET / HTTP/1.1" 304 0 "-" "-" 17 "staticWeb-router@docker" "http://172.28.0.6:80" 0ms
```
So, the requests are redirected on different instances.

#### API server
If we tried to access the api-server from a browser in incognito mode and in standard mode, Traefik uses a sticky session :
```
Incognito mode :
[29/Dec/2023:13:14:26 +0100] "GET /api/bars HTTP/1.1" 200 675 "-" "-" 2 "api-router@docker" "http://172.28.0.2:80" 115ms
[29/Dec/2023:13:14:28 +0100] "GET /api/bars/1 HTTP/1.1" 200 161 "-" "-" 3 "api-router@docker" "http://172.28.0.2:80" 8ms
[29/Dec/2023:13:14:31 +0100] "GET /api/bars HTTP/1.1" 200 675 "-" "-" 4 "api-router@docker" "http://172.28.0.2:80" 4ms

Standard mode :
[29/Dec/2023:13:18:24 +0100] "GET /api HTTP/1.1" 200 8 "-" "-" 24 "api-router@docker" "http://172.28.0.7:80" 78ms
[29/Dec/2023:13:18:27 +0100] "GET /api/bars HTTP/1.1" 200 675 "-" "-" 25 "api-router@docker" "http://172.28.0.7:80" 119ms
[29/Dec/2023:13:18:29 +0100] "GET /api/bars/1 HTTP/1.1" 200 161 "-" "-" 26 "api-router@docker" "http://172.28.0.7:80" 8ms
```

So, all requests from the same browser go to the same instance.

## Step 7: Securing Traefik with HTTPS
### Certificates generation
First, two certificates were generated using the openssl tool, which allows us to generate a self-signed certificate and its associated private key.
```
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -sha256 -days 3650 -nodes -subj "/C=CH/ST=VD/L=Yverdon/O=HEIG/OU=HEIG/CN=localhost"
```

Then, these certificates must be in a folder called **certificates** located in the reverse_proxy folder.

### Traefik configuration file
To be able to securing Traefik with HTTPS, a file named [traefik.yaml](./reverse_proxy/traefik.yaml) has been created.

In this file, firstly, we have two sections called `api` and `providers` to enable the dashboard Traefix and to define the environment with which Traefik must communicate.

Then, we added two entrypoints to define on which input point traefik should listen.
In this case, we will have one for HTTP requests called `web` and another one for HTTPS requests called `websecure`.
Moreover, we will redirect all request on the port 80 to the port 443.
```
entryPoints:
  web:
    address: ":80"
    http:
      redirections:
        entryPoint:
          to: websecure
          scheme: https
          permanent: true
  websecure:
    address: ":443"
```
Moreover, a section called `tls` has been added to define the certificates.
```
tls:
  certificates:
    - certFile: /etc/traefik/certificates/cert.pem
      keyFile: /etc/traefik/certificates/key.pem
```

### Activating the HTTPS entrypoint for the servers
Finally, to activate the HTTPS entrypoint for the static web server and the API server, one label has been added to define the entrypoint on which the router listen to and one another label to activate the tls on the router.
```
- "traefik.http.routers.staticWeb-router.entrypoints=websecure"
- "traefik.http.routers.staticWeb-router.tls=true"
```

### Testing
If we tried to access both servers from a browser, we can see that they are accessible only through HTTPS.

## Optional step 1: Management UI
In our infrastructure, we have chosen to deploy `Portainer`.

`Portainer` is a web interface which allows us to easily manage a docker environment.

The official documentation of the tool can be found under :
https://docs.portainer.io/

This service has been added in the docker compose file.

It can be accessible through the URL : http://localhost:9000

## Optional step 2: Integration API - static Web site
A new section has been added in which we displayed the existing bar names.

It can be found at the bottom of the static website, below the contact information.

To add this, we make an API call. To do this we use JavaScript.

This is integrated directly into the HTML page.

It will run a GET request to the dynamic server to get all the bars, then it will display them in the section of the index.html file containing the id `barsRow`.
```
<div class="card py-4 h-100">
<div class="card-body text-center">
<h4 class="text-uppercase m-0">Bar Names</h4>
<hr class="my-4 mx-auto" />
<div class="small text-black-50" id="barsRow"></div>
```
``` HTML
  <script>
            let url = "/api/bars"

            fetch(url)
                .then(res => {
                    return res.json();
                })
                .then(data => {
                    Object.values(data).forEach(member => {
                        let newDiv = document.createElement("div");
                        newDiv.innerText = member.name;
                        document.getElementById("barsRow").appendChild(newDiv);
                    })
                })
                .catch(error => console.error(error));
        </script>
```