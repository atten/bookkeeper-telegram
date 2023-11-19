deploy:
	$(grep -v '^#' .env | xargs) docker-compose pull
	$(grep -v '^#' .env | xargs) docker-compose up -d

logs:
	docker logs -f --since=30m bookkeeper-app

db_shell:
	$(grep -v '^#' .env | xargs) docker exec -it bookkeeper-postgres psql --user bookkeeper

restart:
	docker-compose restart app
