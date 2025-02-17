package com.ohhoonim.para;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public sealed interface Para {
	public UUID paraId();

	public String title();

	public String content();

	/**
	 * paraId만으로 Para 객체를 생성할 때 사용 
	 * 각 레코드들은 UUID 타입 파라미터만 가지는 constructor가 있어야한다.
	 * ({@link Para.Project} 참조)
	 */
	public static <T extends Para> T of (UUID paraId, Class<T> paraType){
		T paraInstance = null;
        try {
			Constructor<T> uuidConstructor = paraType.getConstructor(
				UUID.class);
			paraInstance = uuidConstructor.newInstance(paraId);
        } catch (Exception e){
			throw new RuntimeException("not exists contructor(UUID)");
		}

		return paraInstance;
    }

	public record Project(
			UUID paraId,
			String title,
			String content,
			LocalDate startDate,
			LocalDate endDate,
			Status status) implements Para {
		public Project(UUID paraId) {
			this(paraId, null, null, null, null, null);
		}
	}

	public sealed interface Shelf extends Para {
		public record Area(
				UUID paraId,
				String title,
				String content) implements Shelf {
			public Area(UUID paraId) {
				this(paraId, null, null);
			}
		}

		public record Resource(
				UUID paraId,
				String title,
				String content) implements Shelf {
			public Resource(UUID paraId) {
				this(paraId, null, null);
			}
		}

		public record Archive(
				UUID paraId,
				String title,
				String content) implements Shelf {
			public Archive(UUID paraId) {
				this(paraId, null, null);
			}
		}
	}

	public interface Usecase {
		public List<Note> notes(Para paraId);

		public List<Note> registNote(Para paraId, UUID noteId);

		public List<Note> removeNote(Para paraId, UUID noteId);

		public Optional<Para> moveToPara(Para origin, Class<? extends Shelf> targetPara);

		public Optional<Para> getPara(Para para);

		public Optional<Para> addPara(Para para);

		public Optional<Para> modifyPara(Para para);

		public void removePara(Para para);

		public List<Shelf> findShelves(String searchString, Page page);

		public List<Project> findProjects(String searchString, Page page);

	}
}
