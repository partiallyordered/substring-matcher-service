#!/usr/bin/env sh
DICT_ID=$(uuidgen)
TARGET="This text tests annotation. The words test and entry exist in the dictionary, so \"test\"
and \"entry\" will appear a couple of times in this text. Sometimes in proper case like Test or
Entry."


echo "Creating dictionary"

curl -i \
    -X POST \
    -H 'content-type: application/json' \
    -H 'accept: application/json' \
    --data "{\"id\":\"$DICT_ID\",\"entries\":[\"test\",\"entry\"],\"is_case_sensitive\":false}" \
    "localhost:8080/dictionary"
echo ""


echo "Testing annotation"

curl -i -G \
    -H 'content-type: application/json' \
    -H 'accept: application/json' \
    --data-urlencode "dictId=$DICT_ID" \
    --data-urlencode "target=$TARGET" \
    "localhost:8080/annotate"
echo ""


echo "Modifying dictionary entries and is_case_sensitive"

curl -i \
    -X PUT \
    -H 'content-type: application/json' \
    -H 'accept: application/json' \
    --data "[\"Entry\"]" \
    "localhost:8080/dictionary/$DICT_ID/entries"
echo ""

curl -i \
    -X PUT \
    -H 'content-type: application/json' \
    -H 'accept: application/json' \
    --data "true" \
    "localhost:8080/dictionary/$DICT_ID/is_case_sensitive"
echo ""


echo "Testing annotation with new dictionary entries and is_case_sensitive"

curl -i -G \
    -H 'content-type: application/json' \
    -H 'accept: application/json' \
    --data-urlencode "dictId=$DICT_ID" \
    --data-urlencode "target=$TARGET" \
    "localhost:8080/annotate"
echo ""
