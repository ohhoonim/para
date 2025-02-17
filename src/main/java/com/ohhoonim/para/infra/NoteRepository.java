package com.ohhoonim.para.infra;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.ohhoonim.para.Note;
import com.ohhoonim.para.Page;
import com.ohhoonim.para.Tag;
import com.ohhoonim.para.port.NotePort;
import com.ohhoonim.para.port.TagPort;

@Repository
public class NoteRepository implements NotePort, TagPort {
    private final JdbcClient jdbcClient;

    public NoteRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void addNote(Note newNote, UUID newNoteId) {
        var sql = """
                insert into para_note (note_id, title, content)
                values(:noteId, :title, :content)
                 """;

        jdbcClient.sql(sql)
                .params(toParamMap.apply(newNote, newNoteId))
                .update();
    }

    private final BiFunction<Note, UUID, Map<String, Object>> toParamMap = (newNote, newNoteId) -> {
        var map = new HashMap<String, Object>();
        map.put("noteId", newNoteId.toString());
        map.put("title", newNote.title());
        map.put("tagId", null);
        map.put("content", newNote.content());
        return map;
    };

    @Override
    public Optional<Note> getNote(UUID noteId) {
        var sql = """
                select
                    note_id, title, content
                from
                    para_note
                where
                    note_id = :noteId
                """;
        return jdbcClient.sql(sql)
                .param("noteId", noteId.toString())
                .query(rowmapper)
                // .query(Note.class)
                .optional();
    }

    private final RowMapper<Note> rowmapper = ((rs, idx) -> {
        return new Note(
                UUID.fromString(rs.getString("note_id")),
                rs.getString("title"),
                rs.getString("content"));
    });

    @Override
    public void modifyNote(Note modifiedNote) {
        var sql = """
                update para_note
                set
                    title = :title
                    , content = :content
                where
                    note_id = :noteId
                """;
        jdbcClient.sql(sql)
                .params(toPramMap.apply(modifiedNote))
                .update();
    }

    private final Function<Note, Map<String, Object>> toPramMap = note -> {
        Map<String, Object> map = new HashMap<>();
        map.put("title", note.title());
        map.put("content", note.content());
        map.put("noteId", note.noteId().toString());
        return map;
    };

    @Override
    public void removeNote(UUID noteId) {
        var sql = """
                delete from para_note where note_id = :noteId
                """;
        jdbcClient.sql(sql)
            .param("noteId", noteId.toString())
            .update();
    }

    @Override
    public Set<Tag> findTags(String tag, Page page) {
        var sql = """
                select tag_id as tagId, tag from para_tag
                where
                    tag_id > :lastSeenKey
                    and tag like '%:tag%'
                order by tag
                limit :limit
                """;
        return jdbcClient.sql(sql)
                .param("tag", tag)
                .param("lastSeenKey", page.lastSeenKey().toString())
                .param("limit", page.limit())
                .query(Tag.class)
                .set();
    }

    @Override
    public void addTagInNote(UUID noteId, Tag tag) {
        var tagId = tag.tagId();
        if (tag.tagId() == null) {
            tagId = this.addTag(tag).tagId();
        }
        var sql = """
                insert into para_note_tag (note_id, tag_id)
                values(:noteId, :tagId)
                """;
        jdbcClient.sql(sql)
                .param("noteId", noteId.toString())
                .param("tagId", tagId)
                .update();
    }

    @SuppressWarnings("null")
    public Tag addTag(Tag tag) {
        var sql = """
                insert into para_tag (tag) values(:tag)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(sql).param("tag", tag.tag())
                .update(keyHolder);

        return jdbcClient.sql("select tag_id, tag from para_tag where tag_id = :tagId")
                .param("tagId", keyHolder.getKeys().get("tag_id"))
                .query(Tag.class).single();
    }

    @Override
    public Set<Tag> tagsInNote(UUID noteId) {
        var sql = """
                select
                    t.tag_id,
                    t.tag
                from para_note_tag nt
                join para_tag t
                on nt.tag_id = t.tag_id
                where nt.note_id = :noteId
                """;
        return jdbcClient.sql(sql).param("noteId", noteId.toString())
                .query(Tag.class).set();
    }

    @Override
    public void removeTagInNote(UUID noteId, Tag tag) {
        var sql = """
                delete from para_note_tag
                where
                    note_id = :noteId and tag_id = :tagId
                """;
        jdbcClient.sql(sql)
                .param("noteId", noteId.toString())
                .param("tagId", tag.tagId())
                .update();
    }

    @Override
    public List<Note> findNote(String searchString, Page page) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findNote'");
    }

}
