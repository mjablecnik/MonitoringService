# Local setup:

run with docker:
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

# Endpoints:
## Authentication endpoints:
```
curl -X POST 'localhost:8080/authenticate' -d '{"username": "michal@example.com", "password": "QaymHZc7rF"}' -H "Content-Type: application/json"
```

## User endpoints:
### Create User:
```
curl -X POST 'localhost:8080/user' -d '{"name":"Aneta", "email":"aneta@example.com", "password": "jCRLXUu44T"}'  -H "Content-Type: application/json" -H "Authorization: Bearer $token"
```

### List Users:
```
curl -X GET 'localhost:8080/user' -H "Content-Type: application/json" -H "Authorization: Bearer $token"
```

### Remove User:
```
curl -X GET 'localhost:8080/user/{id}' -H "Authorization: Bearer $token"
```

## Monitoring endpoints:
### Add new monitor endpoint:
```
curl -X POST 'localhost:8080/monitor/endpoint' -d '{"name":"Example", "url":"http://www.example.com", "interval": 10}'  -H "Content-Type: application/json" -H "Authorization: Bearer $token"
```

### Update monitor endpoint:
```
curl -X PUT 'localhost:8080/monitor/endpoint/{id}' -d '{"name":"Example", "url":"http://www.example.com", "interval": 600}'  -H "Content-Type: application/json" -H "Authorization: Bearer $token"
```

### Remove monitor endpoint:
```
curl -X DELETE 'localhost:8080/monitor/endpoint/{id}' -H "Authorization: Bearer $token"
```

### List all monitor endpoints:
```
curl -X GET 'localhost:8080/monitor/endpoint' -H "Content-Type: application/json" -H "Authorization: Bearer $token"
```

### List all results for monitored endpoint:
```
curl -X GET 'localhost:8080/monitor/endpoint/{id}/result' -H "Content-Type: application/json" -H "Authorization: Bearer $token"
```



# Authors:

 - Martin Jablečník

