FROM postman/newman:ubuntu
MAINTAINER https://github.com/adorsys/xs2a

RUN apk add --update nodejs

ENV NEWMAN_VERSION 4.5.0

RUN npm init
RUN npm install -g newman@${NEWMAN_VERSION};
RUN npm install -g newman-reporter-html

WORKDIR /etc/newman

ENTRYPOINT ["newman"]
