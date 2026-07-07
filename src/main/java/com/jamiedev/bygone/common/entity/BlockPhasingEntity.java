package com.jamiedev.bygone.common.entity;

public interface BlockPhasingEntity {

	boolean isPhasing();
	boolean canStartPhasing();
	void startPhasing();
	void stopPhasing();
	void tickPhasing();
	int getPhasingTicks();
	int getMaxPhasingTicks();

}
