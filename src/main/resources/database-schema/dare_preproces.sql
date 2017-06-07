BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE DARE_PREPROCES';
  EXCEPTION
  WHEN OTHERS THEN NULL;
END;

CREATE TABLE DARE_PREPROCES (
  ID NUMBER(10),
  STATE NUMBER(10) DEFAULT 0 NOT NULL,
  KBOBJID VARCHAR2(255) NOT NULL,
  TS_CREATE TIMESTAMP(6) NOT NULL,
  TS_PROCESSED TIMESTAMP(6) DEFAULT NULL,
  FINGERPRINT VARCHAR2(512) DEFAULT NULL,
  REPOSITORY_ID NUMBER(10) NOT NULL,
  OAI_ID VARCHAR2(255) NOT NULL,
  OAI_DATESTAMP VARCHAR2(255) NOT NULL,
  PRIMARY KEY (ID)
)

CREATE INDEX DARE_PREPROCES_INDEX ON DARE_PREPROCES (OAI_ID, OAI_DATESTAMP, STATE, REPOSITORY_ID)

BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_DARE_PREPROCES';
  EXCEPTION
  WHEN OTHERS THEN NULL;
END;

CREATE SEQUENCE SEQ_DARE_PREPROCES START WITH 1 INCREMENT BY 1

CREATE OR REPLACE TRIGGER SEQ_DARE_PREPROCES_TR
BEFORE INSERT ON DARE_PREPROCES FOR EACH ROW
WHEN (NEW.id IS NULL)
  BEGIN
    SELECT SEQ_DARE_PREPROCES.NEXTVAL INTO :NEW.id FROM DUAL;
  END;
