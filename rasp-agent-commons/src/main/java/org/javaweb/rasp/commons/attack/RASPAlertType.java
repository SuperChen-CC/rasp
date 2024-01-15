package org.javaweb.rasp.commons.attack;

public enum RASPAlertType {

	TEST(0), ALARM(1), VUL(2), ATTACK(3), RULES(4);

	private final int value;

	RASPAlertType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
