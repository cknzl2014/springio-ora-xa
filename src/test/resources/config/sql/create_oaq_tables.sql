connect sys/oracle as sysdba

-- -------------------------------------------------------
-- create user aq
-- -------------------------------------------------------

DROP USER aq CASCADE;

CREATE USER aq IDENTIFIED BY aq
  DEFAULT TABLESPACE users
  TEMPORARY TABLESPACE temp;

ALTER USER aq QUOTA UNLIMITED ON users;

GRANT aq_administrator_role TO aq;
GRANT aq_user_role          TO aq;
GRANT connect               TO aq;
GRANT create type           TO aq;
GRANT create sequence       TO aq;
GRANT execute ON DBMS_AQADM TO aq;
GRANT create procedure      TO aq;

grant select on pending_trans$ to aq; 
grant select on dba_2pc_pending to aq;
grant select on dba_pending_transactions to aq;
grant execute on dbms_xa to aq;

EXECUTE dbms_aqadm.grant_type_access('aq');

-- -------------------------------------------------------
-- create queue table and queue
-- -------------------------------------------------------

CONNECT aq/aq

SET SERVEROUTPUT ON

DECLARE
  -- -----
  PROCEDURE create_in_queue(nr IN INTEGER)
  IS
  BEGIN
    DBMS_AQADM.CREATE_QUEUE_TABLE (
        queue_table         => 'aq.SR3MSGIN' || nr
      , queue_payload_type  => 'SYS.AQ$_JMS_BYTES_MESSAGE'
      , SORT_LIST           => 'PRIORITY,ENQ_TIME'
      , MULTIPLE_CONSUMERS  => FALSE
      , MESSAGE_GROUPING    => 0
      , COMMENT             => 'IN Queue for messages'
      , SECURE              => FALSE
    );

    DBMS_AQADM.CREATE_QUEUE (
        queue_name          => 'aq.SRUMSGIN' || nr
      , queue_table         => 'aq.SR3MSGIN' || nr
      , queue_type          => DBMS_AQADM.NORMAL_QUEUE
      , max_retries         => 5
      , retry_delay         => 0
      , retention_time      => 0
      , comment             => 'IN Queue for messages'
    );

    DBMS_AQADM.START_QUEUE('aq.SRUMSGIN' || nr
       ,ENQUEUE => TRUE 
       ,DEQUEUE => TRUE 
    );
  END create_in_queue;

BEGIN

  create_in_queue(1);

END;
/
