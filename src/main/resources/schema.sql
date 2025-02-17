create sequence if not exists seq_para_tag start with 1;

create table if not exists para_tag (
    tag_id bigint not null default nextval('seq_para_tag'),
    tag varchar(36),
    constraint pk_para_tag primary key (tag_id)
);

create table if not exists para_note (
    note_id varchar(36) not null,
    title varchar(255),
    content text,
    constraint pk_para_note primary key (note_id)
);

comment on table para_note is '노트';
comment on column para_note.note_id is 'id';
comment on column para_note.title is '제목';
comment on column para_note.content is '노트 내용';


create table if not exists para_note_tag (
    note_id varchar(36) not null,
    tag_id bigint not null,
    constraint pk_para_note_tag primary key (note_id, tag_id),
    constraint fk_para_note_tag_note_id foreign key (note_id) references para_note(note_id),
    constraint fk_para_note_tag_tag_id foreign key( tag_id) references para_tag(tag_id)
);

create table if not exists para_project (
    project_id varchar(36) not null,
    title varchar(255),
    content text,
    start_date timestamp,
    end_date timestamp,
    status varchar(10), /* backlog, ready, inprogress, done */
    constraint pk_para_project primary key (project_id)
);

comment on table para_project is 'para Project';
comment on column para_project.project_id is 'id';
comment on column para_project.title is '프로젝트명';
comment on column para_project.content is '프로젝트 내용';
comment on column para_project.start_date is '시작일';
comment on column para_project.end_date is '종료일';
comment on column para_project.status is '진행항태. backlog,ready, inprogress, done';

create table if not exists para_project_note (
    project_id varchar(36) not null,
    note_id varchar(36) not null,
    constraint pk_para_project_note primary key (project_id, note_id),
    constraint fk_para_project_note_project_id foreign key (project_id) references para_project(project_id),
    constraint fk_para_project_note_note_id foreign key (note_id) references para_note(note_id)
);

create table if not exists para_shelf (
    shelf_id varchar(36) not null,
    shape varchar(10) not null, /* area, resource, archive */
    title varchar(255),
    content text,
    constraint pk_para_shelf primary key (shelf_id)
);

comment on table para_shelf is 'area resource archive';
comment on column para_shelf.shelf_id is 'id';
comment on column para_shelf.shape is '구분. area, resource, archive';
comment on column para_shelf.title is '제목';
comment on column para_shelf.content is '내용';

create table if not exists para_shelf_note (
    shelf_id varchar(36) not null,
    note_id varchar(36) not null,
    constraint pk_para_shelf_note primary key (shelf_id, note_id),
    constraint fk_para_shelf_note_shelf_id foreign key (shelf_id) references para_shelf(shelf_id),
    constraint fk_para_shelf_note_note_id foreign key (note_id) references para_note(note_id)
);
