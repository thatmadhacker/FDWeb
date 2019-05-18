# The FDWeb protocol v1.0

# P2P Protocol

The P2P requests are setup like this:

```
Length:<length of request in lines>
Version:1.0
ReqType:<request type (GET, POST,PEERS)>
Encoding:base64
Site:<base of url ex: thatmadhacker.org>
Page:<page of url without the domain at the front ex: /index.html>
ContentLen:[Content length in lines]
[Extra content goes here (Only for POST requests)]
```

The checksum requests are setup like this:

```
Length:5
Version:1.0
ReqType:CHECKSUM
Encoding:base64
Site:<base of url ex: thatmadhacker.org>
Page:<page of url without the domain at the front ex: /index.html>
```
Note that checksum requests should only be sent directly to the central server and not to other peers

The P2P responses are setup like this:

```
Length:<length of the response in lines>
Version:1.0
Encoding:base64
Site:<echo of the page's domain>
Page:<echo of the page that was requested>
Status:<status, one of these: NOT_FOUND, REDIRECT, FAILED_UNKNOWN, PEER_ERROR, SUCCESS,BAD_REQUEST>
ContentLen:<Page content length in lines>
<Page content>
```

The checksum responses are setup like this:

```
Length:7
Version:1.0
Encoding:base64
Site:<echo of the page's domain>
Page:<echo of the page that was requested>
Status:<status, one of these: NOT_FOUND, FAILED_UNKNOWN, PEER_ERROR, SUCCESS>
ContentLen:<length of checksum in lines>
<Checksum>
```