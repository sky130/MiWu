#!/usr/bin/env bash

set -euo pipefail

if [[ $# -ne 2 ]]; then
    echo "usage: $0 <base-sha> <head-sha>" >&2
    exit 2
fi

base_sha=$1
head_sha=$2
zero_sha=0000000000000000000000000000000000000000

git rev-parse --verify "${head_sha}^{commit}" >/dev/null

if [[ "$base_sha" == "$zero_sha" ]]; then
    range=$head_sha
else
    git rev-parse --verify "${base_sha}^{commit}" >/dev/null
    range="${base_sha}..${head_sha}"
fi

failed=0

while IFS= read -r commit_sha; do
    [[ -z "$commit_sha" ]] && continue
    echo "merge commit is not allowed: $commit_sha $(git show -s --format=%s "$commit_sha")" >&2
    failed=1
done < <(git rev-list --reverse --merges "$range")

while IFS= read -r commit_sha; do
    [[ -z "$commit_sha" ]] && continue
    subject=$(git show -s --format=%s "$commit_sha")
    if [[ ! "$subject" =~ ^[a-z] ]]; then
        echo "commit subject must start with a lowercase letter: $commit_sha $subject" >&2
        failed=1
    fi
done < <(git rev-list --reverse "$range")

exit "$failed"
