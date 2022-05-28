package xyz.novaserver.mechanics.features;

import xyz.novaserver.mechanics.NovaMechanics;

public interface EarlyLoadable {
    void onLoad(NovaMechanics novaMechanics);
}
