@echo off
rem arguments remark: datapath myName myServer allServers
java -classpath .;fastjson-1.2.70.jar;SPaxos.jar test.ServerAllTest D:/paxostest/data M2 127.0.0.1:3001 127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002

pause