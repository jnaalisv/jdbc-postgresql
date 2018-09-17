
Start the database:
```
$ sudo docker-compose -f postgres.yml up -d
```

Migrations have to be applied manually.

Connect to the database:
```
psql -h localhost -p 5433 -U postgres
```