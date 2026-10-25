package com.vibe.client;

import com.vibe.client.modules.*;
import java.util.List;

public class ModuleManager {
    private final List<Module> modules = List.of(
        new Freecam(),
        new StashFinder(),
        new PlayerActivity()
    );

    public List<Module> all() { return modules; }

    public List<Module> inCategory(Category c) {
        return modules.stream().filter(m -> m.category == c).toList();
    }
}
