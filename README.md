# CelestialMain
Main/version independent section for Celestial. This is where most of the logic behind all the Celestial versions is kept.

## Contributing

This repo does not build on its own. It it intended to be used as a gradle subproject in one of the many celestial repos.
To contribute, please do a recursive clone of one of the following repos:

- [https://github.com/fishcute/Celestial1.21](https://github.com/fishcute/Celestial1.21)
- [https://github.com/fishcute/Celestial1.20](https://github.com/fishcute/Celestial1.20)
- [https://github.com/fishcute/Celestial1.19.4](https://github.com/fishcute/Celestial1.19.4)
- [https://github.com/fishcute/Celestial1.18.2](https://github.com/fishcute/Celestial1.18.2)
- [https://github.com/fishcute/Celestial1.16.5](https://github.com/fishcute/Celestial1.16.5)

Inside of these should be a working CelestialMain repo as a git submodule. Any changes inside of this repo can be edited and any changes will be commited and pushed to this repo.

For testing please execute the gradle runClient task for both forge and fabric. This helps make sure that your changes don't break anything. Ideally testing the changes on other versions 
of celestial would also be helpful.
