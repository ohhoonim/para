package com.ohhoonim.para.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ohhoonim.para.Note;
import com.ohhoonim.para.Page;
import com.ohhoonim.para.Para;
import com.ohhoonim.para.Para.Project;
import com.ohhoonim.para.Para.Shelf;
import com.ohhoonim.para.port.ProjectPort;
import com.ohhoonim.para.port.ShelfPort;

@Service
public class ParaService implements Para.Usecase {

    private final ProjectPort projectPort;
    private final ShelfPort shelfPort;

    public ParaService(ProjectPort projectPort, ShelfPort shelfPort) {
        this.projectPort = projectPort;
        this.shelfPort = shelfPort;
    }

    @Override
    public List<Note> notes(Para para) {
        if (para instanceof Project p) {
            return projectPort.notes(p);
        } else if (para instanceof Shelf s) {
            return shelfPort.notes(s);
        }
        return List.of();
    }

    @Override
    public List<Note> registNote(Para para, UUID noteId) {
        if (para instanceof Project p) {
            projectPort.registNote(noteId, p);
            return projectPort.notes(p);
        } else if (para instanceof Shelf s) {
            shelfPort.registNote(noteId, s);
            return shelfPort.notes(s);
        }
        return List.of();
    }

    @Override
    public List<Note> removeNote(Para para, UUID noteId) {
        if (para instanceof Project p) {
            projectPort.removeNote(noteId, p);
            return projectPort.notes(p);
        } else if (para instanceof Shelf s) {
            shelfPort.removeNote(noteId, s);
            return shelfPort.notes(s);
        }
        return List.of();
    }

    @Override
    public Optional<Para> moveToPara(Para origin, Class<? extends Shelf> targetPara) {
        shelfPort.moveToPara(origin, targetPara);
        return shelfPort.getShelf(origin.paraId());
    }

    @Override
    public Optional<Para> getPara(Para para) {
        if (para instanceof Project p) {
            return projectPort.getProject(p.paraId());
        } else if (para instanceof Shelf s) {
            return shelfPort.getShelf(s.paraId());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Para> addPara(Para para) {
        var newParaId = UUID.randomUUID();
        if (para instanceof Project p) {
            projectPort.addProject(p, newParaId);
            return projectPort.getProject(newParaId);
        } else if (para instanceof Shelf s) {
            shelfPort.addShelf(s, newParaId);
            return shelfPort.getShelf(newParaId);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Para> modifyPara(Para para) {
        if (para instanceof Project p) {
            projectPort.modifyProject(p);
            return projectPort.getProject(p.paraId());
        } else if (para instanceof Shelf s) {
            shelfPort.modifyShelf(s);
            return shelfPort.getShelf(s.paraId());
        }
        return Optional.empty();
    }

    @Override
    public void removePara(Para para) {
        if (para instanceof Project p) {
            projectPort.removeProject(p);
        } else if (para instanceof Shelf s) {
            shelfPort.removeShelf(s);
        }
    }

    @Override
    public List<Shelf> findShelves(String searchString, Page page) {
        return shelfPort.findShelves(searchString, page);
    }

    @Override
    public List<Project> findProjects(String searchString, Page page) {
        return projectPort.findProjects(searchString, page);
    }

}
