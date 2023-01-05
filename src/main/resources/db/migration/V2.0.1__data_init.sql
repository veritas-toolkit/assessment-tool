----- ----- ----- ----- ----- ----- ----- ----- ----- -----
-- vat2_business_scenario
insert into vat2_business_scenario(code, name, description)
values(0, "Generic", "Generic");
insert into vat2_business_scenario(code, name, description)
values(1, "Credit Scoring", "Credit Scoring");
insert into vat2_business_scenario(code, name, description)
values(2, "Customer Marketing", "Customer Marketing");
insert into vat2_business_scenario(code, name, description)
values(3, "Insurance", "Insurance");

-- vat2_user
insert into vat2_user(username, full_name, email, password,
admin,
project_limited, group_limited, created_time, last_login_time)
values("admin", "admin", "admin@example.com", "$2a$10$XRz2ew7AX.StHRF.XptumuXSSSHiF4ylTCC5uDBO1l2z51maRnE82",
1,
100, 10, datetime('now'), null);


-- vat2_system_config_entry
insert into vat2_system_config_entry(key, value) values ("default_email_suffix", "@example.com");
insert into vat2_system_config_entry(key, value) values ("default_limit_group", "10");
insert into vat2_system_config_entry(key, value) values ("default_limit_project", "100");
insert into vat2_system_config_entry(key, value) values ("default_user_password", "123456");