@echo off
rem arguments remark: datapath totalNodes totalProposers startProposerServer
java -classpath .;fastjson-1.2.70.jar;SPaxos.jar test.ClientAllTest D:/paxostest/data 3 6 127.0.0.1:3000

pause