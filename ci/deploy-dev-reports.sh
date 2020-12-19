#!/bin/sh

set -e${DEBUG_COMMANDS:+x}

BRANCH=$1
REPORTS_DIR=${2:-/tmp/ogham-dev-reports}

if [ "$CLONE_REPOSITORY" = "true" ]; then
	# accept github.com host
	ssh-keyscan github.com >> ~/.ssh/known_hosts
	
	# clone dev-reports
	git clone git@github.com:aurelien-baudet/ogham-dev-reports.git $REPORTS_DIR
fi

# copy generated site
mkdir -p $REPORTS_DIR/$BRANCH && cp -r target/site/* $REPORTS_DIR/$BRANCH

# update main index to list Ogham branches
branchnames=$(git for-each-ref --format='%(refname:short)' | while read -r branch; do echo "${branch#origin/}"; done | sort | uniq)
links=""
while read branch; do
	if [ -d "$REPORTS_DIR/$branch" ]; then
		echo "$branch"
		links="$links <li><a href='$branch'>$branch</a></li>"
	fi
done << EOF
$(echo "$branchnames")
EOF
echo "<html><head><title>dev reports</title></head><body><ul>$links</ul></body>" > $REPORTS_DIR/index.html
# TODO: remove old branches ?

# commit and push generated site
cd $REPORTS_DIR && git config user.email github-actions@github.com && git config user.name github-actions && git add . && git commit -m "add/update $BRANCH" && git push
