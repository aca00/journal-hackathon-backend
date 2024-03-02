sudo docker compose rm --stop --force
sudo docker rm -f $(docker ps -a -q)

sudo docker compose \
 -f docker-compose.yml
#
#mvn clean install
