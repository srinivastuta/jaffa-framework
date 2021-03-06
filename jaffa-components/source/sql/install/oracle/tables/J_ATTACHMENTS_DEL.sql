DECLARE
  v_count NUMBER default 0;

BEGIN
  DBMS_OUTPUT.enable(null);
  Dbms_Output.put_line('In create table script for J_ATTACHMENTS_DEL');

  SELECT  count(*)
    INTO  v_count
    FROM  USER_TABLES
   WHERE  TABLE_NAME = 'J_ATTACHMENTS_DEL'
  ;

  IF (v_count > 0) then
     Dbms_Output.put_line('Table already exists, therefore not creating table: J_ATTACHMENTS_DEL');
  ELSE 
     Dbms_Output.put_line('Table does not exist, therefore creating table: J_ATTACHMENTS_DEL');
     EXECUTE IMMEDIATE '
        CREATE TABLE "J_ATTACHMENTS_DEL"(
           "ATTACHMENT_ID"          VARCHAR2(80) NOT NULL,
           "SERIALIZED_KEY"          VARCHAR2(500) NOT NULL,
           "VERSION_NUMBER"          NUMBER(10),
           "DELETION_CREATED_ON"          DATE
        )
     ';
  END IF;
END;
/
