include .env

YDB_CMD = ydb --endpoint ${YDB_ENDPOINT} --database ${YDB_DATABASE} --sa-key-file ${YDB_SA_KEY_PATH}

deploy:
	docker compose pull
	docker compose up -d

logs:
	docker logs -f --since=30m bookkeeper-app

db_shell:
	$(YDB_CMD)

db_dump:
	$(YDB_CMD) tools dump --output dump
	tar -czf dump.tar.gz dump/
	rm -rf dump/

restart:
	docker compose restart app
