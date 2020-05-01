rem Use mmj2 and set.mm when they're inside the Cygwin home directory
rem To set up, start up cygwin, and put set.mm in cygwin's ~/set.mm/ :
rem  cd; git clone https://github.com/metamath/set.mm.git # DB in ~/set.mm/
rem Then put mmj2 in cygwin's ~/mmj2 . To get the "master" version, use:
rem  cd; git clone https://github.com/digama0/mmj2.git    # mmj2 in ~/mmj2
rem Then use Windows "cmd" to run this command (e.g., with a shortcut!)
rem
java -Xms128M -Xmx1280M -jar mmj2.jar RunParms.txt Y "" c:\cygwin64\home\%USERNAME%\set.mm ""
