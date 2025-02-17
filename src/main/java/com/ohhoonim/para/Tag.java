package com.ohhoonim.para;

import java.util.Set;

public record Tag(
        Long tagId,
        String tag) {
    public interface Usecase {
        public Set<Tag> findTagsLimit20PerPage(String tag, Page page);
    }
}