#! /bin/sh

jdbc_prefix='jdbc:postgresql://'
db_url=$jdbc_prefix`echo $DATABASE_URL | sed -e "s|^.*@||"`
db_user=`echo $DATABASE_URL | sed -e "s|^.*//||" -e "s|:.*||"`
db_password=`echo $DATABASE_URL | sed -e "s|@.*||" -e "s|^.*:||"`

find ./target -type f | xargs sed -i "s|{{DATABASE_URL}}|$db_url|g"
find ./target -type f | xargs sed -i "s|{{DATABASE_USER}}|$db_user|g"
find ./target -type f | xargs sed -i "s|{{DATABASE_PASSWORD}}|$db_password|g"
