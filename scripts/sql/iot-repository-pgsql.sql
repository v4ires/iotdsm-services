--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.9
-- Dumped by pg_dump version 9.5.9

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 1 (class 3079 OID 12395)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2193 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 182 (class 1259 OID 33292)
-- Name: tb_sensor; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE tb_sensor (
    id bigint NOT NULL,
    create_time timestamp without time zone,
    description character varying(255),
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    name character varying(255),
    sensor_source_id bigint NOT NULL
);


ALTER TABLE tb_sensor OWNER TO postgres;

--
-- TOC entry 183 (class 1259 OID 33301)
-- Name: tb_sensor_has_sensor_measure_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE tb_sensor_has_sensor_measure_type (
    sensor_id bigint NOT NULL,
    sensor_measure_type_id bigint NOT NULL
);


ALTER TABLE tb_sensor_has_sensor_measure_type OWNER TO postgres;

--
-- TOC entry 181 (class 1259 OID 33290)
-- Name: tb_sensor_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE tb_sensor_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tb_sensor_id_seq OWNER TO postgres;

--
-- TOC entry 2194 (class 0 OID 0)
-- Dependencies: 181
-- Name: tb_sensor_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tb_sensor_id_seq OWNED BY tb_sensor.id;


--
-- TOC entry 185 (class 1259 OID 33308)
-- Name: tb_sensor_measure; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE tb_sensor_measure (
    id bigint NOT NULL,
    create_time timestamp without time zone,
    value character varying(255),
    sensor_id bigint NOT NULL,
    sensor_measure_type_id bigint NOT NULL
);


ALTER TABLE tb_sensor_measure OWNER TO postgres;

--
-- TOC entry 184 (class 1259 OID 33306)
-- Name: tb_sensor_measure_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE tb_sensor_measure_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tb_sensor_measure_id_seq OWNER TO postgres;

--
-- TOC entry 2195 (class 0 OID 0)
-- Dependencies: 184
-- Name: tb_sensor_measure_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tb_sensor_measure_id_seq OWNED BY tb_sensor_measure.id;


--
-- TOC entry 187 (class 1259 OID 33316)
-- Name: tb_sensor_measure_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE tb_sensor_measure_type (
    id bigint NOT NULL,
    create_time timestamp without time zone,
    name character varying(255),
    unit character varying(255)
);


ALTER TABLE tb_sensor_measure_type OWNER TO postgres;

--
-- TOC entry 186 (class 1259 OID 33314)
-- Name: tb_sensor_measure_type_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE tb_sensor_measure_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tb_sensor_measure_type_id_seq OWNER TO postgres;

--
-- TOC entry 2196 (class 0 OID 0)
-- Dependencies: 186
-- Name: tb_sensor_measure_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tb_sensor_measure_type_id_seq OWNED BY tb_sensor_measure_type.id;


--
-- TOC entry 189 (class 1259 OID 33327)
-- Name: tb_sensor_source; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE tb_sensor_source (
    id bigint NOT NULL,
    create_time timestamp without time zone,
    description character varying(255),
    name character varying(255)
);


ALTER TABLE tb_sensor_source OWNER TO postgres;

--
-- TOC entry 188 (class 1259 OID 33325)
-- Name: tb_sensor_source_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE tb_sensor_source_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tb_sensor_source_id_seq OWNER TO postgres;

--
-- TOC entry 2197 (class 0 OID 0)
-- Dependencies: 188
-- Name: tb_sensor_source_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tb_sensor_source_id_seq OWNED BY tb_sensor_source.id;


--
-- TOC entry 2044 (class 2604 OID 33295)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tb_sensor ALTER COLUMN id SET DEFAULT nextval('tb_sensor_id_seq'::regclass);


--
-- TOC entry 2045 (class 2604 OID 33311)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tb_sensor_measure ALTER COLUMN id SET DEFAULT nextval('tb_sensor_measure_id_seq'::regclass);


--
-- TOC entry 2046 (class 2604 OID 33319)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tb_sensor_measure_type ALTER COLUMN id SET DEFAULT nextval('tb_sensor_measure_type_id_seq'::regclass);


--
-- TOC entry 2047 (class 2604 OID 33330)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tb_sensor_source ALTER COLUMN id SET DEFAULT nextval('tb_sensor_source_id_seq'::regclass);


--
-- TOC entry 2178 (class 0 OID 33292)
-- Dependencies: 182
-- Data for Name: tb_sensor; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY tb_sensor (id, create_time, description, latitude, longitude, name, sensor_source_id) FROM stdin;
\.


--
-- TOC entry 2179 (class 0 OID 33301)
-- Dependencies: 183
-- Data for Name: tb_sensor_has_sensor_measure_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY tb_sensor_has_sensor_measure_type (sensor_id, sensor_measure_type_id) FROM stdin;
\.


--
-- TOC entry 2198 (class 0 OID 0)
-- Dependencies: 181
-- Name: tb_sensor_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('tb_sensor_id_seq', 1, false);


--
-- TOC entry 2181 (class 0 OID 33308)
-- Dependencies: 185
-- Data for Name: tb_sensor_measure; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY tb_sensor_measure (id, create_time, value, sensor_id, sensor_measure_type_id) FROM stdin;
\.


--
-- TOC entry 2199 (class 0 OID 0)
-- Dependencies: 184
-- Name: tb_sensor_measure_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('tb_sensor_measure_id_seq', 1, false);


--
-- TOC entry 2183 (class 0 OID 33316)
-- Dependencies: 187
-- Data for Name: tb_sensor_measure_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY tb_sensor_measure_type (id, create_time, name, unit) FROM stdin;
\.


--
-- TOC entry 2200 (class 0 OID 0)
-- Dependencies: 186
-- Name: tb_sensor_measure_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('tb_sensor_measure_type_id_seq', 1, false);


--
-- TOC entry 2185 (class 0 OID 33327)
-- Dependencies: 189
-- Data for Name: tb_sensor_source; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY tb_sensor_source (id, create_time, description, name) FROM stdin;
\.


--
-- TOC entry 2201 (class 0 OID 0)
-- Dependencies: 188
-- Name: tb_sensor_source_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('tb_sensor_source_id_seq', 1, false);


--
-- TOC entry 2051 (class 2606 OID 33305)
-- Name: tb_sensor_has_sensor_measure_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tb_sensor_has_sensor_measure_type
    ADD CONSTRAINT tb_sensor_has_sensor_measure_type_pkey PRIMARY KEY (sensor_id, sensor_measure_type_id);


--
-- TOC entry 2053 (class 2606 OID 33313)
-- Name: tb_sensor_measure_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tb_sensor_measure
    ADD CONSTRAINT tb_sensor_measure_pkey PRIMARY KEY (id);


--
-- TOC entry 2055 (class 2606 OID 33324)
-- Name: tb_sensor_measure_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tb_sensor_measure_type
    ADD CONSTRAINT tb_sensor_measure_type_pkey PRIMARY KEY (id);


--
-- TOC entry 2049 (class 2606 OID 33300)
-- Name: tb_sensor_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tb_sensor
    ADD CONSTRAINT tb_sensor_pkey PRIMARY KEY (id);


--
-- TOC entry 2057 (class 2606 OID 33335)
-- Name: tb_sensor_source_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tb_sensor_source
    ADD CONSTRAINT tb_sensor_source_pkey PRIMARY KEY (id);


--
-- TOC entry 2060 (class 2606 OID 33346)
-- Name: fk7kh7vycrh6wo1xopweg4tbu0f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tb_sensor_has_sensor_measure_type
    ADD CONSTRAINT fk7kh7vycrh6wo1xopweg4tbu0f FOREIGN KEY (sensor_id) REFERENCES tb_sensor(id);


--
-- TOC entry 2058 (class 2606 OID 33336)
-- Name: fk8iacr1nconflem1qa5fy75dlu; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tb_sensor
    ADD CONSTRAINT fk8iacr1nconflem1qa5fy75dlu FOREIGN KEY (sensor_source_id) REFERENCES tb_sensor_source(id);


--
-- TOC entry 2059 (class 2606 OID 33341)
-- Name: fko4b836kfoir93b40t56rs62q6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tb_sensor_has_sensor_measure_type
    ADD CONSTRAINT fko4b836kfoir93b40t56rs62q6 FOREIGN KEY (sensor_measure_type_id) REFERENCES tb_sensor_measure_type(id);


--
-- TOC entry 2062 (class 2606 OID 33356)
-- Name: fktabdn3sejqud2dm2oym8olqiu; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tb_sensor_measure
    ADD CONSTRAINT fktabdn3sejqud2dm2oym8olqiu FOREIGN KEY (sensor_measure_type_id) REFERENCES tb_sensor_measure_type(id);


--
-- TOC entry 2061 (class 2606 OID 33351)
-- Name: fktbhtqf9ftmiueppjy6evacfm2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tb_sensor_measure
    ADD CONSTRAINT fktbhtqf9ftmiueppjy6evacfm2 FOREIGN KEY (sensor_id) REFERENCES tb_sensor(id);


--
-- TOC entry 2192 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2017-10-30 20:59:28 BRST

--
-- PostgreSQL database dump complete
--

