FROM postman/newman_alpine33
MAINTAINER https://github.com/adorsys/xs2a

RUN apk add --update nodejs

ENV NEWMAN_VERSION 4.5.0

RUN npm init
RUN npm install newman@${NEWMAN_VERSION};
RUN npm install newman newman-reporter-html

WORKDIR /etc/newman

ENTRYPOINT ["newman"]
