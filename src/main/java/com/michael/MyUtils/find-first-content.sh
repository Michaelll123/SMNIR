#!/bin/bash
# usage: ./find-first-content.sh <file_path> "<target content>"

FILE="$1"
CONTENT="$2"

# get all the related commits（ordered by old to new）
COMMITS=$(git log --reverse --pretty=format:%H --follow -- "$FILE")

for commit in $COMMITS; do
  # check whether file exist in this commit
  if git ls-tree -r --name-only $commit | grep -q "$FILE$"; then
	echo "this commit contains file"
    # extract file content and search for the target content
    if git show $commit:"$FILE" | grep -q "$CONTENT"; then
      echo "first commit is : $commit"
      git show -s --format="%h - %an, %ad (%ar)" $commit
      exit 0
    fi
  fi
done

echo "not found commits containing '$CONTENT'"
exit 1