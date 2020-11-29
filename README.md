# Local setup:

run with docker:
```
docker-compose build
docker-compose up
```

# Passwords for test users:

```
Martin: vK83ffVh4e
Michal: QaymHZc7rF
Aneta:  jCRLXUu44T
```


# Authentication:
```
curl -X POST 'localhost:8080/authenticate' -d '{"username": "michal@example.com", "password": "QaymHZc7rF"}' -H "Content-Type: application/json"
```


# Authors:

 - Martin Jablečník

