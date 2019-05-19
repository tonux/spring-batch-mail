
# Mail batch application

Un exemple d'application Spring Batch pour envoyer des courriers électroniques à plusieurs destinataires. Il alimente les adresses électroniques à partir d'un fichier csv et envoie un courrier électronique à chaque destinataire, y compris une pièce jointe. Un exemple de fichier csv et une pièce jointe sont fournis à titre d'exemple. Le code utilise le projet [Spring Batch] 

## How to run the application

prérequis : Java 8
Lancement:

    mvn clean package
    java -jar target/mail-batch-0.0.1-SNAPSHOT.jar \
      --spring.mail.username=<user> \
      --spring.mail.password=<pass>
    
## Takk Diok