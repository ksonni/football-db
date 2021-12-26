# Script to generate self-signed certs for development

set -xe

mkdir .certs || true

keytool -genkeypair -alias football-db -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore .certs/football-db.p12 -validity 3650
