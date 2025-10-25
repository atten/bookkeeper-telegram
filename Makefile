up:
	docker compose -f docker-compose.dev.yml up -d

down:
	docker compose -f docker-compose.dev.yml stop

logs:
	docker compose -f docker-compose.dev.yml logs -f --tail=100

build_image:
	./gradlew jibDockerBuild --image=bookkeeper:local


# ========================= DATABASE ============================

YDB_CMD = echo 1234 > /tmp/bookkeeper-password && ydb --endpoint grpc://127.0.0.1:2136 --database /local --user root --password-file /tmp/bookkeeper-password

db_shell:
	$(YDB_CMD)

db_recreate:
	docker stop bookkeeper-ydb-dev
	docker container rm bookkeeper-ydb-dev
	docker compose -f docker-compose.dev.yml up -d ydb_dev

db_dump:
	rm -rf backup
	$(YDB_CMD) tools dump --output backup

db_restore: db_recreate
	sleep 3
	$(YDB_CMD) tools restore --input backup --path .


# ========================= TESTS ============================

test:
	./gradlew test
