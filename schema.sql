create table accounts
(
    account_type   varchar(31)  not null,
    id             binary(16)   not null
        primary key,
    email          varchar(255) null,
    name           varchar(255) null,
    password       varchar(255) null,
    registered_at  datetime(6)  not null,
    role           tinyint      null,
    comments_count int          null,
    friends_count  int          null,
    constraint email
        unique (email)
);

create table friendships
(
    id        binary(16) not null
        primary key,
    accepted  bit        not null,
    friend_id binary(16) not null,
    user_id   binary(16) not null,
    constraint UKjwaac0iw9d1fu58mx7afwf9f4
        unique (user_id, friend_id),
    constraint FK3vw7qq1nf7d3qidy4ql4mcu2g
        foreign key (user_id) references accounts (id)
            on delete cascade,
    constraint FKfepuf4a5oxa4q2x15k417rcmx
        foreign key (friend_id) references accounts (id)
            on delete cascade
);

create table messages
(
    id        binary(16)   not null
        primary key,
    recipient varchar(255) null,
    sender    varchar(255) null,
    text      varchar(255) null,
    timestamp datetime(6)  null,
    constraint fk_messages_recipient
        foreign key (recipient) references accounts (email)
            on delete cascade,
    constraint fk_messages_sender
        foreign key (sender) references accounts (email)
            on delete cascade
);

create table posts
(
    id         bigint auto_increment
        primary key,
    content    tinytext    not null,
    created_at datetime(6) null,
    user_id    binary(16)  null,
    constraint FKrpd26d053g5nkjn04cn2sfurr
        foreign key (user_id) references accounts (id)
            on delete cascade
);

create table post_comments
(
    id         bigint auto_increment
        primary key,
    content    tinytext    not null,
    created_at datetime(6) null,
    user_id    binary(16)  null,
    post_id    bigint      null,
    constraint FKaawaqxjs3br8dw5v90w7uu514
        foreign key (post_id) references posts (id)
            on delete cascade,
    constraint FKeouew0322br7hls7lgw5523ns
        foreign key (user_id) references accounts (id)
            on delete cascade
);

create table post_likes
(
    id       bigint auto_increment
        primary key,
    liked_at datetime(6) null,
    post_id  bigint      not null,
    user_id  binary(16)  not null,
    constraint UK5l2rj28vw5oj6f7ox746grokg
        unique (post_id, user_id),
    constraint unique_post_user
        unique (post_id, user_id),
    constraint FK_post
        foreign key (post_id) references posts (id)
            on delete cascade,
    constraint FK_user
        foreign key (user_id) references accounts (id)
            on delete cascade
);

create table post_reports
(
    id          bigint auto_increment
        primary key,
    reason      varchar(500) null,
    reported_at datetime(6)  null,
    post_id     bigint       not null,
    reporter_id binary(16)   not null,
    constraint UK4b1x1p130tsm6vlvruk6jkqlr
        unique (reporter_id, post_id),
    constraint FK4yg88o0crrdfqmaoea16upkv0
        foreign key (reporter_id) references accounts (id)
            on delete cascade,
    constraint FK7ccpkj5jys037f9pq98l31ya2
        foreign key (post_id) references posts (id)
            on delete cascade
);

