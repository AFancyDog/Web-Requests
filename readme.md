# Web Requests

-# By AFancyDog

The Web Requests mod adds one command, `/request`, which contains the two sub-commands `get` and `post`

## `Get`

`/request get <URL> <headers>`
example:
`/request get https://example.com "foo:bar:::hello:world"`

## `Post`

`/request post <URL> <headers> <body>`

### URL

A string _that should be contained in quotation marks ("")_

### headers

headers are formatted like so: `key1:value1:::key2:value2`, triple semicolons are the equivalent of a comma

### body

the body is a string _also in quotation marks ("")_
