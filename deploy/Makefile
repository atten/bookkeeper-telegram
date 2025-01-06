include .env

deploy:
	docker compose pull
	docker compose up -d

logs:
	docker logs -f --since=30m bookkeeper-app

db_shell:
	docker exec -it bookkeeper-postgres psql --user ${POSTGRES_USER}

db_dump:
	docker exec -i bookkeeper-postgres pg_dump --user ${POSTGRES_USER} > dump.sql

restart:
	docker compose restart app
