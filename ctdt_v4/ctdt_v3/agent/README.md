# CTDT Agent — Contributor File Watcher

Runs silently on the contributor's Windows PC.
Detects every file save and sends a log to the CTDT backend automatically.

## Requirements
- Java 17 or higher (same as backend)

## Setup (run once)

1. Copy CtdtAgent.java to the contributor's PC
2. Open Command Prompt and run:

```
javac CtdtAgent.java
java CtdtAgent
```

3. On first run it will ask:
   - Server URL  → e.g. http://192.168.1.5:8080  (host's IP)
   - Project ID  → get this from the Projects page
   - Username    → set by host when adding the contributor
   - Password    → set by host when adding the contributor
   - Folder      → the project folder to watch

4. After successful login, the agent runs silently.
   Every file save is automatically logged.
   Settings are saved — next time just run `java CtdtAgent` with no setup.

## Auto-start with Windows

To make it start automatically when Windows boots:

1. Press Win+R → type `shell:startup` → Enter
2. Create a file called `ctdt_agent.bat` with:

```bat
@echo off
cd C:\path\to\agent\folder
start /min java CtdtAgent
```

3. Save it in the Startup folder.
   Now the agent starts silently every time Windows boots.

## What gets logged
- Every file save (MODIFY event)
- File name and line count are sent automatically
- Weight is calculated by the backend based on file extension
- Hidden files, temp files (.tmp, .swp, ~) are ignored
