DO
$do$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_catalog.pg_database
      WHERE datname = 'account'
   ) THEN
      CREATE DATABASE account;
   END IF;
END
$do$;