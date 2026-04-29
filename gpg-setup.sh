GPG_KEY=$1

# ---

mvn clean install --file tesseract-parent/pom.xml
mvn clean install --file tesseract-starter-jps/pom.xml
mvn clean install --file tesseract-starter-zitadel/pom.xml

# ---

gpg -ab tesseract-parent/tesseract-core/target/tesseract-core-v1.0.3.jar
gpg -ab tesseract-parent/tesseract-core/target/tesseract-core-v1.0.3-sources.jar
gpg -ab tesseract-parent/tesseract-core/target/tesseract-core-v1.0.3-javadoc.jar
gpg -ab tesseract-parent/tesseract-core/target/tesseract-core-v1.0.3.pom

gpg -ab tesseract-parent/tesseract-sync/target/tesseract-sync-v1.0.3.jar
gpg -ab tesseract-parent/tesseract-sync/target/tesseract-sync-v1.0.3-sources.jar
gpg -ab tesseract-parent/tesseract-sync/target/tesseract-sync-v1.0.3-javadoc.jar
gpg -ab tesseract-parent/tesseract-sync/target/tesseract-sync-v1.0.3.pom

gpg -ab tesseract-parent/tesseract-async/target/tesseract-async-v1.0.3.jar
gpg -ab tesseract-parent/tesseract-async/target/tesseract-async-v1.0.3-sources.jar
gpg -ab tesseract-parent/tesseract-async/target/tesseract-async-v1.0.3-javadoc.jar
gpg -ab tesseract-parent/tesseract-async/target/tesseract-async-v1.0.3.pom

# ---

gpg -ab tesseract-starter-jps/target/tesseract-starter-jps-1.0.3.jar
gpg -ab tesseract-starter-jps/target/tesseract-starter-jps-1.0.3-sources.jar
gpg -ab tesseract-starter-jps/target/tesseract-starter-jps-1.0.3-javadoc.jar
gpg -ab tesseract-starter-jps/target/tesseract-starter-jps-1.0.3.pom

# ---

gpg -ab tesseract-starter-zitadel/target/tesseract-starter-zitadel-1.0.3.jar
gpg -ab tesseract-starter-zitadel/target/tesseract-starter-zitadel-1.0.3-sources.jar
gpg -ab tesseract-starter-zitadel/target/tesseract-starter-zitadel-1.0.3-javadoc.jar
gpg -ab tesseract-starter-zitadel/target/tesseract-starter-zitadel-1.0.3.pom

# ---

gpg --keyserver keyserver.ubuntu.com --send-keys $GPG_KEY