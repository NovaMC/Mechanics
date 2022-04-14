package xyz.novaserver.mechanics.features.proxy_cmd;

import xyz.novaserver.mechanics.NovaMechanics;
import xyz.novaserver.mechanics.features.Feature;

public class ProxyCmdFeature implements Feature {
    @Override
    public void register(NovaMechanics mechanics) {
        mechanics.getCommand("runproxycmd").setExecutor(new SendProxyCommand(mechanics));
    }
}
