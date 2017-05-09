DECLARE
  v_count NUMBER;

BEGIN

  SELECT Count(*) INTO v_count FROM USER_INDEXES WHERE INDEX_NAME = 'JMS_MESSAGES_DESTINATION';
  IF (v_count = 0) THEN
     EXECUTE IMMEDIATE 'CREATE INDEX JMS_MESSAGES_DESTINATION ON JMS_MESSAGES  (DESTINATION)'  ;
  END IF;

END;
/