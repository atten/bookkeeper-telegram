up:
	docker-compose -f docker-compose.dev.yml up -d

down:
	docker-compose -f docker-compose.dev.yml stop

logs:
	docker-compose -f docker-compose.dev.yml logs -f --tail=100

build:
	./gradlew jibDockerBuild --image=bookkeeper:local


# ========================= DATABASE ============================

db_shell:
	docker exec -it bookkeeper-postgres-dev psql -U bookkeeper -d bookkeeper
