package com.ohhoonim.para;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ohhoonim.para.Para.Shelf.Archive;
import com.ohhoonim.para.Para.Shelf.Area;
import com.ohhoonim.para.Para.Shelf.Resource;

public sealed interface Para {
	public UUID paraId();

	public String title();

	public String content();

	public String category();

	/**
	 * paraId만으로 Para 객체를 생성할 때 사용 
	 * 각 레코드들은 UUID 타입 파라미터만 가지는 constructor가 있어야한다.
	 * ({@link Para.Project} 참조)
	 */
	public static <T extends Para> T of(UUID paraId, Class<T> paraType) {
		T paraInstance = null;
		try {
			Constructor<T> uuidConstructor = paraType.getConstructor(
					UUID.class);
			paraInstance = uuidConstructor.newInstance(paraId);
		} catch (Exception e) {
			throw new RuntimeException("not exists contructor(UUID)");
		}

		return paraInstance;
	}

	public static Para getParaInstance(UUID paraId, String category) {
		return switch (category) {
			case "project" -> Para.of(paraId, Project.class);
			case "area" -> Para.of(paraId, Area.class);
			case "resource" -> Para.of(paraId, Resource.class);
			case "archive" -> Para.of(paraId, Archive.class);
			case null, default -> null;
		};
	}

	public static Para getNewParaInstance(String category,
			String title, String content) {
		return switch (category) {
			case "project" -> new Project(title, content);
			case "area" -> new Area(null, title, content);
			case "resource" -> new Resource(null, title, content);
			case "archive" -> new Archive(null, title, content);
			case null, default -> null;
		};
	}

	public enum ParaEnum {
		Project("project"),
		Area("area"),
		Resource("resource"),
		Archive("archive");

		private final String paraString;

		private ParaEnum(String paraString) {
			this.paraString = paraString;
		}

		@Override
		public String toString() {
			return this.paraString;
		}
	}

	public record Project(
			UUID paraId,
			String title,
			String content,
			LocalDate startDate,
			LocalDate endDate,
			Status status,
			String category) implements Para {
		public Project {
			category = ParaEnum.Project.toString();
		}

		public Project(UUID paraId) {
			this(paraId, null, null, null, null, null, ParaEnum.Project.toString());
		}

		public Project(String title, String content) {
			this(null, title, content, null, null, null, ParaEnum.Project.toString());
		}

		public Project(UUID paraId,
				String title,
				String content,
				LocalDate startDate,
				LocalDate endDate,
				Status status) {
			this(paraId, title, content, startDate, endDate, status, ParaEnum.Project.toString());
		}
	}

	public sealed interface Shelf extends Para {

		public static Class<? extends Shelf> getParaType(String category) {
			return switch (category) {
				case "area" -> Area.class;
				case "resource" -> Resource.class;
				case "archive" -> Archive.class;
				case "project" -> null;
				case null, default -> null;
			};
		}

		public record Area(
				UUID paraId,
				String title,
				String content,
				String category) implements Shelf {
			public Area {
				category = ParaEnum.Area.toString();
			}

			public Area(UUID paraId, String title, String content) {
				this(paraId, title, content, ParaEnum.Area.toString());
			}

			public Area(UUID paraId) {
				this(paraId, null, null, ParaEnum.Area.toString());
			}
		}

		public record Resource(
				UUID paraId,
				String title,
				String content,
				String category) implements Shelf {
			public Resource {
				category = ParaEnum.Resource.toString();
			}

			public Resource(UUID paraId, String title, String category) {
				this(paraId, title, title, ParaEnum.Resource.toString());
			}

			public Resource(UUID paraId) {
				this(paraId, null, null, ParaEnum.Resource.toString());
			}
		}

		public record Archive(
				UUID paraId,
				String title,
				String content,
				String category) implements Shelf {
			public Archive {
				category = ParaEnum.Archive.toString();
			}

			public Archive(UUID paraId, String title, String content) {
				this(paraId, title, content, ParaEnum.Archive.toString());
			}

			public Archive(UUID paraId) {
				this(paraId, null, null, ParaEnum.Archive.toString());
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
