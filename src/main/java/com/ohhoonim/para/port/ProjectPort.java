package com.ohhoonim.para.port;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.ohhoonim.para.Note;
import com.ohhoonim.para.Page;
import com.ohhoonim.para.Para;
import com.ohhoonim.para.Para.Project;

public interface ProjectPort {

    List<Project> findProjects(String searchString, Page page);

    void registNote(UUID noteId, Project project);

    void addProject(Project project, UUID newParaId);

    Optional<Para> getProject(UUID paraId);

    List<Note> notes(Project para);

    void removeNote(UUID noteId, Project p);

    void removeProject(Project p);

    void modifyProject(Project p);

    Set<Para> findProjectInNote(UUID noteId);
    
}
