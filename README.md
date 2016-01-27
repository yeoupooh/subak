[![Build Status](https://travis-ci.org/yeoupooh/subak.svg?branch=master)](https://travis-ci.org/yeoupooh/subak)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/2c7371984770499680a184a97058ccbd)](https://www.codacy.com/app/thomas-min-v1/subak)
[![Codeship Status for yeoupooh/subak](https://codeship.com/projects/74b104a0-9e42-0133-935c-2a763bc2b06b/status?branch=master)](https://codeship.com/projects/127718)
[![Code Climate](https://codeclimate.com/github/yeoupooh/subak/badges/gpa.svg)](https://codeclimate.com/github/yeoupooh/subak)
[![implementation-nodejs-brightgreen](https://img.shields.io/badge/server-nodejs-brightgreen.svg)](https://nodejs.org/en/)
[![implementation-groovy-blue](https://img.shields.io/badge/client-groovy-blue.svg)](http://www.groovy-lang.org/)
[![license-GPLv2](https://img.shields.io/badge/license-GPLv2-blue.svg)](http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html)

# Subak
Subak(수박, Watermelon in Korean) is a for-fun-personal-purpose music service written in many languages(hopefully).


# DISCLAIMER
We don't share any copyright protected files with anyone via this.
We don't guarantee any legal issues with running this service by you. Totally at your risk.


# Build, Test and Run Node Server
It requires nodejs, mocha environment. Tested on NodeJS 5.x.

```
cd server/node
npm install
npm test
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
./gradlew run
```

# Licenses
* All Source Codes followed by GPLv2
* Watermelon icon: http://www.freefavicon.com/freefavicons/food/iconinfo/watermelon-152-190680.html


# Build your own engine
Hack your engines referencing simple engines under sever/node/server/engines.


# Community
Check out https://funsubak.slack.com/
