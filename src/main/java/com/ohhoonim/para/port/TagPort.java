package com.ohhoonim.para.port;

import java.util.Set;
import java.util.UUID;

import com.ohhoonim.para.Page;
import com.ohhoonim.para.Tag;

public interface TagPort {

    Set<Tag> findTags(String tag, Page page);

    void addTagInNote(UUID noteId, Tag tag);

    Set<Tag> tagsInNote(UUID noteId);

    void removeTagInNote(UUID noteId, Tag tag);
    
}
