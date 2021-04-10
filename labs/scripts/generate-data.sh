#!/bin/bash
#products
for i in $(seq 101 164); do P=("F" "D") && N=("ICECREAM" "SHAKE") && echo "${P[0+$RANDOM%2]},$i,${N[0+$RANDOM%2]}_$i,$((1+$RANDOM%10)).99,$((1+$RANDOM%5)),2022-01-01" > product$i.csv; done
#reviews
for i in $(seq 101 164); do for n in $(seq 1 $((1+$RANDOM%5))); do LIST=("Not Bad" "Ok" "Hot" "Good" "Fizzy") &&  echo "$((1+$RANDOM%5)), ${LIST[0+$RANDOM%5]}" >> reviews$i.csv; done; done

