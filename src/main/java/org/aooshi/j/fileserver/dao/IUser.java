package org.aooshi.j.fileserver.dao;

import org.aooshi.j.fileserver.domain.User;

public interface IUser {
	
    User findUserByUid(int uid);
}
