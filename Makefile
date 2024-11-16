up:
	docker compose -f docker-compose.dev.yml up -d

down:
	docker compose -f docker-compose.dev.yml stop

logs:
	docker compose -f docker-compose.dev.yml logs -f --tail=100

build:
	./gradlew jibDockerBuild --image=bookkeeper:local


# ========================= DATABASE ============================

db_shell:
	docker exec -it bookkeeper-postgres-dev psql -U bookkeeper -d bookkeeper

db_shell_plus:
	pgcli postgres://bookkeeper:bookkeeper@localhost:5434/bookkeeper

db_recreate:
	docker stop bookkeeper-postgres-dev
	docker container rm bookkeeper-postgres-dev
	docker volume rm bookkeeper-postgres-dev
	docker compose -f docker-compose.dev.yml up -d postgres_dev

db_dump:
	docker exec -i bookkeeper-postgres-dev pg_dump --user bookkeeper > dump_dev.sql

db_restore: db_recreate
	sleep 3
	docker exec -i bookkeeper-postgres-dev psql -U bookkeeper -d bookkeeper < dump_dev.sql


# ========================= TESTS ============================

up_test_env:
	docker compose -f docker-compose.dev.yml up -d
	docker exec -i bookkeeper-postgres-dev psql -U bookkeeper -d postgres -c "CREATE DATABASE bookkeeper_test"

test:
	./gradlew test
