# The FDWeb protocol v1.0

# P2P Protocol

The P2P messages are setup like this:

```
length:<length of message in lines>
FDWeb:1.0
ReqType:<request type (GET, POST)>
Encoding:base64
Site:<base of url ex: thatmadhacker.org>
Page:<page of url without the domain at the front ex: /index.html>
[Extra content goes here (Only for POST requests)]
```
