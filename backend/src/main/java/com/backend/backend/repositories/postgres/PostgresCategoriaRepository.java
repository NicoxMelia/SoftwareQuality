package com.backend.backend.repositories.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.backend.backend.dto.CategoriaDTO;
import com.backend.backend.repositories.CategoriaRepository;

@Repository
@Profile("k8s")
public class PostgresCategoriaRepository implements CategoriaRepository {

private final JdbcTemplate jdbc;

    public PostgresCategoriaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
public CategoriaDTO get(String idCategoria) {
        List<CategoriaDTO> list = jdbc.query(
        "SELECT id_categoria, codigo, descrip, vigente FROM categorias WHERE id_categoria = ?",
        this::mapRow, idCategoria);
    return list.isEmpty() ? null : list.get(0);
}

    @Override
public List<CategoriaDTO> list() {
    return jdbc.query("SELECT id_categoria, codigo, descrip, vigente FROM categorias", this::mapRow);
    }

    @Override
public CategoriaDTO insert(CategoriaDTO categoria) {
    String id = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    jdbc.update("INSERT INTO categorias (id_categoria, codigo, descrip, vigente) VALUES (?, ?, ?, ?)",
                id, categoria.getCodigo(), categoria.getDescrip(), categoria.getVigente());
        return get(id);
    }

private CategoriaDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setIdCategoria(rs.getString("id_categoria"));
        dto.setCodigo(rs.getString("codigo"));
        dto.setDescrip(rs.getString("descrip"));
        dto.setVigente(rs.getBoolean("vigente"));
        return dto;
    }
}