package com.milestone.tools;

import com.milestone.entity.Administrator;

public class Cache {
	private Administrator user;

	private Cache() {

	}

	private static class CacheHolder {
		private static final Cache INSTANCE = new Cache();
	}

	public Cache getInstance() {
		return CacheHolder.INSTANCE;
	}

	public Administrator getUser() {
		return user;
	}

	public void setUser(Administrator user) {
		this.user = user;
	}

}
