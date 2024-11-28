# CraftVelocity
Tento plugin slouží ke kontrolování hráčů a úpravy serveru na proxy - Velocity.

## Config
[Defaultní config](./-/blob/main/src/main/resources/config.toml)

## Autologin
Autologin slouží k automatickému přihlašování hráčů s zakoupeným Minecraftem.

### Admin příkazy
| Příkaz                                   | Právo | Popis |
|------------------------------------------|---| --- |
| `//autologin`                            | `craftvelocity.autologin.admin` | Vypíše informace o Autologin Cache |
| `//autologin verbose`                    | `craftvelocity.autologin.admin` | Vypíše informace o Autologin Cache s jednotlivými záznamy |
| `//autologin clear-cache`                | `craftvelocity.autologin.admin` | Vymaže Autologin Cache |
| `//autologin enable <nick>`              | `craftvelocity.autologin.admin` | Zapne autologin pro nick; pokud hráč je online, kickne ho to |
| `//autologin force-enable <nick> <uuid>` | `craftvelocity.autologin.admin` | Zapne autologin pro nick s UUID; pokud hráč je online, kickne ho to (ignoruje MineTools checky) |
| `//autologin disable <nick>`             | `craftvelocity.autologin.admin` | Vypne autologin pro nick; pokud hráč je online, kickne ho to |
| `//autologin check <nick>`               | `craftvelocity.autologin.admin` | Řekne zda nick má zapnutý či vypnutý autologin |

### Hráčské příkazy
| Příkaz | Právo | Popis |
| --- |---| --- |
| `/autologin` | - | Řekne hráči zda má zapnutý či vypnutý autologin |
| `/autologin on` | - | Zapne hráči autologin; automaticky ho to kickne |
| `/autologin ignore` | - | Vypne hráči zprávu o možném zapnutí autologinu |

> [!NOTE]
> V zdrojovém kodu existuje kod pro `/autologin off`, ale je vykomentovaný.<br>

## KickGuard
KickGuard handluje to, na který server se hráč připojí po tom, co jde vyhozený z serveru (např. při pádu). 

> [!NOTE]
> Seznam lobby serverů si získává z `autologin.servers.lobbies`.

### Nastavení
`kickGuard.whitelistedServers`
  - Seznam serverů, které KickGuard bude kontrolovat, aka. pokud zde bude napsaný server "survival" a hráč bude vyhozený ze serveru "survival", KickGuard se postará o přesměrování hráče na lobby.
  - Tato možnost je zde kvůli tomu, že nechceme řešit když se někdo vyhodí na whubu, lobby, atd.
  - **Nikdy zde nesmí být server, na který se hráč připojuje jako první, aka. whub, lobby, atd.**