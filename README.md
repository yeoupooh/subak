# Subak [![Build Status](https://travis-ci.org/yeoupooh/subak.svg?branch=master)](https://travis-ci.org/yeoupooh/subak)
Subak(수박, Watermelon in Korean) is a for-fun-personal-purpose music service written in many languages(hopefully).


# DISCLAIMER
We don't share any copyright protected files with anyone via this.
We don't guarantee any legal issues with running this service by you. Totally at your risk.


# Build and Run Node Server
It requires nodejs environment. Tested on NodeJS 5.x.

```
cd server/node/server
npm install
npm start
```

Now you can use Subak on http://localhost:8081/.

# Build and Run Groovy Client
It requires groovy, gradle environment. Tested on Groovy 2.4.x.

## Edit subak.config.json
You can start with copying from subak.config.sample.json under client/groovy/src/main/resources.
```json
{
  "url": "http://localhost:8081"
}
```

## Build and Run
```
cd client/groovy
gradle start
```

# Licenses
* All Source Codes followed by GPLv2
* Watermelon icon: http://www.freefavicon.com/freefavicons/food/iconinfo/watermelon-152-190680.html


# Build your own engine
Hack your engines referencing simple engines under sever/node/server/engines.


# Community
Check out https://funsubak.slack.com/
