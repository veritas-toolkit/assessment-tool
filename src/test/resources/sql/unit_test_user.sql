
insert into vat_user(username, full_name, email, password, admin,
project_limited, group_limited, created_time, last_login_time)
values("test_1", "test_1", "test_1@example.com", "$2a$10$XRz2ew7AX.StHRF.XptumuXSSSHiF4ylTCC5uDBO1l2z51maRnE82", 0,
100, 10, datetime('now'), null);

insert into vat_user(username, full_name, email, password, admin,
project_limited, group_limited, created_time, last_login_time)
values("test_2", "test_2", "test_2@example.com", "$2a$10$XRz2ew7AX.StHRF.XptumuXSSSHiF4ylTCC5uDBO1l2z51maRnE82", 0,
100, 10, datetime('now'), null);
