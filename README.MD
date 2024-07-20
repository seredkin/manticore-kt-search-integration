# How to reproduce the error

Run `./gradlew test --info`

And check the error in the log:
``` 
Unexpected JSON token at offset 113: Expected start of the object '{', but had '2' instead at path: $.hits.total
JSON input: ....."failed": 0 },"hits":{"total":2,"total_relation":"eq","max_s.....
kotlinx.serialization.json.internal.JsonDecodingException: Unexpected JSON token at offset 113: Expected start of the object '{', but had '2' instead at path: $.hits.total
JSON input: ....."failed": 0 },"hits":{"total":2,"total_relation":"eq","max_s.....
```

## Expected behavior:

Search result returns by the KTSearch client alike it does with OpenSearch and ElasticSearch