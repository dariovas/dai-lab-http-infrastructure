# DAI lab: HTTP Infrastructure
***

## Static Web Server
The configuration files are included in the Dockerfile and the nginx.conf.

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