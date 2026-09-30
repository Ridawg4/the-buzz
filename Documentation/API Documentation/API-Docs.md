# API Documentation

#### This document is created to provide the inputs and outputs of all endpoints provided by the backend to improve front-end development speed.

    This will be broken into segments based on the API endpoints, starting from the global endpoint:
    ex. 1) 127.0.0.1:8080/api/ -> /api
    ex. 2) 127.0.0.1:8080/index.html -> /index.html


/api/v1/audio/upload :
    POST: Upload endpoint for live audio segments
        This endpoint will take Multi-part Files from forms
        This endpoint will return a 202 Accepted

/api/v1/audio/live/{path} :
    GET:

/api/v1/audio/{resource}/{filename} :
    GET:
