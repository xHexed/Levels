package com.grassminevn.levels.jskills.elo;

import com.grassminevn.levels.jskills.GameInfo;

public class GaussianKFactor extends KFactor {
    // From paper
    static final double StableDynamicsKFactor = 24.0;

    public GaussianKFactor() { super(StableDynamicsKFactor); }

    public GaussianKFactor(final GameInfo gameInfo, final double latestGameWeightingFactor) {
        super(latestGameWeightingFactor * gameInfo.getBeta()
                * Math.sqrt(Math.PI));
    }
}