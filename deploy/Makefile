include .env

deploy:
	docker compose pull
	docker compose up -d

logs:
	docker logs -f --since=30m bookkeeper-app

db_shell:
	ydb --endpoint ${YDB_ENDPOINT} --database ${YDB_DATABASE} --sa-key-file ${YDB_SA_KEY_PATH}

db_dump:
	ydb --endpoint ${YDB_ENDPOINT} --database ${YDB_DATABASE} --sa-key-file ${YDB_SA_KEY_PATH} tools dump --output dump
	tar -czf dump.tar.gz dump/
	rm -rf dump/

restart:
	docker compose restart app
