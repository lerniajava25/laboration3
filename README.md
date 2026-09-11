# laboration3
Laborationsuppgift 3 går ut på att jobba med enhets tester (Unit testing) samt Java 8 streams och funktionell programmering i en Spring Boot applikation.

## Hur Spring Boot underlättar utvecklingen
Spring Boot gör det mycket lättare att komma igång med ett Spring-baserat projekt, något som annars är ganska komplicerat. Detta eftersom att en stor del av konfigurationen redan är färdig och mycket grundläggande funktionalitet redan finns.

Spring Boot använder även en tydlig struktur, vilket gör det att underhålla och bygga på projekt. Det är väletablerat och man har däför tillgång till en mängd bibliotek och ramverk. Precis som Java finns det även mycket dokumentation och många lösningar på problem att hitta.

## Spring Boot vs Node.js
Node.js och Spring Boot har viss överlappande funktionalitet som att skapa REST-api:er och hantera databaser.

Om man jämför Spring Boot med Node.js skulle jag säga att den stora skillnaden är att Node är mycket lättare att komma igång med och få ihop enklare projekt med. Ännu mer så eftersom JavaScript lättare att komma igång med än Java.

Spring Boot verkar dock ha mycket mer funktionalitet och skalbarhet, och är därför mer lämpligt på ett störe projekt med mer komplexitet.

En annan viktig skillnad är att Java är statisk typat, medan JavaScript är dynamisk, därför upptäcks många typfel i Java redan när projektet kompileras. I Node.js upptäcks vissa fel först när programmet körs, om man inte använder TypeScript.

Fler skillnader:
Java/Spring Boot: Maven projekt: pom.xml - Node.js: npm, package.json.
Concurrency - Java-trådar/virtuella trådar VS node.js/express Event loop + asynkron I/O
REST API - Spring Boot: Controllers, annotations - Node.js: Routes/callbacks

I Spring Boot jämnfört med Node.js så ingår det mer redan från början i Spring Boot. Jag har exempelvis gjort en realtids chatt med node.js/express/socket.io etc.. i backend. Man behöver ladda ned många små separata paket för att bygga ihop sin lösning. Medans med Spring Boot använder man oftast Maven projekt med starters som samlar flera dependencys.
Det gör att strukturen styrs mera av Spring Boot jämfört med Node.js/express har större frihet.
