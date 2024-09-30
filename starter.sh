#!/bin/bash

# Porta di partenza
start_port=9091
# Numero di istanze da avviare
instances=128

# Ottieni il percorso corrente
current_dir=$(pwd)

# Variabili da passare
variabile_pari="MRCLSN97C14H501J^^^&2.16.840.1.113883.2.9.4.3.2&ISO"
variabile_dispari="NGNVCN92S19L259C^^^&2.16.840.1.113883.2.9.4.3.2&ISO"

# Loop per avviare ogni istanza su una porta differente
for ((i=0; i<$instances; i++))
do
  port=$(($start_port + $i))
  profile="dev"
  
  # Controllo se i è pari o dispari
  if (( $i % 2 == 0 )); then
    variabile=$variabile_pari
  else
    variabile=$variabile_dispari
  fi

  echo "Starting instance on port $port with profile $profile and variable $variabile"

  # Aprire una nuova finestra di terminale per ogni istanza nel percorso corrente
  osascript -e "tell application \"Terminal\" to do script \"cd '$current_dir' && java -jar target/gtw-ini-client-ms.jar --server.port=$port --spring.profiles.active=$profile --paziente.prova='$variabile'\""
done
