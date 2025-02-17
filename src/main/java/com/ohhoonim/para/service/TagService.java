package com.ohhoonim.para.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.ohhoonim.para.Page;
import com.ohhoonim.para.Tag;
import com.ohhoonim.para.Tag.Usecase;
import com.ohhoonim.para.port.TagPort;

@Service
public class TagService implements Usecase {

    private final TagPort tagPort;

    public TagService(TagPort tagPort) {
        this.tagPort = tagPort;
    }

    @Override
    public Set<Tag> findTagsLimit20PerPage(String tag, Page page) {
        return tagPort.findTags(tag, page);
    }
    
}
