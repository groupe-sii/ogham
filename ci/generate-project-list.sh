#!/bin/sh

set -e${DEBUG_COMMANDS:+x}

parent="$1"
part=${2%/*}
parts=${2#*/}

folders="$(find $HOME/classpath-tests/$parent -mindepth 1 -maxdepth 1 -type d | grep -v '.mvn')"
num_folders="$(echo "$folders" | wc -l)"

num_per_part=$(( ($num_folders + $parts - 1) / $parts ))

start=$(( ($part - 1) * $num_per_part + 1 ))
end=$(( $start + $num_per_part - 1 ))

project_list=""
for folder in $folders; do
	project_list="$project_list$(basename $folder),"
done
project_list=${project_list%,}

echo "$project_list" | cut -d, -f${start}-${end}
