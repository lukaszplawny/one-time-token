# One Time Token
Application generates one time token for specified url. Token is valid for a limited time.
When user send request with valid token, then it is redirected to url related to the token.
If the token is invalid, then 404 http response code is returned.

## Getting Started

### Prerequisites
* Cassandra database for storing tokens is installed and running,

### Installation
1. Clone git repository
```
git clone https://github.com/lukaszplawny/one-time-token.git
```
2. Optional step: 
Change default configuration
* Tune token time-to-live if needed. Default TTL value is set to 20 seconds. TTL value needs to be greater than 0, otherwise default value is used.
* Change Cassandra properties. Needed if not default user, host or port are used.
```
cat ./src/main/resources/config/application.properties 
# Application specific configuration
# token time-to-live specified in seconds
token.ttl=20

# Cassandra properties
spring.data.cassandra.contact-points=localhost
spring.data.cassandra.port=9142
spring.data.cassandra.password=cassandra
spring.data.cassandra.username=cassandra
```
3. Optional step: 
Build and test the application
```
mvn clean install
```
or build and execute system test (system tests are skipped by default):
```
mvn clean install -Dsystemtest.skip=false
```
4. Run the application
```
mvn spring-boot:run
```


## Usage example
```
# generate token
lukas@lukas-laptop:~$ curl -X POST http://localhost:8080/token?url=http://www.google.com -v
*   Trying 127.0.0.1...
* Connected to localhost (127.0.0.1) port 8080 (#0)
> POST /token?url=http://www.google.com HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.47.0
> Accept: */*
> 
< HTTP/1.1 201 
< Content-Type: text/plain;charset=UTF-8
< Content-Length: 12
< Date: Fri, 20 Oct 2017 01:27:05 GMT
< 
* Connection #0 to host localhost left intact
SUVGPl6mpE1J

#use generated token
lukas@lukas-laptop:~$ curl -X GET http://localhost:8080/token/SUVGPl6mpE1J -v
Note: Unnecessary use of -X or --request, GET is already inferred.
*   Trying 127.0.0.1...
* Connected to localhost (127.0.0.1) port 8080 (#0)
> GET /token/SUVGPl6mpE1J HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.47.0
> Accept: */*
> 
< HTTP/1.1 302 
< Location: http://www.google.com
< Content-Length: 0
< Date: Fri, 20 Oct 2017 01:27:19 GMT
< 
* Connection #0 to host localhost left intact

#20 seconds later try to use generated token
lukas@lukas-laptop:~$ curl -X GET http://localhost:8080/token/SUVGPl6mpE1J -v
Note: Unnecessary use of -X or --request, GET is already inferred.
*   Trying 127.0.0.1...
* Connected to localhost (127.0.0.1) port 8080 (#0)
> GET /token/SUVGPl6mpE1J HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.47.0
> Accept: */*
> 
< HTTP/1.1 404 
< Content-Length: 0
< Date: Fri, 20 Oct 2017 01:28:15 GMT
< 
* Connection #0 to host localhost left intact
lukas@lukas-laptop:~$ 

```

## TO DOs:

* improve logging
* separete system test and application by introducing two maven modules. spring-boot-plugin doesn't support starting application from another module, so other plugin needs to be used ()
