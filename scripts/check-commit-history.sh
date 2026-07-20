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

if [[ "$base_sha" == "$zero_sha" ]] || ! git cat-file -e "${base_sha}^{commit}" 2>/dev/null; then
    if [[ "$base_sha" != "$zero_sha" ]]; then
        echo "base commit is unavailable; validating the complete history at $head_sha" >&2
    fi
    range=$head_sha
else
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
