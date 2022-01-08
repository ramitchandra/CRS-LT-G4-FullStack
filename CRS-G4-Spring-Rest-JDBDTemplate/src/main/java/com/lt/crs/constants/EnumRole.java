package com.lt.crs.constants;

public enum EnumRole {

	Student(103),Professor(102),Admin(101),Role(0);

	private int roleId;
	
	private EnumRole(int roleId) {
		this.roleId=roleId;
	}
	
	public int getRoleId() {
		return roleId;
	}
}
