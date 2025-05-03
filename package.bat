@echo off
jpackage ^
  --name KenKenRangers ^
  --app-version 1.0 ^
  --type exe ^
  --input target ^
  --module algorangers.kenkenrangers/algorangers.kenkenrangers.Launcher ^
  --runtime-image target/app ^
  --icon kenken.png ^
  --dest installer
