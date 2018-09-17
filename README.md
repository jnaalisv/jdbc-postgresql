
Start the database:
```
$ sudo docker-compose -f postgres.yml up
```

Connect to the database:
```
psql -h localhost -p 5433 -U postgres
```