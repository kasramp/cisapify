#!/bin/bash

if [[ $# -eq 0 ]]
  then
    echo -e "No arguments supplied."
    echo -e "Needed arguments (in order):"
    echo -e "\t - URI of the database"
    echo -e "\t - Username of the database"
    echo -e "\t - Password of the database"
    echo -e "\t - Dropbox App Key"
    echo -e "\t - Dropbox App Secret"
    exit 1
fi

mkdir -p static/songs

docker pull kasramp/cisapify

docker run --mount src=$(pwd)/static/songs,target=/user/share/cisapify/static/songs,type=bind -d -p80:8080 -e SPRING_DATASOURCE_URL=$1 -e SPRING_DATASOURCE_USERNAME=$2 -e SPRING_DATASOURCE_PASSWORD=$3 -e APP_INFO_KEY=$4 -e APP_INFO_SECRET=$5 registry.hub.docker.com/kasramp/cisapify:latest


echo -e "Successfully ran the container"
docker ps | grep kasramp/cisapify