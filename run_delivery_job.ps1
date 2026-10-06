$CURRENT_DATE = GET-DATE -Format "yyyy/dd/MM"
mvn clean package "-Dmaven.test.skip=true";

$JAR_PATH = Resolve-Path ./target/linkedin-batch-0.0.1-SNAPSHOT.jar
java -jar $JAR_PATH "item=lallala" "run.date=$CURRENT_DATE" --spring.profiles.active=local;
pause;