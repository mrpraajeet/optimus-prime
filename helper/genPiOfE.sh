#!/bin/bash

format_number() {
    printf "%sL" "$(printf "%s" "$1" | rev | sed 's/\(.\{3\}\)/\1_/g' | sed 's/_$//' | rev)"
}

START_EXP=${1:-1}
END_EXP=${2:-18}

for (( exp=START_EXP; exp<=END_EXP; exp++ )); do
    for (( mult=1; mult<=9; mult++ )); do
        if (( mult == 1 && exp > START_EXP )); then
            printf "\n"
        fi

        e_num="${mult}e${exp}"
        echo "Calculating pi(${e_num})..." >&2
        pi=$(primecount "$e_num")

        plain_num=$(printf "%d%0*d" "$mult" "$exp" 0)
        map_entry="$(format_number "$plain_num") to $(format_number "$pi"),"
        printf "%s " "$map_entry"
    done
done

printf "\n"
echo "Generation complete" >&2
