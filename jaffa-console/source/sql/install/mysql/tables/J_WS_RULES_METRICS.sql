CREATE TABLE J_WS_RULES_METRICS (
        ID          VARCHAR(80) NOT NULL,
        WEB_SERVICE_NAME          TEXT,
        RESULT_GRAPH_RULE          TEXT,
        CREATED_ON          DATETIME,
    CONSTRAINT J_WS_RULES_METRICSP1 PRIMARY KEY(ID)
) TYPE=InnoDB