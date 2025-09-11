#!/bin/bash

format_number() {
    printf "%sL" "$(printf "%s" "$1" | rev | sed 's/\(.\{3\}\)/\1_/g' | sed 's/_$//' | rev)"
}

START_POW=${1:-1}
END_POW=${2:-62}

for (( pow=START_POW; pow<=END_POW; pow++ )); do
    two_pow="2^${pow}"
    echo "Calculating pi(${two_pow})..." >&2
    pi=$(primecount "$two_pow")

    plain_num=$(echo $two_pow | bc)
    map_entry="$(format_number "$plain_num") to $(format_number "$pi"),"
    printf "%s " "$map_entry"

    if (( pow % 16 == 0 )); then
        printf "\n"
    fi
done

printf "\n"
echo "Generation complete" >&2
