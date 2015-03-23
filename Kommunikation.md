

# Introduction #

Alle [Komponenten](Komponenten.md) kommunizieren über tcp/tls.
Die Kommunikation Agent < - > Server < - > Konsolen/GUI erfolgt über JSON.
Die Kommunikation Agent Repository erfolgt über HTTPs.

# JSON Protokoll #

Generell gillt dieser Rumpf des JSON:
```js

{
type: PACKET_TYPE //Integer
}
```
Dieser wird um die spezifischen properties erweitert.

Der PACKET\_TYPE muss eindeutig über den Kommunikationsscope sein.

## Agent - Server ##

### Agent Hello ###
Sender: Agent

Erwartete Antwort: Agent Hello Response
```js

{
agent_id: AGENT_ID, //String
agent_encoding: ENCODING //String (optional)
agent_version: AGENT_VERSION, //String
agent_plattform: AGENT_PLATTFORM, //String
}
```
| **Feld** | **Datentyp** | **Wert/Beschreibung** |
|:---------|:-------------|:----------------------|
| PACKET\_TYPE | Integer | 1 |
| AGENT\_ID | String | fqdn, falls der schon beim Server vergeben ist, wird ein bestehender Eintrag überschrieben |
| AGENT\_VERSION | String | Versionsstring des Agenten, egal was kommt - nur für Admins |
| AGENT\_PLATTFORM | String | {linux, unix, windows} |
| ENCODING | String | Optional, setzt das Antwortformat {JSON, XML} |


### Agent Hello Response ###
Sender: Server

Erwartete Antwort: keine
```js

{
successfull: REGISTRATION,
return_code: CODE,
message: MESSAGE
}
```
| **Feld** | **Datentyp** | **Wert/Beschreibung** |
|:---------|:-------------|:----------------------|
| PACKET\_TYPE | Integer | 2 |
| REGISTRATION | Boolean | {true, false} true = erfolgreich registriert, false = nicht erfolgreich - entsprechende Fehlermeldung in der Message - return gefuellt |
| CODE | Integer | Fehlercode oder 0 |
| MESSAGE | String | Fehlernachricht, oder "hallo" |

| **Fehlercode** | **Beschreibung** |
|:---------------|:-----------------|
| 0 | Registrierung erfolgreich |
| 1 | Erneute Registierung erfolgreich |
| -1 | Registrierung fehlgeschlagen, Agent ist schon an diesem Server registriert und online |
| -2 | Registrierung fehlgeschlagen, Agent ist schon bei einem anderen Server registriert und online |

### Agent Heartbeat ###
Sender: Agent, Server?

Erwartete Antwort: keine
```js

{
local_time: TIME
}
```
| **Feld** | **Datentyp** | **Wert/Beschreibung** |
|:---------|:-------------|:----------------------|
| PACKET\_TYPE | Integer | 0 |
| TIME | timestamp | Lokale Zeit des Agentensystems/Sendezeitpunkt des Heartbeats |


### Automation Control ###
Sender: Server

Erwartete Antwort: Automation Status
```js

{
repository_url: URL,
automation_desctiptor: DESCRIPTOR,
automation_id: AUTOMATION_ID,
automation_action: ACTION,
automation_parameter: {
timeout: TIMEOUT,
arguments: ARGUMENTS,
user: USER,
group: GROUP
}
}
```
| **Feld** | **Datentyp** | **Wert/Beschreibung** |
|:---------|:-------------|:----------------------|
| PACKET\_TYPE | Integer | 13 |
| AUTOMATION\_ID | Integer | s.o. |
| ACTION | String | {start, stop, pause, resume, clear} start: starten der zuvor preparierten Automation, stop: stoppen ein gerade Ausgeführten Automation, pause: pausieren der Automation, resume: wiederaufnehmen der Automation, clear: löschen einer noch nicht gestarteten Automation |
| TIMEOUT | Integer | Timeout bis zum Abbruch der Automation in Sekunden. Überschreibt ggf. das default aus dem descriptor |
| ARGUMENTS | String | Agrumente als String, werden der Automation beim Starten übergeben |
| USER | String | User, unter welchem die Automation ausgeführt werden soll, überschreibt ggf. das default aus dem descriptor |
| GROUP | String | Gruppe, unter welcher die Automation ausgeführt werden soll, überschreibt ggf. das default aus dem desriptor |
| URL | String | URL zum Automations Repository |
| DESCRIOTOR | String | fq AutomationsDescriptor innerhalb des Repositories |


### Automation Status ###
Sender: Agent

Erwartete Antwort: keine
```js

{
automation_id: AUTOMATION_ID,
automation_status: STATUS,
automation_message: MESSAGE
}
```
| **Feld** | **Datentyp** | **Wert/Beschreibung** |
|:---------|:-------------|:----------------------|
| PACKET\_TYPE | Integer | 20 |
| STATUS | String | {unknown, preparing, prepared, stated, paused, resumed, finished, stopped, start-failed, prepare-failed, pause-failed, resume-failed, stopped-failed, talking} |
| MESSAGE | String | Message, bei talking der Konsolenoutput |
| AUTOMATION\_ID | Integer| s.o. |


## Controller - Server ##

### Automation Control ###
Sender: Controller

Erwartete Antwort: Automation Status (siehe Agent-Server:Automation Status)
```js

{
agent_id: [AGENT],
automation_id: [ID],
automation_action: ACTION,
automation_reason: REASON,
automation_descriptor: DESCRIPTOR,
automation_parameter: PARAMETER
}
```
| **Feld** | **Datentyp** | **Wert/Beschreibung** |
|:---------|:-------------|:----------------------|
| AGENT | String | Liste von Agent Ids |
| PACKET\_TYPE | Integer | 13 |
| ACTION | String | {start, stop, pause, resume, listen} |
| ID | Integer | Liste von Automationsis|
| REASON | String | Begründung der Aktion, fürs logging |
| DESCRIPTOR | String | s.o. |
| PARAMETER | String | s.o. |


### Automation Query ###
Sender: Controller

Erwartete Antwort: Automation Query
```js

{
automation_query: QUERY
automation_query_result: RESULT
automation_query_parameter : {
...
}
}
```
| **Feld** | **Datentyp** | **Wert/Beschreibung** |
|:---------|:-------------|:----------------------|
| PACKET\_TYPE | Integer | 30 |
| QUERY | String | {agents, servers, automations} |
| RESULT | Array | Objekte der Instanzen |