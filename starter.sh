#!/bin/bash

# Porta di partenza
start_port=9091
# Numero di istanze da avviare
instances=64

# Ottieni il percorso corrente
current_dir=$(pwd)

# Loop per avviare ogni istanza su una porta differente
for ((i=0; i<$instances; i++))
do
  port=$(($start_port + $i))
  profile="dev"
  echo "Starting instance on port $port with profile $profile"
  
  # Aprire una nuova finestra di terminale per ogni istanza nel percorso corrente
  osascript -e "tell application \"Terminal\" to do script \"cd '$current_dir' && java -jar target/gtw-ini-client-ms.jar --server.port=$port --spring.profiles.active=$profile\""
done
