# Local setup:

Run with docker:
```
docker-compose build
docker-compose up
```

## Passwords for test users:

```
+--------------------+------------+
| username           | password   |
+--------------------+------------+
| martin@example.com | vK83ffVh4e |
| michal@example.com | QaymHZc7rF |
| aneta@example.com  | jCRLXUu44T |
+--------------------+------------+
```
<br>

# Example of usage:


Gain authentication token:
```
martin at probook-pc martin >>> curl -X POST 'localhost:8080/authenticate' -d '{"username": "michal@example.com", "password": "QaymHZc7rF"}' -H "Content-Type: application/json
"{"name":"Michal","email":"michal@example.com","token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtaWNoYWxAZXhhbXBsZS5jb20iLCJleHAiOjE2MDY5MzMyNTAsImlhdCI6MTYwNjkxNTI1MH0.ANfsgiTiMRO7rf52UMW8MxipVrr8fOz3WZBKlIy368hiFvkNUwCajGNMPk3o6ggQDUsYYyVHYJPnxJElE7jMGg"}%                                                                                                                                                                                                                                   
```

When you don't use token:
```
martin at probook-pc martin >>> curl -X GET 'localhost:8080/user' -H "Content-Type: application/json" 
{"timestamp":"2020-12-02T13:21:38.078+00:00","status":401,"error":"Unauthorized","message":"Unauthorized","path":"/user"}%                                                                                                                    
```

When you use token:
```
martin at probook-pc martin >>> curl -X GET 'localhost:8080/user' -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtaWNoYWxAZXhhbXBsZS5jb20iLCJleHAiOjE2MDY5MzMyNTAsImlhdCI6MTYwNjkxNTI1MH0.ANfsgiTiMRO7rf52UMW8MxipVrr8fOz3WZBKlIy368hiFvkNUwCajGNMPk3o6ggQDUsYYyVHYJPnxJElE7jMGg"
{"users":[{"id":1,"name":"Martin","email":"martin@example.com"},{"id":2,"name":"Michal","email":"michal@example.com"},{"id":3,"name":"Aneta","email":"aneta@example.com"}]}%                                                                  
```

Create new monitored endpoint:
```
martin at probook-pc martin >>> curl -X POST 'localhost:8080/monitor/endpoint' -d '{"name": "LEW word list", "url": "https://api.learn-english-words.eu/word/list?page=1&limit=3&state=correct", "interval": 60}' -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtaWNoYWxAZXhhbXBsZS5jb20iLCJleHAiOjE2MDY5MzMyNTAsImlhdCI6MTYwNjkxNTI1MH0.ANfsgiTiMRO7rf52UMW8MxipVrr8fOz3WZBKlIy368hiFvkNUwCajGNMPk3o6ggQDUsYYyVHYJPnxJElE7jMGg"
Created%                                                                                                                                                                                                                                      
```

List all own monitored endpoints:
```
martin at probook-pc martin >>> curl -X GET 'localhost:8080/monitor/endpoint' -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtaWNoYWxAZXhhbXBsZS5jb20iLCJleHAiOjE2MDY5MzMyNTAsImlhdCI6MTYwNjkxNTI1MH0.ANfsgiTiMRO7rf52UMW8MxipVrr8fOz3WZBKlIy368hiFvkNUwCajGNMPk3o6ggQDUsYYyVHYJPnxJElE7jMGg"
{"endpoints":[{"id":1,"name":"LEW word list","url":"https://api.learn-english-words.eu/word/list?page=1&limit=3&state=correct","created":"2020-12-02T13:34:47.903","lastCheck":"2020-12-02T13:34:49.417","monitoredInterval":60}]}%           
```


Every user can see onlu own monitored endpoints:
```
martin at probook-pc martin >>> curl -X GET 'localhost:8080/monitor/endpoint' -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtYXJ0aW5AZXhhbXBsZS5jb20iLCJleHAiOjE2MDY5MzQxNDcsImlhdCI6MTYwNjkxNjE0N30.mmNAMeRd58PnUs_jhL21GxunvIvwtmlxnt46hmsaj2C55_NyH9XOGipTblUte0MZC73ph2J0T0LSSXmJgmGqkw"
{"endpoints":[]}%                                                                                                                                                                                                                             
```

Get results:
```
martin at probook-pc martin >>> curl -X GET 'localhost:8080/monitor/endpoint/1/result' -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtaWNoYWxAZXhhbXBsZS5jb20iLCJleHAiOjE2MDY5MzMyNTAsImlhdCI6MTYwNjkxNTI1MH0.ANfsgiTiMRO7rf52UMW8MxipVrr8fOz3WZBKlIy368hiFvkNUwCajGNMPk3o6ggQDUsYYyVHYJPnxJElE7jMGg" | python -m json.tool
{
    "results": [
        {
            "dateOfCheck": "2020-12-02T13:36:49.802",
            "id": 4,
            "returnedHttpStatusCode": 200,
            "returnedPayload": "{\"status\":200,\"payload\":{\"count\":3746,\"words\":[{\"collectionId\":2,\"text\":\"car\",\"pronunciation\":{\"us\":\"k\u0251\u02d0r\",\"uk\":\"k\u0251\u02d0r\"},\"state\":\"CORRECT\",\"rank\":290,\"sense\":[\"auto\",\"automobil\",\"osobn\u00ed v\u016fz\"],\"examples\":[\"They don't have a car.\",\"Where did you park your car?\",\"It's quicker by car.\",\"She goes to work by car.\"],\"updated\":\"2020-05-25T04:05:57\",\"id\":2},{\"collectionId\":2,\"text\":\"hello\",\"pronunciation\":{\"us\":\"he\u02c8lo\u028a\",\"uk\":\"he\u02c8l\u0259\u028a\"},\"state\":\"CORRECT\",\"rank\":0,\"sense\":[\"ahoj\",\"nazdar\",\"\u010dau\"],\"examples\":[\"Hello, Paul. I haven't seen you for ages.\",\"I just thought I'd call by and say hello.\",\"Hello, this is very strange - I know that man.\",\"\\\"Hello, Paul,\\\" she said, \\\"I haven\u2019t seen you for months.\\\"\"],\"id\":3},{\"collectionId\":2,\"text\":\"dream\",\"pronunciation\":{\"us\":\"dri\u02d0m\",\"uk\":\"dri\u02d0m\"},\"state\":\"CORRECT\",\"rank\":993,\"sense\":[\"sen\",\"tou\u017eit\",\"p\u0159\u00e1t\"],\"examples\":[\"I had a very strange dream about you last night.\",\"Paul had a dream that he won the lottery.\",\"It's always been my dream to have flying lessons.\"],\"id\":5}]}}"
        },
        {
            "dateOfCheck": "2020-12-02T13:35:49.783",
            "id": 3,
            "returnedHttpStatusCode": 200,
            "returnedPayload": "{\"status\":200,\"payload\":{\"count\":3746,\"words\":[{\"collectionId\":2,\"text\":\"car\",\"pronunciation\":{\"us\":\"k\u0251\u02d0r\",\"uk\":\"k\u0251\u02d0r\"},\"state\":\"CORRECT\",\"rank\":290,\"sense\":[\"auto\",\"automobil\",\"osobn\u00ed v\u016fz\"],\"examples\":[\"They don't have a car.\",\"Where did you park your car?\",\"It's quicker by car.\",\"She goes to work by car.\"],\"updated\":\"2020-05-25T04:05:57\",\"id\":2},{\"collectionId\":2,\"text\":\"hello\",\"pronunciation\":{\"us\":\"he\u02c8lo\u028a\",\"uk\":\"he\u02c8l\u0259\u028a\"},\"state\":\"CORRECT\",\"rank\":0,\"sense\":[\"ahoj\",\"nazdar\",\"\u010dau\"],\"examples\":[\"Hello, Paul. I haven't seen you for ages.\",\"I just thought I'd call by and say hello.\",\"Hello, this is very strange - I know that man.\",\"\\\"Hello, Paul,\\\" she said, \\\"I haven\u2019t seen you for months.\\\"\"],\"id\":3},{\"collectionId\":2,\"text\":\"dream\",\"pronunciation\":{\"us\":\"dri\u02d0m\",\"uk\":\"dri\u02d0m\"},\"state\":\"CORRECT\",\"rank\":993,\"sense\":[\"sen\",\"tou\u017eit\",\"p\u0159\u00e1t\"],\"examples\":[\"I had a very strange dream about you last night.\",\"Paul had a dream that he won the lottery.\",\"It's always been my dream to have flying lessons.\"],\"id\":5}]}}"
        },
        {
            "dateOfCheck": "2020-12-02T13:34:49.439",
            "id": 2,
            "returnedHttpStatusCode": 200,
            "returnedPayload": "{\"status\":200,\"payload\":{\"count\":3746,\"words\":[{\"collectionId\":2,\"text\":\"car\",\"pronunciation\":{\"us\":\"k\u0251\u02d0r\",\"uk\":\"k\u0251\u02d0r\"},\"state\":\"CORRECT\",\"rank\":290,\"sense\":[\"auto\",\"automobil\",\"osobn\u00ed v\u016fz\"],\"examples\":[\"They don't have a car.\",\"Where did you park your car?\",\"It's quicker by car.\",\"She goes to work by car.\"],\"updated\":\"2020-05-25T04:05:57\",\"id\":2},{\"collectionId\":2,\"text\":\"hello\",\"pronunciation\":{\"us\":\"he\u02c8lo\u028a\",\"uk\":\"he\u02c8l\u0259\u028a\"},\"state\":\"CORRECT\",\"rank\":0,\"sense\":[\"ahoj\",\"nazdar\",\"\u010dau\"],\"examples\":[\"Hello, Paul. I haven't seen you for ages.\",\"I just thought I'd call by and say hello.\",\"Hello, this is very strange - I know that man.\",\"\\\"Hello, Paul,\\\" she said, \\\"I haven\u2019t seen you for months.\\\"\"],\"id\":3},{\"collectionId\":2,\"text\":\"dream\",\"pronunciation\":{\"us\":\"dri\u02d0m\",\"uk\":\"dri\u02d0m\"},\"state\":\"CORRECT\",\"rank\":993,\"sense\":[\"sen\",\"tou\u017eit\",\"p\u0159\u00e1t\"],\"examples\":[\"I had a very strange dream about you last night.\",\"Paul had a dream that he won the lottery.\",\"It's always been my dream to have flying lessons.\"],\"id\":5}]}}"
        }
    ]
}
```

Every user can show only own endpoints results:
```
martin at probook-pc martin >>> curl -X GET 'localhost:8080/monitor/endpoint/1/result' -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtYXJ0aW5AZXhhbXBsZS5jb20iLCJleHAiOjE2MDY5MzQxNDcsImlhdCI6MTYwNjkxNjE0N30.mmNAMeRd58PnUs_jhL21GxunvIvwtmlxnt46hmsaj2C55_NyH9XOGipTblUte0MZC73ph2J0T0LSSXmJgmGqkw"
You are not owner.%                                                                                                                                                                                                                           
```


When you want show results of not existing endpoint:
```
martin at probook-pc martin >>> curl -X GET 'localhost:8080/monitor/endpoint/9/result' -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtYXJ0aW5AZXhhbXBsZS5jb20iLCJleHAiOjE2MDY5MzQxNDcsImlhdCI6MTYwNjkxNjE0N30.mmNAMeRd58PnUs_jhL21GxunvIvwtmlxnt46hmsaj2C55_NyH9XOGipTblUte0MZC73ph2J0T0LSSXmJgmGqkw"
{"timestamp":"2020-12-02T13:39:49.268+00:00","status":404,"error":"Not Found","message":"MonitoredEndpoint with id 9 doesn't exists.","path":"/monitor/endpoint/9/result"}%                                                                   
```

<br>
<br>

# Endpoints:
## Authentication endpoints:
Endpoint: /authenticate <br>
Method:   POST 

Json data:
- username: String
- password: String

Response http code: 200 Ok <br>
Error response http codes: 
- 400 Bad Request 
- 401 Unauthorized

Json response body:
- username: String
- email: String
- token: String

Description: Get user Bearer token for using in other requests. 

Example:
```
curl -X POST 'localhost:8080/authenticate' -d '{"username": "michal@example.com", "password": "QaymHZc7rF"}' -H "Content-Type: application/json"
```

<br>

## User endpoints:
### Create User:
Endpoint: /user <br>
Method:   POST 

Json data:
- name: String
- email: String
- password: String

Response http code: 201 Created <br>
Error response http codes: 
- 400 Bad Request 
- 401 Unauthorized

Description: Create new user. 

Example:
```
curl -X POST 'localhost:8080/user' -d '{"name":"Aneta", "email":"aneta@example.com", "password": "jCRLXUu44T"}'  -H "Content-Type: application/json" -H "Authorization: Bearer $token"
```

### List Users:
Endpoint: /user <br>
Method:   GET

Response http code: 200 Ok <br>
Error response http codes: 
- 401 Unauthorized

Json response body:
- users:
    - id: Long
    - username: String
    - email: String

Description: List all users. 

Example:
```
curl -X GET 'localhost:8080/user' -H "Content-Type: application/json" -H "Authorization: Bearer $token"
```

### Remove User:
Endpoint: /user <br>
Method:   REMOVE

Response http code: 200 Ok <br>
Error response http codes: 
- 401 Unauthorized
- 404 Not Found

Description: Remove user. 

Example:
```
curl -X GET 'localhost:8080/user/1' -H "Authorization: Bearer $token"
```
<br>

## Monitoring endpoints:
### Add new monitor endpoint:
Endpoint: /monitor/endpoint <br>
Method:   POST

Json data:
- name: String
- url: String
- interval: Int

Response http code: 201 Ok <br>
Error response http codes: 
- 400 Bad Request
- 401 Unauthorized

Description: Add new monitored endpoint. 

Example:
```
curl -X POST 'localhost:8080/monitor/endpoint' -d '{"name":"Example", "url":"http://www.example.com", "interval": 10}'  -H "Content-Type: application/json" -H "Authorization: Bearer $token"
```

### Update monitor endpoint:
Endpoint: /monitor/endpoint/{id} <br>
Method:   PUT

Json data:
- name: String
- url: String
- interval: Int

Response http code: 200 Ok <br>
Error response http codes: 
- 400 Bad Request
- 401 Unauthorized
- 403 Forbidden

Description: Update monitored endpoint. 

Example:
```
curl -X PUT 'localhost:8080/monitor/endpoint/1' -d '{"name":"Example", "url":"http://www.example.com", "interval": 600}'  -H "Content-Type: application/json" -H "Authorization: Bearer $token"
```

### Remove monitor endpoint:
Endpoint: /monitor/endpoint/{id} <br>
Method:   DELETE

Response http code: 200 Ok <br>
Error response http codes: 
- 400 Bad Request
- 401 Unauthorized
- 403 Forbidden

Description: Remove monitored endpoint. 

Example:
```
curl -X DELETE 'localhost:8080/monitor/endpoint/1' -H "Authorization: Bearer $token"
```

### List monitor endpoints:
Endpoint: /monitor/endpoint <br>
Method:   GET <br>
Parameters:
- page: Int - default 1
- size: Int - default 10

Response http code: 200 Ok <br>
Error response http codes: 
- 400 Bad Request
- 401 Unauthorized
- 403 Forbidden

Json response body:
- endpoints:
    - id: Long
    - name: String
    - url: String
    - created: DateTime
    - lastCheck: DateTime
    - monitoredInterval: Int

Description: List monitored endpoints. 

Example:
```
curl -X GET 'localhost:8080/monitor/endpoint?page=1&size=5' -H "Content-Type: application/json" -H "Authorization: Bearer $token"
```

### List results of monitored endpoint:
Endpoint: /monitor/endpoint/{id}/result <br>
Method:   GET <br>
Parameters:
- page: Int - default 1
- size: Int - default 10
- sort: Enum(DESC, ASC) - default DESC

Response http code: 200 Ok <br>
Error response http codes: 
- 400 Bad Request
- 401 Unauthorized
- 403 Forbidden

Json response body:
- endpoints:
    - id: Long
    - dateOfCheck: DateTime
    - returnedHttpStatusCode: Int
    - returnedPayload: String

Description: List results of monitored endpoint. 

Example:
```
curl -X GET 'localhost:8080/monitor/endpoint/1/result' -H "Content-Type: application/json" -H "Authorization: Bearer $token"
```
