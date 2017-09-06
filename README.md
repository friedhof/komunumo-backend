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

## Installation

### From Source

#### Prerequisites

- [git](https://git-scm.com)
- [Java](https://www.oracle.com/technetwork/java/javase/downloads) 8 or newer

#### Build

1. Clone this repository to your computer: `git clone https://github.com/komunumo/komunumo-backend.git`
2. Enter the newly created project directory: `cd komunumo-backend`
3. Build the artifact from source: `./gradlew assemble`
4. The artifact can be found in the following directory: `build/libs`

#### Run

1. Place your [configuration file](#configuration) in the directory: `~/.komunumo`
2. Start the artifact: `./gradlew run`
3. To stop the running server, press: `CTRL+C`

### Using Docker

#### Prerequisites

- [Docker](https://www.docker.com)

#### Command Line

`docker run -it -p [localport]:8080 -v [datadir]:/root/.komunumo --name komunumo --rm komunumo/komunumo-backend`

Replace `[localport]` with the port number you would like to assign to your running *Komunumo*  backend server and replace `[datadir]` with the directory on your local drive that contains the *Komunumo*  backend [configuration file](#configuration). This directory will be used to store the business data, too.

We suggest to start the *Komunumo* backend server using the `-it` and `--rm` command line parameters to activate the interactive mode (you will see all logging output directkly at the console) and to remove the container after the server was shut down. It is always a good idea to assign a descriptive name to the container (`--name`).

#### Example

`docker run -it -p 8080:8080 -v ~/.komunumo:/root/.komunumo --name komunumo --rm komunumo/komunumo-backend`

This command uses the local port `8080` for the *Komunumo* backend server. The [configuration file](#configuration) is located in the folder `~/.komunumo` which will be used to store the business data, too. The server will be started in interactive mode (`-it`), so all logging output will be shown directly on the console and the server can stopped using `Ctrl+C` at any time. The running container will have the name `komunumo` so it easy to identify if you run a lot of docker containers (`--name`). The container will be automatically removed after the *Komunumo* backend server was shut down (`--rm`).

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

## API Documentation

### Authorization

*Komunumo* uses a passwordless login system. An email address has to be specified where a onetime login code will be send to, which is valid for five minutes. The user must authorize himself with his email and the generated onetime login code to get a JSON web token (JWT), which is valid for eight hours. The JWT has to be send with every request which needs authentication.

#### Request a onetime login code

Request: `curl -X GET ${baseURL}/api/authorization?email=${email}`

Example: `curl -X GET https://mydomain.com/api/authorization?email=foo.bar@mydomain.com`

The onetime login code will be send by email and is valid for five minutes.

| Response | Description |
| --- | ---|
| 200 OK | The ontime login code was sent successfully. |
| 400 BAD REQUEST | The request itself was not valid. Maybe the email address was missing? |
| 404 NOT FOUND | There is no user with the sprecified email address. |

#### Authorize using onetime login code

Request: `curl -X POST -H 'Content-Type: application/json; charset=utf-8' -d '{"email":"${email}","code":"${code}"}' ${baseURL}/api/authorization`

Example: `curl -X POST -H 'Content-Type: application/json; charset=utf-8' -d '{"email":"foo.bar@mydomain.com","code":"5IJWX"}' https://mydomain.com/api/authorization`

To authorize successfully an email address and a valid onetime login code is needed. After successful authorization the response contains the JSON web token in the `Authorization` header.

| Response | Description |
| --- | ---|
| 201 CREATED | The JSON web token was created. Take a look at the `Authorization` header. |
| 401 UNAUTHORIZED | The user could not be authorized using the specified credentials. |

### Event

#### Create a new event

Request: `curl -X POST -H 'Content-Type: application/json; charset=utf-8' -d '{"title":"${title}","subtitle":"${subtitle}","speaker":"${speaker}","dateTime":{"date":{"year":${year},"month":${month},"day":${day}},"time":{"hour":${hour},"minute":${minute},"second":${second},"nano":${nano}}},"location":"${location}","description":"${description}","tags":["${tag}"],"status":"${status}"}' ${baseURL}/api/events`

Example: `curl -X POST -H 'Content-Type: application/json; charset=utf-8' -d '{"title":"This is a test event","subtitle":"Foobar","speaker":"Me","dateTime":{"date":{"year":2017,"month":9,"day":15},"time":{"hour":18,"minute":15,"second":0,"nano":0}},"location":"There","description":"This is a test","tags":["test","foobar"],"status":"draft"}' https://mydomain.com/api/events`

An authorized user with the admin role can create a new event. After a successful event creation, the response contains a `Location` header with a direct link to the created event.

| Response | Description |
| --- | ---|
| 201 CREATED | The event was created. Take a look at the `Location` header. |
| 400 BAD REQUEST | The request itself was not valid. Maybe missing properties or wrong formatting? |
| 401 UNAUTHORIZED | The user has to authorize before he can create an event. |
| 403 FORBIDDEN | The user has not the correct role to create an event. |

## Throughput

[![Throughput Graph](https://graphs.waffle.io/komunumo/komunumo-backend/throughput.svg)](https://waffle.io/komunumo/komunumo-backend/metrics/throughput)

