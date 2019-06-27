# cisapify
Web version of [cisap](https://cisap.madadipouya.com) 


## Run

```bash
$ mvn spring-boot:run
```

Run with `developer` profile

```bash
$ mvn clean compile -Pdeveloper
```

Set `mysql` profile:

```bash
$ export SPRING_PROFILES_ACTIVE=mysql
```

## Build Docker images

- Local build: `mvn dockerfile:build` or `mvn clean package`
- Push to registry: `mvn dockerfile:push` or `mvn clean deploy`