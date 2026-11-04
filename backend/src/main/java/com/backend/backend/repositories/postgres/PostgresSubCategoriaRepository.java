package com.backend.backend.repositories.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.backend.backend.dto.SubCategoriaDTO;
import com.backend.backend.repositories.SubCategoriaRepository;

@Repository
@Profile("k8s")
public class PostgresSubCategoriaRepository implements SubCategoriaRepository {

private final JdbcTemplate jdbc;

    public PostgresSubCategoriaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
public SubCategoriaDTO get(String idSubCategoria) {
    List<SubCategoriaDTO> list = jdbc.query(
                "SELECT id_sub_categoria, codigo, descrip, id_categoria, vigente "
            + "FROM sub_categorias WHERE id_sub_categoria = ?",
        this::mapRow, idSubCategoria);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
public String insert(SubCategoriaDTO subCategoria) {
        String id = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        jdbc.update("INSERT INTO sub_categorias "
                + "(id_sub_categoria, codigo, descrip, id_categoria, vigente) VALUES (?, ?, ?, ?, ?)",
                id, subCategoria.getCodigo(), subCategoria.getDescrip(),
                subCategoria.getIdCategoria(), subCategoria.getVigente());
        return id;
    }

@Override
    public List<SubCategoriaDTO> list() {
    return jdbc.query(
                "SELECT id_sub_categoria, codigo, descrip, id_categoria, vigente FROM sub_categorias",
                this::mapRow);
    }

@Override
    public List<SubCategoriaDTO> getPorIdCategoria(String idCategoria) {
        return jdbc.query(
                "SELECT id_sub_categoria, codigo, descrip, id_categoria, vigente "
                        + "FROM sub_categorias WHERE vigente = true AND id_categoria = ?",
        this::mapRow, idCategoria);
    }

    private SubCategoriaDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        SubCategoriaDTO dto = new SubCategoriaDTO();
        dto.setIdSubCategoria(rs.getString("id_sub_categoria"));
    dto.setCodigo(rs.getString("codigo"));
        dto.setDescrip(rs.getString("descrip"));
    dto.setIdCategoria(rs.getString("id_categoria"));
        dto.setVigente(rs.getBoolean("vigente"));
    return dto;
    }
}