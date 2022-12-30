# CraftVelocity

## Autologin

### Admin příkazy
Právo: `craftvelocity.autologin.admin`<br>
`//autologin` - Vypíše informace o Autologin Cache<br>
`//autologin verbose` - Vypíše informace o Autologin Cache s jednotlivými záznamy<br>
`//autologin clear-cache` - Vymaže Autologin Cache<br>
`//autologin enable <nick>` - Zapne autologin pro nick; pokud hráč je online, kickne ho to<br>
`//autologin disable <nick>` - Vypne autologin pro nick; pokud hráč je online, kickne ho to<br>
`//autologin check <nick>` - Řekne zda nick má zapnutý či vypnutý autologin<br>

### Hráčské příkazy
Právo: N/A<br>
`/autologin` - Řekne hráči zda má zapnutý či vypnutý autologin<br>
`/autologin on` - Zapne hráči autologin; automaticky ho to kickne<br>
`/autologin ignore` - Vypne hráči zprávu o možném zapnutí autologinu<br>
Note: V zdrojovém kodu existuje kod pro `/autologin off`, ale je vykomentovaný.<br>

## Config
[Defaultní config](./-/blob/main/src/main/resources/config.toml)