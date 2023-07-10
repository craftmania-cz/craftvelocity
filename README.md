# CraftVelocity

## Autologin

### Admin příkazy
Právo: `craftvelocity.autologin.admin`<br>
`//autologin` - Vypíše informace o Autologin Cache<br>
`//autologin verbose` - Vypíše informace o Autologin Cache s jednotlivými záznamy<br>
`//autologin clear-cache` - Vymaže Autologin Cache<br>
`//autologin enable <nick>` - Zapne autologin pro nick; pokud hráč je online, kickne ho to<br>
`//autologin force-enable <nick> <uuid>` - Zapne autologin pro nick s UUID; pokud hráč je online, kickne ho to (ignoruje MineTools checky)<br>
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

## KickGuard
KickGuard handluje to, na který server se hráč připojí po tom, co jde vyhozený z serveru (např. při pádu). 

### Poznámka
Seznam lobby serverů si získává z `autologin.servers.lobbies`.

### Nastavení
`kickGuard.whitelistedServers`
  - Seznam serverů, které KickGuard bude kontrolovat, aka. pokud zde bude napsaný server "survival" a hráč bude vyhozený ze serveru "survival", KickGuard se postará o přesměrování hráče na lobby.
  - Tato možnost je zde kvůli tomu, že nechceme řešit když se někdo vyhodí na whubu, lobby, atd.
  - **Nikdy zde nesmí být server, na který se hráč připojuje jako první, aka. whub, lobby, atd.**