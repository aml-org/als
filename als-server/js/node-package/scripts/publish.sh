#!/usr/bin/env bash
# $1 represents the project's version that is going to be published
# $2 has the auth token
# $3 contains the branch name from where the script has run

FULL_VERSION=$1
NPM_TOKEN=$2
BRANCH=$3

IS_MASTER=false
if [[ "$BRANCH" == "master" ]]; then
    IS_MASTER=true
fi

echo "Publish version: $FULL_VERSION"

DIR="$( cd "$( dirname "$0" )" && pwd )"
BASE_DIR="$( cd "${DIR}/.." && pwd )"
DIST_DIR=$BASE_DIR

cd $DIST_DIR
echo "Connecting to MuleSoft internal registry"
printf "@mulesoft:registry=https://nexus3.build.msap.io/repository/npm-internal/\n//nexus3.build.msap.io/repository/npm-internal/:_authToken=$NPM_TOKEN" > .npmrc
touch .npmrc

LATEST_TAG=`npm v @mulesoft/als-server dist-tags.releases`

echo "Repo latest tag: $LATEST_TAG"

echo "Start release from scratch"
npm version ${FULL_VERSION} --force --no-git-tag-version --allow-same-version

echo "Publish new version"
if [ "$IS_MASTER" = true ] ; then
  echo "Tagging as release"
  npm publish --tag release
else
  echo "Tagging as prerelease"
  npm publish --tag prerelease
fi
status=$?
if [ $status -eq 0 ]; then
 echo "Finished publish"
 echo "To version: $FULL_VERSION"
else
 echo "Publish failed"
fi

echo "Clean working directory"
rm -rf $DIST_DIR/dist

exit $status