CREATE TABLE t_radarcovid_casecodes
(
    pk_casecode_id  serial                   NOT NULL,
    case_number     character varying(12)    NOT NULL,
    ccaa_id         character(2)             NOT NULL,
    redeemed        boolean                  NOT NULL default false,
    created_at      timestamp with time zone NOT NULL DEFAULT now(),
    redeemed_at     timestamp with time zone,
    start_time      timestamp with time zone,
    end_time        timestamp with time zone,
    venue           char varying(255),
    CONSTRAINT pk_t_radarcovid_casecode PRIMARY KEY (pk_casecode_id)
);
