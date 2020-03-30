# XS2ASandbox
[![Build Status](https://api.travis-ci.com/adorsys/XS2A-Sandbox.svg?branch=master)](https://travis-ci.com/adorsys/XS2A-Sandbox)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=adorsys_XS2A-Sandbox&metric=alert_status)](https://sonarcloud.io/dashboard?id=adorsys_XS2A-Sandbox)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=adorsys_XS2A-Sandbox&metric=coverage)](https://sonarcloud.io/dashboard?id=adorsys_XS2A-Sandbox)


## What is it

XS2ASandbox is a dynamic sandbox environment: you can create users, payments as well as consents, and play with data for testing purposes. With the dynamic sandbox [TPP UI](https://github.com/adorsys/XS2A-Sandbox/tree/master/tpp-ui), you can access banking APIs directly, get TPP certificates and manage testing accounts.

[Developer Portal](https://github.com/adorsys/XS2A-Sandbox/tree/master/developer-portal-ui) contains testing instructions and all of the necessary documentation. All PSD2 services can be installed and run with one simple command. 

All four SCA approaches are supported: REDIRECT, OAUTH, EMBEDDED, DECOUPLED. Two of them (REDIRECT, EMBEDDED) are directly testable on Developer portal. For the Redirect SCA Approach an [Online Banking UI](https://github.com/adorsys/XS2A-Sandbox/tree/master/oba-ui) is used for authorisation.

![XS2ASandbox structure](https://github.com/adorsys/XS2A-Sandbox/tree/master/docs/XS2ASandbox.png)


## Project documentation

* [Release notes](https://github.com/adorsys/XS2A-Sandbox/tree/master/docs/release_notes) contain information about changes included into releases.
* [User Guide](https://github.com/adorsys/XS2A-Sandbox/tree/master/docs/user-guide.md) describes how to configure XS2ASandbox.

## How to try it

* [Running XS2ASandbox instructions](https://github.com/adorsys/XS2A-Sandbox/tree/master/docs/running-xs2asandbox.md) will help you getting you a copy of the project up and running on your local machine.

## Development and contributing

Any person are free to join us by implementing some parts of code or fixing some bugs and making a merge requests for them.

[Contribution Guidelines](https://github.com/adorsys/XS2A-Sandbox/tree/master/docs/Contribution-Guidelines.md) describe internal development process and how to contribute to XS2ASandbox.

## Contact

If you still have any questions, please write us an [e-mail](psd2-sandbox@adorsys.de).

For commercial support please contact [adorsys Team](https://adorsys-platform.de/solutions/).

## License

This project is licensed under the Apache License version 2.0 - see the [LICENSE](https://github.com/adorsys/XS2A-Sandbox/blob/master/LICENSE) file for details.