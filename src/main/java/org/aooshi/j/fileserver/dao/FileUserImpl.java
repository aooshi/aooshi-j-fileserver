package org.aooshi.j.fileserver.dao;

import org.aooshi.j.fileserver.domain.User;
import org.aooshi.j.fileserver.util.JsonUserConfiguration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FileUserImpl implements IUser {

	@Override
	public User findUserByUid(int uid) {
		return JsonUserConfiguration.singleton.getUser(uid);
	}

}
