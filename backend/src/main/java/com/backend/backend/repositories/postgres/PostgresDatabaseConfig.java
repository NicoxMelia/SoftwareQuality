package com.backend.backend.repositories.postgres;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@Profile("k8s")
public class PostgresDatabaseConfig {

    @Value("${DB_URL:jdbc:postgresql://db:5432/iot_db}")
    private String dbUrl;

    @Value("${DB_USER:admin}")
    private String dbUser;

    @Value("${DB_PASSWORD:supersecreto123}")
    private String dbPassword;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl(dbUrl);
        ds.setUsername(dbUser);
        ds.setPassword(dbPassword);
        return ds;
}

    @Bean
public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
    initSchemaAndSeed(jdbc);
        return jdbc;
    }

private void initSchemaAndSeed(JdbcTemplate jdbc) {
        jdbc.execute("CREATE TABLE IF NOT EXISTS categorias ("
        + "id_categoria VARCHAR(20) PRIMARY KEY, "
                + "codigo VARCHAR(50), "
        + "descrip VARCHAR(255), "
                + "vigente BOOLEAN)");

        jdbc.execute("CREATE TABLE IF NOT EXISTS sub_categorias ("
                + "id_sub_categoria VARCHAR(20) PRIMARY KEY, "
                + "codigo VARCHAR(50), "
                + "descrip VARCHAR(255), "
                + "id_categoria VARCHAR(20), "
        + "vigente BOOLEAN)");

    jdbc.execute("CREATE TABLE IF NOT EXISTS productos ("
                + "id_producto VARCHAR(20) PRIMARY KEY, "
        + "descrip VARCHAR(255), "
                + "precio REAL, "
                + "vigente BOOLEAN, "
                + "con_stock BOOLEAN, "
                + "cant_stock INTEGER, "
        + "id_sub_categoria VARCHAR(20), "
                + "id_categoria VARCHAR(20), "
                + "link_imagen TEXT, "
                + "destacado BOOLEAN)");

        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM categorias", Integer.class);
        if (count != null && count == 0) {
            jdbc.update("INSERT INTO categorias (id_categoria, codigo, descrip, vigente) VALUES (?, ?, ?, ?)",
            "cat00000000000000001", "SENS", "Sensores AgTech", true);
            jdbc.update("INSERT INTO categorias (id_categoria, codigo, descrip, vigente) VALUES (?, ?, ?, ?)",
                    "cat00000000000000002", "TELE", "Telemetria ESP32", true);

    jdbc.update("INSERT INTO sub_categorias "
                    + "(id_sub_categoria, codigo, descrip, id_categoria, vigente) VALUES (?, ?, ?, ?, ?)",
                    "sub00000000000000001", "SUELO", "Humedad de Suelo", "cat00000000000000001", true);
            jdbc.update("INSERT INTO sub_categorias "
                    + "(id_sub_categoria, codigo, descrip, id_categoria, vigente) VALUES (?, ?, ?, ?, ?)",
                    "sub00000000000000002", "CLIMA", "Estacion Meteorologica", "cat00000000000000002", true);

            String img = "https://placehold.co/300x300/2a6f4e/ffffff?text=TerraSense+ESP32";
        jdbc.update("INSERT INTO productos (id_producto, descrip, precio, vigente, con_stock, cant_stock, "
            + "id_sub_categoria, id_categoria, link_imagen, destacado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            "prd00000000000000001", "Nodo Sensor ESP32 Humedad Suelo", 45000.0f, true, true, 50,
            "sub00000000000000001", "cat00000000000000001", img, true);
            jdbc.update("INSERT INTO productos (id_producto, descrip, precio, vigente, con_stock, cant_stock, "
            + "id_sub_categoria, id_categoria, link_imagen, destacado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            "prd00000000000000002", "Sonda NPK Conductividad Electrica", 62000.0f, true, true, 35,
            "sub00000000000000001", "cat00000000000000001", img, true);
            jdbc.update("INSERT INTO productos (id_producto, descrip, precio, vigente, con_stock, cant_stock, "
            + "id_sub_categoria, id_categoria, link_imagen, destacado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            "prd00000000000000003", "Estacion Clima LoRaWAN ESP32", 120000.0f, true, true, 15,
            "sub00000000000000002", "cat00000000000000002", img, true);
        }
    }
}