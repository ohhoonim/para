package com.ohhoonim.para.infra;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ohhoonim.para.Note;
import com.ohhoonim.para.Page;
import com.ohhoonim.para.Para;
import com.ohhoonim.para.Para.Shelf;
import com.ohhoonim.para.Para.Shelf.Archive;
import com.ohhoonim.para.Para.Shelf.Area;
import com.ohhoonim.para.Para.Shelf.Resource;
import com.ohhoonim.para.port.ShelfPort;

@Repository
public class ShelfRepository implements ShelfPort {

    private final JdbcClient jdbcClient;

    public ShelfRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // shelf 관리
    @Override
    public void addShelf(Shelf s, UUID newParaId) {
        var sql = """
                INSERT INTO para_shelf (shelf_id, shape, title, "content")
                VALUES (:shelf_id, :shape, :title, :content)
                """;
        jdbcClient.sql(sql)
                .params(toShelfMap.apply(s, newParaId))
                .update();
    }

    private final BiFunction<Shelf, UUID, Map<String, Object>> toShelfMap = (shelf, paraId) -> {
        String shape = switch (shelf) {
            case Area a -> "area";
            case Resource r -> "reaorce";
            case Archive a -> "archive";
        };

        var map = new HashMap<String, Object>();
        map.put("shelf_id", paraId.toString());
        map.put("shape", shape);
        map.put("title", shelf.title());
        map.put("content", shelf.content());
        return map;
    };

    @Override
    public Optional<Para> getShelf(UUID paraId) {
        var sql = """
                select shelf_id, shape, title, content
                from para_shelf
                where shelf_id = :paraId
                """;
        return jdbcClient.sql(sql)
                .param("paraId", paraId.toString())
                .query(shelfRowMapper).optional();
    }

    private final RowMapper<Para> shelfRowMapper = (rs, idx) -> {
        var shelfId = UUID.fromString(rs.getString("shelf_id"));
        var title = rs.getString("title");
        var content = rs.getString("content");

        return switch (rs.getString("shape")) {
            case "area" -> new Area(shelfId, title, content);
            case "resource" -> new Resource(shelfId, title, content);
            case "archive" -> new Archive(shelfId, title, content);
            case null, default -> null;
        };

    };

    @Override
    public void modifyShelf(Shelf s) {
        var sql = """
                update para_shelf
                set
                     title = :title
                     , content = :content
                 where
                     shelf_id = :shelf_id
                 """;
        jdbcClient.sql(sql)
                .param("shelf_id", s.paraId().toString())
                .param("title", s.title())
                .param("content", s.content())
                .update();
    }

    @Override
    public void moveToPara(Para origin, Class<? extends Shelf> targetPara) {
        var sql = """
                update para_shelf
                set
                    shape = :shape
                 where
                     shelf_id = :shelf_id
                 """;
        jdbcClient.sql(sql)
                .param("shelf_id", origin.paraId().toString())
                .param("shape", targetPara.getSimpleName().toLowerCase())
                .update();
    }

    @Override
    public void removeShelf(Shelf s) {
        var sql = """
                delete from para_shelf
                where shelf_id = :shelf_id
                """;
        jdbcClient.sql(sql)
                .param("shelf_id", s.paraId().toString())
                .update();
    }

    // note와 연결된 shelf 목록
    @Override
    public Set<Para> findShelfInNote(UUID noteId) {
        var sql = """
                select
                    s.shelf_id,
                    s.shape,
                    s.title,
                    s.content
                from para_shelf_note sn
                join para_shelf s
                on sn.shelf_id = s.shelf_id
                where sn.note_id = :noteId
                """;
        return jdbcClient.sql(sql)
                .param("noteId", noteId.toString())
                .query(shelfRowMapper).set();
    }

    // shelf내 노트 관리 
    @Override
    // addNote와 관련 테이블은 동일 
    public void registNote(UUID noteId, Shelf s) {
        var sql = """
                insert into para_shelf_note (shelf_id, note_id)
                values (:shelf_id, :note_id)
                """;
        jdbcClient.sql(sql)
                .param("shelf_id", s.paraId().toString())
                .param("note_id", noteId.toString())
                .update();
    }

    @Override
    public List<Note> notes(Shelf para) {
        var sql = """
                select
                    n.note_id,
                    n.title,
                    n.content
                from para_shelf_note sn
                join para_note n
                on sn.note_id = n.note_id
                where sn.shelf_id = :shelfId
                """;
        return jdbcClient.sql(sql)
                .param("shelfId", para.paraId().toString())
                .query(noteRowMapper).list();
    }

    private final RowMapper<Note> noteRowMapper = (rs, idx) -> {
        return new Note(
                UUID.fromString(rs.getString("note_id")),
                rs.getString("title"),
                rs.getString("content"));
    };

    @Override
    public void removeNote(UUID noteId, Shelf s) {
        var sql = """
                delete from para_shelf_note
                where shelf_id = :shelf_id and note_id = :note_id
                """;
        jdbcClient.sql(sql)
                .param("shelf_id", s.paraId().toString())
                .param("note_id", noteId.toString())
                .update();
    }

    // shelf 검색 
    @Override
    public List<Shelf> findShelves(String searchString, Page page) {

        var lastSeenKeyExpression = "and shelf < :lastSeenKey";

        if (isNullExpr.test(page.lastSeenKey())) {
            lastSeenKeyExpression = "";
        }

        var sql = """
                select shelf_id, shape, title, content
                from para_shelf
                where title like concat('%', :title, '%')
                    $lastSeenKeyExpression
                limit :limit
                """.replace("$lastSeenKeyExpression", lastSeenKeyExpression);

        return jdbcClient.sql(sql)
                .params(toSearchMap.apply(searchString, page))
                .query(searchShelfRowMapper).list();
    }

    // TODO query build에 대한 컴포넌트 작성 필요 
    private final Predicate<Object> isNullExpr = value -> {
        return switch (value) {
            case String s when s.length() == 0 -> true;
            case String s -> false;
            case null, default -> true;
        };
    };

    private final BiFunction<Object, Predicate<Object>, Object> qStr = (value, expr) -> {
        if (!expr.test(value)) {
            return switch (value) {
                case UUID uuid -> uuid.toString();
                case null, default -> value;
            };
        }
        return value;
    };
    private final BiFunction<String, Page, Map<String, Object>> toSearchMap = (searchString, page) -> {
        var map = new HashMap<String, Object>();
        map.put("title", searchString);
        map.put("limit", page.limit());
        if (!isNullExpr.test(page.lastSeenKey())) {
            map.put("lastSeenKey", qStr.apply(page.lastSeenKey(), isNullExpr));
        }
        return map;
    };

    private final RowMapper<Shelf> searchShelfRowMapper = (rs, idx) -> {
        var shelfId = UUID.fromString(rs.getString("shelf_id"));
        var title = rs.getString("title");
        var content = rs.getString("content");

        return switch (rs.getString("shape")) {
            case "area" -> new Area(shelfId, title, content);
            case "resource" -> new Resource(shelfId, title, content);
            case "archive" -> new Archive(shelfId, title, content);
            case null, default -> null;
        };
    };
    

}
