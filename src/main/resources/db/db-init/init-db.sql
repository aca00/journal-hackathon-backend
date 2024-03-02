DROP TABLE IF EXISTS additional_display_image;
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS favourites;
DROP TABLE IF EXISTS product_review;
DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS temporary_credential_holder;
DROP TABLE IF EXISTS unexpired_revoked_token;
DROP TABLE IF EXISTS user_verification_code;
DROP TABLE IF EXISTS variant_property;
DROP TABLE IF EXISTS variant;
DROP TABLE IF EXISTS ordered_items;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS item_status;
DROP TABLE IF EXISTS item_property;
DROP TABLE IF EXISTS _order;
DROP TABLE IF EXISTS _user;
DROP TABLE IF EXISTS brand;
DROP TABLE IF EXISTS manufacturer;
DROP TABLE IF EXISTS category;

CREATE TABLE  public.item_status
(
    status_id integer NOT NULL,
    status_info character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT item_status_pkey PRIMARY KEY (status_id),
    CONSTRAINT uk_1bhybjhyj563balqwg94ngao0 UNIQUE (status_info)
);

CREATE TABLE  public._user
(
    user_id bigint NOT NULL,
    date_of_birth timestamp(6) without time zone,
    email character varying(255) COLLATE pg_catalog."default",
    full_name character varying(255) COLLATE pg_catalog."default",
    gender character varying(255) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default",
    user_role character varying(255) COLLATE pg_catalog."default",
    is_enabled boolean NOT NULL,
    is_verified boolean NOT NULL,
    phone_number character varying(255) COLLATE pg_catalog."default",
    display_image_url character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT _user_pkey PRIMARY KEY (user_id),
    CONSTRAINT uk_k11y3pdtsrjgy8w9b6q4bjwrx UNIQUE (email),
    CONSTRAINT _user_gender_check CHECK (gender::text = ANY (ARRAY['MALE'::character varying::text, 'FEMALE'::character varying::text, 'OTHER'::character varying::text]))
);








CREATE TABLE  public.temporary_credential_holder
(
    user_id bigint NOT NULL,
    password character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT temporary_credential_holder_pkey PRIMARY KEY (user_id)
);


CREATE TABLE  public.unexpired_revoked_token
(
    token_id bigint NOT NULL,
    token_type text COLLATE pg_catalog."default",
    token_text text COLLATE pg_catalog."default",
    issue_date timestamp(6) without time zone,
    expiry_date timestamp(6) without time zone,
    user_id bigint,
    CONSTRAINT token_pk PRIMARY KEY (token_id),
    CONSTRAINT token_to_user_fk FOREIGN KEY (user_id)
        REFERENCES public._user (user_id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);


CREATE TABLE  public.user_verification_code
(
    _id integer NOT NULL,
    verification_code text COLLATE pg_catalog."default",
    expiry_date timestamp(6) without time zone,
    user_id bigint NOT NULL,
    CONSTRAINT verification_pk PRIMARY KEY (_id)
);

