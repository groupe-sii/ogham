#!/bin/sh

REPORTS_DIR=${1:-/tmp/ogham-dev-reports}

# accept github.com host
ssh-keyscan github.com >> ~/.ssh/known_hosts

# clone dev-reports
git clone git@github.com:aurelien-baudet/ogham-dev-reports.git $REPORTS_DIR
# copy generated site
mkdir -p $REPORTS_DIR/$CIRCLE_BRANCH && cp -r target/site/* $REPORTS_DIR/$CIRCLE_BRANCH
# update main index to list Ogham branches
links=""
while read branch; do
	dirname="${branch#origin/}"
	if [ -d "$REPORTS_DIR/$dirname" ]; then
		echo "$dirname"
		links="$links <a href='$dirname'>$dirname</a>"
	fi
done << EOF
$(git for-each-ref --format='%(refname:short)')
EOF
echo "<html><head></head><body>$links</body>" > $REPORTS_DIR/index.html
# TODO: remove old branches ?

# commit and push generated site
cd $REPORTS_DIR && git config user.email $GH_EMAIL && git config user.name $GH_NAME && git add . && git commit -m "add/update $CIRCLE_BRANCH" && git push