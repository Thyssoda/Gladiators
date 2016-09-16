package com.thyssoda.gladiators.Game;

public enum GladiatorsState {

	WAIT, LOBBY, GAMEPVP;

	private static GladiatorsState currentState;

	public static void setState(GladiatorsState state) {
		GladiatorsState.currentState = state;
	}

	public static boolean isState(GladiatorsState state) {
		return GladiatorsState.currentState == state;
	}

	public static GladiatorsState getState() {
		return currentState;
	}

}
