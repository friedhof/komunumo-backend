*Komunumo Backend*
==================

[![Build Status](https://travis-ci.org/komunumo/komunumo-backend.svg?branch=master)](https://travis-ci.org/komunumo/komunumo-backend) [![Stories in Ready](https://badge.waffle.io/komunumo/komunumo-backend.png?label=ready&title=ready)](http://waffle.io/komunumo/komunumo-backend) [![gitmoji](https://img.shields.io/badge/gitmoji-%20😜%20😍-FFDD67.svg)](https://gitmoji.carloscuesta.me)

**Open Source Community Manager**

*Copyright (C) 2017 Java User Group Switzerland*

This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.

## The Name

*Komunumo* is an [esperanto](https://wikipedia.org/wiki/Esperanto) noun with a meaning of *community*.

## Configuration

In your home directory create a new directory with the name `.komunumo` (yes, the name of the directory starts with a dot, which hides the directory on unix systems by default). Inside of this directory create a text file with the name `komunumo.cfg` which you can use to configure *Komunumo*. This configuration file can contain as many empty lines and comments (lines starting with a `#` character) as you like.

### Example configuration file
```
# Security
token.signing.key = very secret text 
token.expiration.time = 480

# Administrator
admin.firstname = John
admin.lastname = Doe
admin.email = john.doe@mydomain.com

# SMTP Server information
smtp.server = mail.mydomain.com
smtp.port = 465
smtp.user = no-reply@mydomain.com
smtp.password = very secret password
smtp.useSSL = true
smtp.from = no-reply@mydomain.com

# Server
server.baseURL = https://mydomain.com
```

Most configuration options are self-descriptive. The `token.signing.key` is just text like a password which is used to sign the JSON Web Token with a private key. The `token.expiration.time` is a number specifying the time a JSON Web Token is valid until it expires (in minutes, 480 minutes are 8 hours = about 8 hours after a successful authorization the user has to authorize again). The `server.baseURL` is used as a prefix for automatically generated links like in the `Location` header of a response and in emails.

## Throughput

[![Throughput Graph](https://graphs.waffle.io/komunumo/komunumo-backend/throughput.svg)](https://waffle.io/komunumo/komunumo-backend/metrics/throughput)

