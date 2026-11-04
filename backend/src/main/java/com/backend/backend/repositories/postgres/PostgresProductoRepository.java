package com.backend.backend.repositories.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.backend.backend.dto.FiltroProductosDTO;
import com.backend.backend.dto.ProductoDTO;
import com.backend.backend.repositories.ProductoRepository;

@Repository
@Profile("k8s")
public class PostgresProductoRepository implements ProductoRepository {

    private final JdbcTemplate jdbc;

    public PostgresProductoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public ProductoDTO get(String idProducto) {
        List<ProductoDTO> list = jdbc.query(
                "SELECT id_producto, descrip, precio, vigente, con_stock, cant_stock, "
                        + "id_sub_categoria, id_categoria, link_imagen, destacado "
                        + "FROM productos WHERE id_producto = ?",
                this::mapRow, idProducto);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<ProductoDTO> list() {
        return jdbc.query(
                "SELECT id_producto, descrip, precio, vigente, con_stock, cant_stock, "
                        + "id_sub_categoria, id_categoria, link_imagen, destacado "
                        + "FROM productos LIMIT 200",
                this::mapRow);
    }

    @Override
    public List<ProductoDTO> getProductosPorFiltro(FiltroProductosDTO filtro) {
        StringBuilder sql = new StringBuilder(
                "SELECT id_producto, descrip, precio, vigente, con_stock, cant_stock, "
                        + "id_sub_categoria, id_categoria, link_imagen, destacado "
                        + "FROM productos WHERE vigente = true");
        List<Object> params = new ArrayList<>();

        if (filtro.getIdCategoria() != null) {
            sql.append(" AND id_categoria = ?");
            params.add(filtro.getIdCategoria());
        }
        if (filtro.getIdSubCategoria() != null) {
            sql.append(" AND id_sub_categoria = ?");
            params.add(filtro.getIdSubCategoria());
        }
        if (sinFiltros(filtro)) {
            sql.append(" AND destacado = true");
        }
        sql.append(" LIMIT 200");

        List<ProductoDTO> rows = jdbc.query(sql.toString(), this::mapRow, params.toArray());
        List<ProductoDTO> ret = new ArrayList<>();
        for (ProductoDTO tmp : rows) {
            if (coincideDescripcion(tmp, filtro.getDescrip())) {
                ret.add(tmp);
            }
        }
        return ret;
    }

    @Override
    public ProductoDTO insert(Map<String, Object> pr) {
        String id = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        jdbc.update("INSERT INTO productos (id_producto, descrip, precio, vigente, con_stock, cant_stock, "
                + "id_sub_categoria, id_categoria, link_imagen, destacado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                id,
                pr.get("descrip"),
                pr.get("precio"),
                pr.get("vigente"),
                pr.get("conStock"),
                pr.get("cantStock"),
                pr.get("idSubCategoria"),
                pr.get("idCategoria"),
                pr.get("linkImagen"),
                pr.get("destacado"));
        return get(id);
    }

    private boolean sinFiltros(FiltroProductosDTO filtro) {
        return filtro.getIdSubCategoria() == null && filtro.getIdCategoria() == null
                && (filtro.getDescrip() == null || filtro.getDescrip().isEmpty());
    }

    private boolean coincideDescripcion(ProductoDTO producto, String descripcion) {
        return descripcion == null || descripcion.isEmpty()
                || (producto.getDescrip() != null
                && producto.getDescrip().toLowerCase(Locale.ROOT).contains(descripcion.toLowerCase(Locale.ROOT)));
    }

    private ProductoDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        ProductoDTO dto = new ProductoDTO();
        dto.setIdProducto(rs.getString("id_producto"));
        dto.setDescrip(rs.getString("descrip"));
        dto.setPrecio(rs.getFloat("precio"));
        dto.setVigente(rs.getBoolean("vigente"));
        dto.setConStock(rs.getBoolean("con_stock"));
        dto.setCantStock(rs.getInt("cant_stock"));
        dto.setIdSubCategoria(rs.getString("id_sub_categoria"));
        dto.setIdCategoria(rs.getString("id_categoria"));
        dto.setLinkImagen(rs.getString("link_imagen"));
        dto.setDestacado(rs.getBoolean("destacado"));
        return dto;
    }
}